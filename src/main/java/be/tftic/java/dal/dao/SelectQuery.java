package be.tftic.java.dal.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SelectQuery<T> extends Query {

    private static Logger logger = LoggerFactory.getLogger(SelectQuery.class);
    private final DBExtractor<T> extractor;

    SelectQuery(String query, ConnectionSupplier coSupplier, DBExtractor<T> extractor) {
        super(query, coSupplier);
        this.extractor = extractor;
    }

    @Override
    public SelectQuery<T> withParam(int position, Object value) {
        return (SelectQuery<T>) super.withParam(position, value);
    }

    @Override
    public SelectQuery<T> withParams(Map<Integer, Object> params) {
        return (SelectQuery<T>) super.withParams(params);
    }

    public Optional<T> fetchOne(){
        try(
                Connection co = getCoSupplier().supplyConnection();
                PreparedStatement stmt = co.prepareStatement(getQuery());
        ) {
            injectParams(stmt);
            logger.info(getQuery());
            try(
                    ResultSet rs = stmt.executeQuery();
            ) {
                if( rs.next() ){
                    return Optional.of(extractor.extract(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<T> fetchList(){
        try(
                Connection co = getCoSupplier().supplyConnection();
                PreparedStatement stmt = co.prepareStatement(getQuery());
        ) {
            injectParams(stmt);
            logger.info(getQuery());
            try(
                    ResultSet rs = stmt.executeQuery();
            ) {
                Stream.Builder<T> streamBuilder = Stream.builder();
                while( rs.next() ){
                    streamBuilder.add(extractor.extract(rs));
                }
                return streamBuilder.build()
                        .toList();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
