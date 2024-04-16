package be.tftic.java.domain.models.entity;

import be.tftic.java.dal.orm.annotation.Column;
import be.tftic.java.dal.orm.annotation.Id;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.annotation.relation.ManyToOne;
import be.tftic.java.dal.orm.annotation.relation.OneToMany;
import be.tftic.java.dal.utils.EntityUtils;

import java.util.List;

@Table(name = "\"User\"", configName = "carental")
public class User {
    @Id
    @Column(name = "user_id")
    private Long id;
    @Column(name = "user_name")
    private String username;
    @Column(name = "user_password")
    private String password;
    @Column(name = "user_active")
    private boolean active = true;

    @ManyToOne(joinColumnName = "user_role_id")
    private Role role;

    @OneToMany(referencedBy = Rental.class, referenceColumn = "rental_user_id")
    private List<Rental> rentals;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Role getRole(){
        if( this.role == null ){
            this.role = EntityUtils.<Role>getReferenceFor(this, "role")
                        .orElseThrow();
        }
        return this.role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<Rental> getRentals() {
        if(rentals == null){
            rentals = EntityUtils.getReferencesFor(this, "rentals");
        }
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }
}
