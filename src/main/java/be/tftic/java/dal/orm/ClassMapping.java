package be.tftic.java.dal.orm;

import be.tftic.java.dal.dao.DBExtractor;
import be.tftic.java.dal.orm.annotation.Column;
import be.tftic.java.dal.orm.annotation.Id;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.annotation.relation.ManyToMany;
import be.tftic.java.dal.orm.annotation.relation.ManyToOne;
import be.tftic.java.dal.orm.annotation.relation.OneToMany;
import be.tftic.java.dal.orm.annotation.relation.OneToOne;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ClassMapping<T> implements DBExtractor<T>{

    private final Class<T> entityClass;
    private final Map<Field, FieldMapping> fieldMappings;
    private final Field idField;

    public ClassMapping(ORMLoader ormLoader, Class<T> entityClass) {
        this.entityClass = entityClass;
        this.fieldMappings = initFieldMappings();
        this.idField = initIdField();
    }

    private Map<Field, FieldMapping> initFieldMappings(){
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(
                        field ->
                                field.isAnnotationPresent(Column.class) ||
                                field.isAnnotationPresent(ManyToMany.class) ||
                                field.isAnnotationPresent(OneToMany.class) ||
                                field.isAnnotationPresent(OneToOne.class) ||
                                field.isAnnotationPresent(ManyToOne.class)
                )
                .collect(
                        HashMap::new,
                        (target, next) -> target.put(next,new FieldMapping(next)),
                        Map::putAll
                );
    }

    private Field initIdField(){
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class) && field.isAnnotationPresent(Column.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(STR."no @Id && @Column on class \{entityClass.getSimpleName()} fields"));
    }

    public Field getIdField() {
        return idField;
    }

    public FieldMapping getIdFieldMapping() {
        return getFieldMapping(idField);
    }

    public String geTableName(){
        return entityClass.getAnnotation(Table.class).name();
    }

    public String getColumnName(Field field){
        return getFieldMapping(field).getColumnName();
    }

    public String getIdColumnName(){
        return getFieldMapping(idField).getColumnName();
    }

    public Collection<FieldMapping> getFieldMappings(){
        return this.fieldMappings.values();
    }

    @Override
    public T extract(ResultSet rs) throws SQLException {
        try {
            T entity = entityClass.newInstance();
            for (FieldMapping fieldMapping : fieldMappings.values()) {
                if(!fieldMapping.isRelation()){
                    Object value = fieldMapping.extract(rs);
                    if( value instanceof BigDecimal bd){
                        value = bd.doubleValue();
                    }
                    fieldMapping.set(entity, value);
                }
            }
            return entity;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(STR."Could not instanciate entity \{entityClass}",e);
        }
    }

    public FieldMapping getFieldMapping(Field field){
        fieldMappings.computeIfAbsent(
                field,
                (_) -> {throw new RuntimeException("field absent from mapping");}
        );
        return fieldMappings.get(field);
    }

    public class FieldMapping {
        private final boolean isId;
        private final Class<?> relationType;
        private final Field field;
        private final Class<?> fieldType;
        private final Method getter;
        private final Method setter;
        private final String columnName;

        public FieldMapping(Field field) {
            this.field = field;
            this.isId = field.isAnnotationPresent(Id.class);
            this.relationType = initRelationType();
            this.fieldType = field.getType();
            this.getter = initGetter();
            this.setter = initSetter();
            this.columnName = isRelation() ? null : field.getAnnotation(Column.class).name();
        }

        private <U> U extract(ResultSet rs) throws SQLException {
            return (U) rs.getObject(this.field.getAnnotation(Column.class).name());
        }

        private Method initGetter(){
            String prefix;
            if( field.getType().equals(boolean.class) ){
                prefix = field.getName().startsWith("is") ? "" : "is";
            }
            else {
                prefix = "get";
            }

            String getterName = STR."\{prefix}\{(char)(field.getName().charAt(0)-32)}\{field.getName().substring(1)}";
            return getMethodByName(getterName);
        }

        private Method initSetter(){
            String setterName = STR."set\{(char)(field.getName().charAt(0)-32)}\{field.getName().substring(1)}";
            return getMethodByName(setterName, fieldType);
        }

        private Class<?> initRelationType(){
            if( field.isAnnotationPresent(OneToMany.class) )
                return OneToMany.class;
            else if (field.isAnnotationPresent(ManyToOne.class) )
                return ManyToOne.class;
            else if ( field.isAnnotationPresent(OneToOne.class) )
                return OneToOne.class;
            else if ( field.isAnnotationPresent(ManyToMany.class) )
                return ManyToMany.class;
            else
                return null;
        }

        private Method getMethodByName(String methodName, Class<?> ...params) {
            try {
                return entityClass.getMethod(methodName, params);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(STR."Field \{field.getName()} has no method \"\{methodName}\"", e);
            }
        }
        public void set(T entity, Object value){
            try {
                this.setter.invoke(entity, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(STR."Could not call setter: \{e.getMessage()}", e);
            }
        }

        public <R> R get(T entity){
            try {
                return (R) this.getter.invoke(entity);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(STR."Could not call setter: \{e.getMessage()}", e);
            }
        }

        public boolean isId() {
            return isId;
        }

        public Field getField() {
            return field;
        }

        public Class<?> getFieldType() {
            return fieldType;
        }

        public String getColumnName() {
            if( isRelation() )
                throw new RuntimeException("this mapping concerns a relation");

            return columnName;
        }

        public Class<?> getRelationType() {
            return relationType;
        }

        public boolean isRelation() {
            return this.relationType != null;
        }

    }
}
