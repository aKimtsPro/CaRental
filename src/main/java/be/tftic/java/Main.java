package be.tftic.java;

import be.tftic.java.entity.Role;
import be.tftic.java.orm.ORMLoader;
import be.tftic.java.orm.repository.IRepository;

public class Main {
    public static void main(String[] args) {
        System.out.println("test");
        ORMLoader ormLoader = ORMLoader.getLoader("carental");
        IRepository<Role, Long> repo = ormLoader.getRepo(Role.class);
        repo.getAll().forEach(System.out::println);
    }

//    private static Reflections reflections = new Reflections(Main.class.getPackage());

//    public static Reflections getReflections(){
//        return reflections;
//    }
}
