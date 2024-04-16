package be.tftic.java.dal.orm;

import be.tftic.java.Main;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.repository.IRepository;
import be.tftic.java.dal.orm.repository.RepositoryImpl;
import be.tftic.java.dal.utils.DBRegistry;
import be.tftic.java.domain.cdi.Repository;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.util.HashMap;
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

    private final Map<Class<?>, IRepository<?, ?>> repos;
    private final Map<Class<?>, ClassMapping<?>> mappings;
    private final DBRegistry.Config config;

    public ORMLoader(String configName) {
        this.config = DBRegistry.getConfig(configName);
        this.mappings = initMappings();
        this.repos = initRepos();
    }

    private Map<Class<?>, ClassMapping<?>> initMappings(){
        Reflections reflections = Main.getReflections();

        return reflections.get(
                Scanners.SubTypes.of(Scanners.TypesAnnotated.with(Table.class)).asClass()
        )
                .stream()
                .collect(
                    HashMap::new,
                    (target, next) -> target.put(next, new ClassMapping<>(this, next)),
                    HashMap::putAll
                );
    }

    private Map<Class<?>, IRepository<?, ?>> initRepos(){
        Reflections reflections = Main.getReflections();
        return reflections.get(
                        Scanners.SubTypes.of(Scanners.TypesAnnotated.with(Repository.class)).asClass()
                )
                .stream()
                .collect(
                        HashMap::new,
                        (target, next) -> {
                            try {
                                Class<?> entityClass = next.getAnnotation(Repository.class).entityClass();
                                IRepository<?, ?> repo = (IRepository<?, ?>) next.getConstructor(ClassMapping.class)
                                        .newInstance(getClassMapping(entityClass));
                                target.put(entityClass, repo);
                            } catch (ReflectiveOperationException e) {
                                throw new RuntimeException(e);
                            }
                        },
                        HashMap::putAll
                );
    }

    public <T> ClassMapping<T> getClassMapping(Class<T> clazz) {
        return (ClassMapping<T>) this.mappings.get(clazz);
    }

    public <T,ID> IRepository<T, ID> getRepo(Class<T> entityClass){
        IRepository<T,ID> repo = (IRepository<T,ID>) repos.get(entityClass);
        if( repo == null ){
            repo = new RepositoryImpl<>(config, entityClass, getClassMapping(entityClass));
            repos.put(entityClass, repo);
        }
        return repo;
    }
}
