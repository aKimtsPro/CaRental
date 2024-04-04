package be.tftic.java.orm.repository;

import be.tftic.java.dao.QueryCreator;
import be.tftic.java.orm.ClassMapping;
import be.tftic.java.orm.annotations.Column;
import be.tftic.java.orm.annotations.Table;
import be.tftic.java.utils.DBRegistry;

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
                .stream().map(fMapping -> STR."\"\{fMapping.getColumnName()}\"")
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

        Collection<ClassMapping<T>.FieldMapping> fieldMappings = mapping.getFieldMappings().stream()
                .filter(fMapping -> !fMapping.isId())
                .toList();


        int pos = 1;
        Map<Integer, Object> params = new HashMap<>();
        for (ClassMapping<T>.FieldMapping fieldMapping : fieldMappings) {
            if( !fieldMapping.isId() )
                params.put(pos++, fieldMapping.get(toInsert));
        }

        String columnNames = fieldMappings.stream()
                .map(fieldMapping -> STR."\"\{fieldMapping.getColumnName()}\"")
                .collect(Collectors.joining(","));

        String paramMarks = fieldMappings.stream()
                .map((_) -> "?")
                .collect(Collectors.joining(","));


        String query = STR."""
                           INSERT INTO \{mapping.geTableName()}(\{columnNames})
                           VALUES (\{paramMarks})
                           RETURNING "\{mapping.getIdColumnName()}" as inserted_id
                           """;

        ID id = queryCreator.select(query, rs -> (ID) rs.getObject("inserted_id"))
                .withParams(params)
                .fetchOne()
                .orElseThrow();

        return getOne(id).orElseThrow();
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

}
