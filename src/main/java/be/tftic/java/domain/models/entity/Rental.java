package be.tftic.java.domain.models.entity;

import be.tftic.java.dal.orm.annotation.Id;
import be.tftic.java.dal.utils.EntityUtils;
import be.tftic.java.dal.orm.annotation.Column;
import be.tftic.java.dal.orm.annotation.Table;
import be.tftic.java.dal.orm.annotation.relation.ManyToOne;

import java.time.LocalDate;

@Table(name = "\"Rental\"", configName = "carental")
public class Rental {
    @Id
    @Column(name = "rental_id")
    private long id;
    @Column(name = "rental_start")
    private LocalDate startDate;
    @Column(name = "rental_end")
    private LocalDate endDate;
    @Column(name = "rental_return")
    private LocalDate returnDate;
    @Column(name = "rental_price")
    private double price;
    @Column(name = "rental_deposit")
    private double deposit;

    @ManyToOne(joinColumnName = "rental_user_id")
    private User renter;
    @ManyToOne(joinColumnName = "rental_car_id")
    private Car rented;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    public User getRenter() {
        if( this.renter == null ){
            this.renter = EntityUtils.<User>getReferenceFor(this, "renter")
                    .orElseThrow();
        }
        return renter;
    }

    public void setRenter(User renter) {
        this.renter = renter;
    }

    public Car getRented() {
        if( this.rented == null ){
            this.rented = EntityUtils.<Car>getReferenceFor(this, "rented")
                    .orElseThrow();
        }
        return rented;
    }

    public void setRented(Car rented) {
        this.rented = rented;
    }
}
