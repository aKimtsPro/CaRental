package be.tftic.java.dal.orm.repository;

import be.tftic.java.dal.orm.ClassMapping;
import be.tftic.java.dal.orm.ORMLoader;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.domain.cdi.Repository;
import be.tftic.java.domain.models.entity.Role;

import java.util.Optional;

@Repository(entityClass = Role.class)
public class RoleRepository extends RepositoryImpl<Role,Long> {

    public RoleRepository(ClassMapping<Role> mapping) {
        Class<Role> roleClass = Role.class;
        String configName = roleClass.getAnnotation(Table.class).configName();
        super(configName, roleClass, mapping);
    }

    public Optional<Role> getByName(String name) {
        return getQueryCreator().select("SELECT * FROM \"Role\" WHERE role_name = ?", getMapping())
                .withParam(1, name)
                .fetchOne();
    }
}
