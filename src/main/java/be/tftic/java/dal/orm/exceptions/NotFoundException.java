package be.tftic.java.dal.orm.exceptions;

public class NotFoundException {
    private final Class<?> entityClass;
    private final Object findByValue;
    private final String findBy;

    public NotFoundException(Class<?> entityClass, Object findByValue, String findBy) {
        this.entityClass = entityClass;
        this.findByValue = findByValue;
        this.findBy = findBy;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Object getFindByValue() {
        return findByValue;
    }

    public String getFindBy() {
        return findBy;
    }
}
