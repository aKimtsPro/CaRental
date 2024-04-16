package be.tftic.java.bll;

import java.util.List;

public interface ICrudService<T, ID> {

    T getOne(ID id);
    List<T> getAll();

    T create(T t);

    T update(T t);

    T delete(ID id);

}
