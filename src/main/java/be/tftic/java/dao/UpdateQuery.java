package be.tftic.java.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;

public class UpdateQuery extends Query {

    UpdateQuery(String query, ConnectionSupplier coSupplier) {
        super(query, coSupplier);
    }

    @Override
    public UpdateQuery withParam(int position, Object value) {
        return (UpdateQuery) super.withParam(position, value);
    }

    @Override
    public UpdateQuery withParams(Map<Integer, Object> params) {
        return (UpdateQuery) super.withParams(params);
    }

    public int execute(){
        try(
                Connection co = getCoSupplier().supplyConnection();
                PreparedStatement stmt = co.prepareStatement(getQuery());
        ) {
            injectParams(stmt);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
