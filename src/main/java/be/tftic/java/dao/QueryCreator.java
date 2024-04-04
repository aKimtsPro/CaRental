package be.tftic.java.dao;

public class QueryCreator {

    private final ConnectionSupplier coSupplier;

    public QueryCreator(ConnectionSupplier coSupplier) {
        this.coSupplier = coSupplier;
    }

    public <T> SelectQuery<T> select(String query, DBExtractor<T> extractor){
        return new SelectQuery<>(query, coSupplier, extractor);
    }

    public UpdateQuery update(String query){
        return new UpdateQuery(query, coSupplier);
    }
}
