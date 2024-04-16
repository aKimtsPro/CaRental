package be.tftic.java.domain.models.entity;

import be.tftic.java.dal.orm.annotation.Column;
import be.tftic.java.dal.orm.annotation.Id;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.annotation.relation.OneToMany;
import be.tftic.java.dal.utils.EntityUtils;

import java.util.List;

@Table(name="\"Role\"", configName = "carental")
public class Role {

    @Id
    @Column(name="role_id")
    private long id;
    @Column(name = "role_name")
    private String name;
    @Column(name = "role_description")
    private String description;

    @OneToMany(referencedBy = User.class, referenceColumn = "user_role_id")
    private List<User> users;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<User> getUsers() {
        if( this.users == null ){
            this.users = EntityUtils.getReferencesFor(this, "users");
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
