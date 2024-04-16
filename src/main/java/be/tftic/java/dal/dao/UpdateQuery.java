package be.tftic.java.dal.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class UpdateQuery extends Query {

    private static Logger logger = LoggerFactory.getLogger(UpdateQuery.class);
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
            logger.info(getQuery());
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
