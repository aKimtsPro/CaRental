package be.tftic.java.bll.exceptions;

public class NotFoundException extends RuntimeException {
    private final Class<?> entityClass;
    private final Object findByValue;
    private final String findBy;

    public NotFoundException(Class<?> entityClass, Object findByValue, String findBy) {
        this.entityClass = entityClass;
        this.findBy = findBy;
        this.findByValue = findByValue;
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
