package be.tftic.java.domain.models.entity;

import be.tftic.java.dal.orm.annotation.Id;
import be.tftic.java.dal.orm.annotation.relation.OneToMany;
import be.tftic.java.dal.utils.EntityUtils;
import be.tftic.java.dal.orm.annotation.Column;
import be.tftic.java.dal.orm.annotation.Table;

import java.util.List;

@Table(name = "\"Car\"", configName = "carental")
public class Car {

    @Id
    @Column(name = "car_id")
    private long id;
    @Column(name = "car_brand")
    private String brand;
    @Column(name = "car_model")
    private String model;
    @Column(name = "car_color")
    private String color;
    @Column(name = "car_power")
    private int power;
    @Column(name = "car_mileage")
    private int mileage;
    @Column(name = "car_doors")
    private int doorCount;
    @Column(name = "car_daily_rate")
    private double dailyRate;

    @OneToMany(referencedBy = Rental.class, referenceColumn = "rental_car_id")
    private List<Rental> rentals;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public int getDoorCount() {
        return doorCount;
    }

    public void setDoorCount(int doorCount) {
        this.doorCount = doorCount;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public List<Rental> getRentals() {
        if(rentals == null) {
            rentals = EntityUtils.getReferencesFor(this, "rental");
        }
        return rentals;
    }

    public void setRentals(List<Rental> rentals) {
        this.rentals = rentals;
    }
}
