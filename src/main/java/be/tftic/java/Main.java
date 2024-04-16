package be.tftic.java;

import be.tftic.java.domain.models.entity.Role;
import be.tftic.java.domain.models.entity.User;
import be.tftic.java.dal.orm.ORMLoader;
import be.tftic.java.dal.orm.repository.IRepository;
import org.reflections.Reflections;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("test");
        ORMLoader ormLoader = ORMLoader.getLoader("carental");

        Role role = new Role();
        role.setName("mon role");

        role = ormLoader.getRepo(Role.class).insert(role);

        User user = new User();
        user.setUsername("user");
        user.setPassword("pass");
        user.setRole(role);

        IRepository<User,Long> userRepo = ormLoader.getRepo(User.class);

        User create = userRepo.insert(user);
        Role userRole = create.getRole();

        List<User> users = userRole.getUsers();
        System.out.println("fin");
    }

    private static Reflections reflections;

    public static Reflections getReflections(){
        return reflections == null ? reflections = new Reflections(Main.class.getPackageName()) : reflections;
    }
}
