package be.tftic.java.dal.orm.repository;

import be.tftic.java.dal.dao.QueryCreator;
import be.tftic.java.dal.orm.ClassMapping;
import be.tftic.java.dal.orm.ORMLoader;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.annotation.relation.ManyToMany;
import be.tftic.java.dal.orm.annotation.relation.ManyToOne;
import be.tftic.java.dal.orm.annotation.relation.OneToOne;
import be.tftic.java.dal.utils.DBRegistry;

import java.util.*;
import java.util.stream.Collectors;

public class RepositoryImpl<T, ID> implements IRepository<T, ID> {

    private final DBRegistry.Config config;
    private final QueryCreator queryCreator;
    private final Class<T> entityClass;
    private final ClassMapping<T> mapping;

    public RepositoryImpl(String configName, Class<T> entityClass, ClassMapping<T> mapping) {
        this(DBRegistry.getConfig(configName), entityClass, mapping);
    }

    public RepositoryImpl(DBRegistry.Config config, Class<T> entityClass, ClassMapping<T> mapping){
        this.mapping = mapping;
        if( !entityClass.isAnnotationPresent(Table.class) )
            throw new RuntimeException("Invalid class for Repo");

        this.entityClass = entityClass;
        this.config = config;
        this.queryCreator = DBRegistry.getQueryCreator(config.getName());

    }

    @Override
    public List<T> getAll() {
        String tableName = entityClass.getAnnotation(Table.class).name();
        String columnNames = mapping.getFieldMappings().stream()
                .filter(fMapping -> !fMapping.isRelation())
                .map((fieldMapping) -> STR."\"\{fieldMapping.getColumnName()}\"")
                .collect(Collectors.joining(","));

        String query = STR."""
                           SELECT \{columnNames}
                           FROM \{tableName}
                           """;

        return queryCreator.select(query, mapping)
                .fetchList();
    }

    @Override
    public Optional<T> getOne(ID id) {
        String tableName = mapping.geTableName();
        String columnNames = mapping.getFieldMappings()
                .stream()
                .filter(fMapping -> !fMapping.isRelation())
                .map(fMapping -> STR."\"\{fMapping.getColumnName()}\"")
                .collect(Collectors.joining(","));
        String idColumnName = mapping.getIdColumnName();

        String query = STR."""
                           SELECT \{columnNames}
                           FROM \{tableName}
                           WHERE \{idColumnName} = ?
                           """;

        return queryCreator.select(query, mapping)
                .withParam(1, id)
                .fetchOne();
    }

