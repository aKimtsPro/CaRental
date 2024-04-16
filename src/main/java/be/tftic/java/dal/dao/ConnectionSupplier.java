package be.tftic.java.dal.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSupplier {
    Connection supplyConnection() throws SQLException;
}
