package be.tftic.java.dal.orm.repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T, ID> {

    List<T> getAll();
    Optional<T> getOne(ID id);
    Optional<T> getOneBy(String column, Object value);


    T insert(T toInsert);

    void delete(ID deleteID);

    T update(T toUpdate);

    Class<T> getEntityClass();


}
