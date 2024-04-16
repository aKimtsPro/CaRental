package be.tftic.java.bll.impl;

import be.tftic.java.bll.AbstractCrudService;
import be.tftic.java.bll.exceptions.NotFoundException;
import be.tftic.java.dal.orm.ORMLoader;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.repository.IRepository;
import be.tftic.java.dal.orm.repository.RoleRepository;
import be.tftic.java.dal.orm.repository.UserRepository;
import be.tftic.java.domain.cdi.ToInstanciate;
import be.tftic.java.domain.models.business.SignedInUser;
import be.tftic.java.domain.models.entity.Role;
import be.tftic.java.domain.models.entity.User;


@ToInstanciate("userService")
public class UserService extends AbstractCrudService<User,Long> {

    private final IRepository<Role, Long> roleRepo;

    public UserService() {
        String configName = User.class.getAnnotation(Table.class).configName();
        super(ORMLoader.getLoader(configName).getRepo(User.class), User.class);
        this.roleRepo = ORMLoader.getLoader(configName).getRepo(Role.class);
    }

    public SignedInUser signin(String username, String password){
        User user = getRepository().getByUsername(username)
                .orElseThrow(() -> new NotFoundException(User.class, username, "username"));

        if(user.getPassword().equals(password)){
            return new SignedInUser(user.getUsername(), user.getRole().getName());
        }
        throw new RuntimeException("Wrong password");
    }

    public SignedInUser signup(User user){
        Role role = getRoleRepository().getByName("USER")
                .orElseThrow(() -> new NotFoundException(Role.class, "USER", "name"));
        user.setRole(role);
        user.setActive(true);
        user = this.create(user);
        return new SignedInUser(
                user.getUsername(),
                "USER"
        );
    }

    @Override
    protected UserRepository getRepository() {
        return (UserRepository) super.getRepository();
    }

    protected RoleRepository getRoleRepository() {
        return (RoleRepository) roleRepo;
    }
}
