package be.tftic.java.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public abstract class Query {

    private final String query;
    private final ConnectionSupplier coSupplier;
    private final Map<Integer, Object> params = new HashMap<>();

    public Query(String query, ConnectionSupplier coSupplier) {
        this.query = query;
        this.coSupplier = coSupplier;
    }

    public Query withParam(int position, Object value){
        params.put(position, value);
        return this;
    }

    public Query withParams(Map<Integer, Object> params) {
        Query qthis = this;
        for (Integer i : params.keySet()) {
            qthis = qthis.withParam(i, params.get(i));
        }
        return qthis;
    }

    public String getQuery() {
        return query;
    }

    protected ConnectionSupplier getCoSupplier() {
        return coSupplier;
    }

    protected Map<Integer, Object> getParams() {
        return params;
    }

    protected void injectParams(PreparedStatement stmt) throws SQLException {
        for (Integer position : params.keySet()) {
            stmt.setObject(position, params.get(position));
        }
    }
}