    @Override
    public T insert(T toInsert) {
        ORMLoader ormLoader = ORMLoader.getLoader(config.getName());

        Collection<ClassMapping<T>.FieldMapping> fieldMappings = mapping.getFieldMappings().stream()
                .filter(fMapping -> !fMapping.isId())
                .toList();


        int pos = 1;
        Map<Integer, Object> params = new HashMap<>();
        List<String> columnNames = new LinkedList<>();

        List<ClassMapping<T>.FieldMapping> nnRelationMappings = new LinkedList<>();
        for (ClassMapping<T>.FieldMapping fieldMapping : fieldMappings) {
            if( !fieldMapping.isId() ) {
                Object value = null;
                String columnName = null;
                if(
                        fieldMapping.isRelation() &&
                        (
                                fieldMapping.getRelationType().equals(ManyToOne.class) ||
                                fieldMapping.getRelationType().equals(OneToOne.class)
                        )
                ) {
                    if( fieldMapping.getRelationType().equals(ManyToOne.class) ){
                        String joinColumnName = fieldMapping.getField().getAnnotation(ManyToOne.class).joinColumnName();
                        Class<?> referencedType = fieldMapping.getField().getType();
                        value = ormLoader.getClassMapping(referencedType)
                                .getIdFieldMapping()
                                .get( fieldMapping.get(toInsert) );
                        columnName = joinColumnName;
                    }
                    else if (fieldMapping.getRelationType().equals(OneToOne.class)){
                        OneToOne annotation = fieldMapping.getField().getAnnotation(OneToOne.class);
                        if( annotation.strong() ){
                            String joinColumnName = annotation.joinColumnName();
                            Class<?> referencedType = fieldMapping.getField().getType();
                            value = ormLoader.getClassMapping(referencedType)
                                    .getIdFieldMapping()
                                    .get( fieldMapping.get(toInsert) );
                            columnName = joinColumnName;
                        }
                    }
                    else if (fieldMapping.getRelationType().equals(ManyToMany.class)){
                        nnRelationMappings.add(fieldMapping);
                    }
                }
                else if( !fieldMapping.isRelation() ) {
                    value = fieldMapping.get(toInsert);
                    columnName = fieldMapping.getColumnName();
                }

                if( columnName != null ){
                    columnNames.add(columnName);
                    params.put(pos++, value);
                }
            }
        }

        String joinedColNames = columnNames.stream()
                .map(colName -> STR."\"\{colName}\"")
                .collect(Collectors.joining(","));

        String paramMarks = columnNames.stream()
                .map((_) -> "?")
                .collect(Collectors.joining(","));


        String query = STR."""
                           INSERT INTO \{mapping.geTableName()}(\{joinedColNames})
                           VALUES (\{paramMarks})
                           RETURNING "\{mapping.getIdColumnName()}" as inserted_id
                           """;

        ID id = queryCreator.select(query, rs -> (ID) rs.getObject("inserted_id"))
                .withParams(params)
                .fetchOne()
                .orElseThrow();

        if( nnRelationMappings.size() > 0 ){
            for (ClassMapping<T>.FieldMapping nnRelationMapping : nnRelationMappings) {
                ManyToMany annotation = nnRelationMapping.getField().getAnnotation(ManyToMany.class);

                Collection<?> referencedValues = nnRelationMapping.get(toInsert);
                List<String> sqlValues = new LinkedList<>();
                Map<Integer, Object> referencedIdParams = new HashMap<>();

                int position = 1;
                for (Object value : sqlValues) {
                    sqlValues.add(STR."(\{id instanceof String ? STR."'\{id}'" : id},?)");
                    Class<Object> referencedType = (Class<Object>) value.getClass();
                    ClassMapping<Object> referencedMapping = ormLoader.getClassMapping(referencedType);
                    Object referencedId = referencedMapping.getIdFieldMapping().get(value);
                    referencedIdParams.put(position++, referencedId);
                }

                String subQuery = STR."""
                                      INSERT INTO "\{annotation.joinTable()}" (\{annotation.joinColumn()}, \{annotation.inverseJoinColumn()})
                                      VALUES
                                      \{sqlValues.stream().collect(Collectors.joining(","))}
                                      """;

                queryCreator.update(subQuery)
                        .withParams(referencedIdParams)
                        .execute();
            }
        }
        mapping.getIdFieldMapping().set(toInsert, id);

        return toInsert;
    }

    @Override
    public void delete(ID deleteID) {
        String tableName = mapping.geTableName();
        String idColumnName = mapping.getIdColumnName();

        String query = STR."""
                           DELETE FROM \{tableName}
                           WHERE \{idColumnName} = ?
                           """;

        queryCreator.update(query)
                .withParam(1, deleteID)
                .execute();
    }

    @Override
    public T update(T toUpdate) {
        String tableName = mapping.geTableName();
        Collection<ClassMapping<T>.FieldMapping> fieldMappings = mapping.getFieldMappings().stream()
                .filter(fieldMapping -> !fieldMapping.isId())
                .toList();
        int pos = 1;
        Map<Integer, Object> params = new HashMap<>();
        for (ClassMapping<T>.FieldMapping fieldMapping : fieldMappings) {
            params.put(pos++, fieldMapping.get(toUpdate));
        }

        ID id = mapping.getIdFieldMapping().get(toUpdate);
        params.put(pos++, id);

        String columnSetters = fieldMappings.stream()
                .map(fMapping -> STR."\"\{fMapping.getColumnName()}\" = ?")
                .collect(Collectors.joining(","));

        String idColumnName = mapping.getIdColumnName();

        String query = STR."""
                           UPDATE \{tableName}
                           SET \{columnSetters}
                           WHERE \{idColumnName} = ?
                           """;

        queryCreator.update(query)
                .withParams(params)
                .execute();

        return getOne(id).orElseThrow();
    }

    @Override
    public Optional<T> getOneBy(String column, Object value) {
        String tableName = mapping.geTableName();
        String columnNames = mapping.getFieldMappings()
                .stream().map(fMapping -> STR."\"\{fMapping.getColumnName()}\"")
                .collect(Collectors.joining(","));

        String query = STR."""
                           SELECT \{columnNames}
                           FROM \{tableName}
                           WHERE \{column} = ?
                           """;

        return queryCreator.select(query, mapping)
                .withParam(1, value)
                .fetchOne();
    }

    protected DBRegistry.Config getConfig() {
        return config;
    }

    protected QueryCreator getQueryCreator() {
        return queryCreator;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    protected ClassMapping<T> getMapping() {
        return mapping;
    }
}
