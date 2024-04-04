package be.tftic.java.orm.repository;

import be.tftic.java.orm.ClassMapping;

import javax.swing.text.html.Option;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IRepository<T, ID> {

    List<T> getAll();
    Optional<T> getOne(ID id);


    T insert(T toInsert);

    void delete(ID deleteID);

    T update(T toUpdate);


}
