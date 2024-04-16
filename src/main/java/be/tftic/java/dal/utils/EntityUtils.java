package be.tftic.java.dal.utils;

import be.tftic.java.dal.orm.ClassMapping;
import be.tftic.java.dal.orm.ORMLoader;
import be.tftic.java.dal.dao.DBExtractor;
import be.tftic.java.dal.orm.annotation.relation.ManyToMany;
import be.tftic.java.dal.orm.annotation.relation.ManyToOne;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.annotation.relation.OneToMany;
import be.tftic.java.dal.orm.annotation.relation.OneToOne;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class EntityUtils {

    public static <T> Optional<T> getReferenceFor(Object entity, String fieldName){
        Class<Object> entityClass = (Class<Object>) entity.getClass();
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            if( field.isAnnotationPresent(OneToOne.class) ){
                OneToOne oneToOne = field.getAnnotation(OneToOne.class);
                if( oneToOne.strong() ){
                    return getOneToOneStrongRef(entity, field, oneToOne);
                }
                else {
                    return getOneToOneWeakRef(entity, field, oneToOne);
                }
            }
            else if (field.isAnnotationPresent(ManyToOne.class)){
                return getManyToOneRef(entity, field, field.getAnnotation(ManyToOne.class));
            }
            else {
                throw new RuntimeException("No reference can fe fetch through relation");
            }
        }
        catch (NoSuchFieldException ex){
            throw new RuntimeException(STR."No field: \{fieldName} on class \{entityClass.getName()}");
        }
    }

    private static <T> Optional<T> getOneToOneStrongRef(Object entity, Field field, OneToOne annotation){
        Class<Object> entityClass = (Class<Object>) entity.getClass();
        if (annotation == null || !annotation.strong() || !entityClass.isAnnotationPresent(Table.class))
            throw new IllegalArgumentException("Invalid field");

        String configName = entityClass.getAnnotation(Table.class).configName();
        String joinColumnName = field.getAnnotation(OneToOne.class).joinColumnName();
        ClassMapping<Object> referencerMapping = ORMLoader.getLoader(configName).getClassMapping((Class<Object>) field.getDeclaringClass());
        ClassMapping<Object> referencedMapping = ORMLoader.getLoader(configName).getClassMapping((Class<Object>) field.getType());
        var idMapping = referencerMapping.getIdFieldMapping();
        var id = idMapping.get(entity);

        List<String> colNames = referencedMapping.getFieldMappings().stream()
                .filter(fieldMapping -> !fieldMapping.isRelation())
                .map(ClassMapping.FieldMapping::getColumnName)
                .toList();

        String referencerQuery = STR."""
                                 SELECT \{String.join(",", colNames)}
                                 FROM \{referencerMapping.geTableName()} as referencer
                                    JOIN \{referencedMapping.geTableName()} as referenced
                                        ON referencer.\{joinColumnName} = referenced."\{referencedMapping.getIdFieldMapping().getColumnName()}"
                                 WHERE referencer.\{joinColumnName} = ?
                                 """;

        return DBRegistry.getQueryCreator(configName).select(referencerQuery, (DBExtractor<T>) referencedMapping)
                .withParam(1, id)
                .fetchOne();

    }

    private static <T> Optional<T> getOneToOneWeakRef(Object entity, Field field, OneToOne annotation){
        Class<Object> entityClass = (Class<Object>) entity.getClass();
        if (annotation == null || annotation.strong() || !entityClass.isAnnotationPresent(Table.class))
            throw new IllegalArgumentException("Invalid field");

        String configName = entityClass.getAnnotation(Table.class).configName();
        String joinColumnName = field.getAnnotation(OneToOne.class).joinColumnName();
        ClassMapping<Object> referencerMapping = ORMLoader.getLoader(configName).getClassMapping((Class<Object>) field.getType());
        ClassMapping<Object> referencedMapping = ORMLoader.getLoader(configName).getClassMapping((Class<Object>) field.getDeclaringClass());
        var idMapping = referencedMapping.getIdFieldMapping();
        var id = idMapping.get(entity);

        List<String> colNames = referencerMapping.getFieldMappings().stream()
                .filter(fieldMapping -> !fieldMapping.isRelation())
                .map(ClassMapping.FieldMapping::getColumnName)
                .toList();

        String referencerQuery = STR."""
                                 SELECT \{String.join(",", colNames)}
                                 FROM \{referencerMapping.geTableName()} as referencer
                                    JOIN \{referencedMapping.geTableName()} as referenced
                                        ON referencer.\{joinColumnName} = referenced."\{referencedMapping.getIdFieldMapping().getColumnName()}"
                                 WHERE referencer.\{joinColumnName} = ?
                                 """;

        return DBRegistry.getQueryCreator(configName).select(referencerQuery, (DBExtractor<T>) referencerMapping)
                .withParam(1, id)
                .fetchOne();

    }

    private static <T> Optional<T> getManyToOneRef(Object entity, Field field, ManyToOne annotation){
        Class<Object> entityClass = (Class<Object>) entity.getClass();
        if(!entityClass.isAnnotationPresent(Table.class))
            throw new IllegalArgumentException("Invalid field");

        String configName = entityClass.getAnnotation(Table.class).configName();
        String joinColumnName = annotation.joinColumnName();
        ClassMapping<Object> referencerMapping = ORMLoader.getLoader(configName).getClassMapping((Class<Object>) field.getDeclaringClass());
        ClassMapping<Object> referencedMapping = ORMLoader.getLoader(configName).getClassMapping((Class<Object>) field.getType());
        var idMapping = referencerMapping.getIdFieldMapping();
        var id = idMapping.get(entity);

        List<String> colNames = referencedMapping.getFieldMappings().stream()
                .filter(fieldMapping -> !fieldMapping.isRelation())
                .map(ClassMapping.FieldMapping::getColumnName)
                .toList();

        String referencerQuery = STR."""
                                 SELECT \{String.join(",", colNames)}
                                 FROM \{referencerMapping.geTableName()} as referencer
                                    JOIN \{referencedMapping.geTableName()} as referenced
                                        ON referencer.\{joinColumnName} = referenced."\{referencedMapping.getIdFieldMapping().getColumnName()}"
                                 WHERE referencer.\{idMapping.getColumnName()} = ?
                                 """;

        return DBRegistry.getQueryCreator(configName).select(referencerQuery, (DBExtractor<T>) referencedMapping)
                .withParam(1, id)
                .fetchOne();
    }

    public static <T> List<T> getReferencesFor(Object entity, String fieldName){
        Class<Object> entityClass = (Class<Object>) entity.getClass();
        try{
            Field field = entityClass.getDeclaredField(fieldName);
            if( field.isAnnotationPresent(OneToMany.class) ){
                return getOneToManyRefs(entity, field, field.getAnnotation(OneToMany.class));
            }
            else if( field.isAnnotationPresent(ManyToMany.class) ){
                throw new RuntimeException("not implemented yet");
            }
            else {
                throw new RuntimeException("No reference can fe fetch through relation");
            }
        }
        catch (NoSuchFieldException ex){
            throw new RuntimeException(STR."No field: \{fieldName} on class \{entityClass.getName()}");
        }
    }

    private static <T> List<T> getOneToManyRefs(Object entity, Field field, OneToMany annotation){
        Class<Object> entityClass = (Class<Object>) entity.getClass();
        if(!entityClass.isAnnotationPresent(Table.class))
            throw new IllegalArgumentException("Invalid field");

        String configName = entityClass.getAnnotation(Table.class).configName();
        String joinColumnName = annotation.referenceColumn();
        ClassMapping<Object> referencerMapping = ORMLoader.getLoader(configName).getClassMapping((Class<Object>) annotation.referencedBy());
        ClassMapping<Object> referencedMapping = ORMLoader.getLoader(configName).getClassMapping(entityClass);
        var idMapping = referencedMapping.getIdFieldMapping();
        var id = idMapping.get(entity);

        List<String> colNames = referencerMapping.getFieldMappings().stream()
                .filter(fieldMapping -> !fieldMapping.isRelation())
                .map(ClassMapping.FieldMapping::getColumnName)
                .toList();

        String referencerQuery = STR."""
                                 SELECT \{String.join(",", colNames)}
                                 FROM \{referencerMapping.geTableName()} as referencer
                                    JOIN \{referencedMapping.geTableName()} as referenced
                                        ON referencer.\{joinColumnName} = referenced."\{referencedMapping.getIdFieldMapping().getColumnName()}"
                                 WHERE referencer.\{joinColumnName} = ?
                                 """;

        return DBRegistry.getQueryCreator(configName).select(referencerQuery, (DBExtractor<T>) referencerMapping)
                .withParam(1, id)
                .fetchList();
    }

}
