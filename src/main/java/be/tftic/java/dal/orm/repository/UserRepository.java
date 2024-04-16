package be.tftic.java.dal.orm.repository;

import be.tftic.java.dal.orm.ClassMapping;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.domain.cdi.Repository;
import be.tftic.java.domain.models.entity.User;

import java.util.Optional;

@Repository(entityClass = User.class)
public class UserRepository extends RepositoryImpl<User, Long> {

    public UserRepository(ClassMapping<User> userClassMapping) {
        String configName = User.class.getAnnotation(Table.class).configName();
        super(configName, User.class, userClassMapping);
    }

    public Optional<User> getByUsername(String username) {
        String query = "SELECT * FROM \"User\" WHERE user_name = ?";
        return getQueryCreator().select(query, getMapping())
                .withParam(1, username)
                .fetchOne();
    }
}
