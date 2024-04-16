package be.tftic.java.bll.impl;

import be.tftic.java.bll.AbstractCrudService;
import be.tftic.java.dal.orm.ORMLoader;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.domain.cdi.ToInstanciate;
import be.tftic.java.domain.models.entity.Role;

@ToInstanciate("roleService")
public class RoleService extends AbstractCrudService<Role, Long> {
    public RoleService() {
        String configName = Role.class.getAnnotation(Table.class).configName();
        super(ORMLoader.getLoader(configName).getRepo(Role.class), Role.class);
    }
}
