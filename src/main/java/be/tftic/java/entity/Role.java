package be.tftic.java.entity;

import be.tftic.java.orm.annotations.Column;
import be.tftic.java.orm.annotations.Id;
import be.tftic.java.orm.annotations.Table;

@Table(name="\"Role\"")
public class Role {

    @Id
    @Column(name="role_id")
    private long id;
    @Column(name = "role_name")
    private String name;
    @Column(name = "role_description")
    private String description;

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

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
