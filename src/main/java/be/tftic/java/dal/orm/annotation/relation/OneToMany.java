package be.tftic.java.dal.orm.annotation.relation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OneToMany {
    Class<?> referencedBy();
    String referenceColumn();
}
