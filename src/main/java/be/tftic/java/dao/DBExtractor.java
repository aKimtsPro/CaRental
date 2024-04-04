package be.tftic.java.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface DBExtractor<T> {
    T extract(ResultSet rs) throws SQLException;
}
