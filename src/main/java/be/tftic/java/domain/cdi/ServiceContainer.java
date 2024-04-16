package be.tftic.java.domain.cdi;

import be.tftic.java.Main;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;

public class ServiceContainer {

    private static ServiceContainer instance;
    private static ServiceContainer getInstance() {
        return instance == null ? instance = new ServiceContainer() : instance;
    }

    public static <T> T getInstance(Class<T> instanceClass){
        return getInstance().get(instanceClass);
    }

    private final Map<Class<?>, Object> instances = new HashMap<>();

    private ServiceContainer() {
        loadServices();
    }

    public void loadServices() {
        Collection<Class<?>> classes = findServices().stream()
                .sorted((c1, c2) -> {
                    try {
                        return c1.getConstructor().getParameterCount() - c2.getConstructor().getParameterCount();
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toList();

        for (Class<?> clazz : classes){
            try {
                Constructor<?> ctor = clazz.getConstructor();

                Parameter[] params =  ctor.getParameters();
                if( params.length == 0 ){
                    instances.put(clazz, ctor.newInstance());
                }
                else {
                    boolean paramsOk = Arrays.stream( params )
                            .allMatch(param -> instances.containsKey( param.getType() ));

                    if( paramsOk ){
                        Object[] dependencies = Arrays.stream( params )
                                .map(param -> instances.get(param.getType()))
                                .toArray();

                        instances.put( clazz, ctor.newInstance(dependencies) );
                    }
                }
            }
            catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }

        }
    }

    private <T> T get(Class<T> clazz ){
        return (T) instances.get(clazz);
    }


    private Set<Class<?>> findServices() {
        Reflections reflections = Main.getReflections();
        return reflections.get(
                Scanners.SubTypes.of(Scanners.TypesAnnotated.with(ToInstanciate.class)).asClass()
        );
    }

}
