package be.tftic.java.orm;

import be.tftic.java.Main;
import be.tftic.java.entity.Role;
import be.tftic.java.orm.annotations.Table;
import be.tftic.java.orm.repository.IRepository;
import be.tftic.java.orm.repository.RepositoryImpl;
import be.tftic.java.utils.DBRegistry;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ORMLoader {

    private static final Map<String, ORMLoader> lazyLoaders = new HashMap<>();
    public static ORMLoader getLoader(String configName) {
        ORMLoader loader = lazyLoaders.get(configName);

        if( loader == null ){
            loader = new ORMLoader(configName);
            lazyLoaders.put(configName, loader);
        }
        return loader;
    }

    private final Map<Class<?>, IRepository<?, ?>> lazyRepos = new HashMap<>();
    private final Map<Class<?>, ClassMapping<?>> mappings;
    private final DBRegistry.Config config;

    public ORMLoader(String configName) {
        this.config = DBRegistry.getConfig(configName);
        this.mappings = initMappings();
    }

    private Map<Class<?>, ClassMapping<?>> initMappings(){
//        Reflections reflections = Main.getReflections();


//        return reflections.get(
//                Scanners.SubTypes.of(Scanners.TypesAnnotated.with(Table.class)).asClass()
//        )
        return List.of(Role.class)
                .stream()
                .collect(
                    HashMap::new,
                    (target, next) -> target.put(next, new ClassMapping<>(this, next)),
                    HashMap::putAll
                );
    }

    public <T> ClassMapping<T> getClassMapping(Class<T> clazz) {
        return (ClassMapping<T>) this.mappings.get(clazz);
    }

    public <T,ID> IRepository<T, ID> getRepo(Class<T> entityClass){
        IRepository<T,ID> repo = (IRepository<T,ID>)lazyRepos.get(entityClass);
        if( repo == null ){
            repo = new RepositoryImpl<>(config, entityClass, getClassMapping(entityClass));
            lazyRepos.put(entityClass, repo);
        }
        return repo;
    }

}
