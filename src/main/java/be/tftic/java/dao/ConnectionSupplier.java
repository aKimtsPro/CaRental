package be.tftic.java.dao;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSupplier {
    Connection supplyConnection() throws SQLException;
}
