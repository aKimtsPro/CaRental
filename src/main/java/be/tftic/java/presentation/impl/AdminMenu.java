package be.tftic.java.presentation.impl;

import be.tftic.java.bll.impl.CarService;
import be.tftic.java.domain.cdi.ServiceContainer;
import be.tftic.java.domain.models.business.SignedInUser;
import be.tftic.java.domain.models.entity.Car;
import be.tftic.java.presentation.AbstractMenu;
import be.tftic.java.presentation.Option;

import java.util.Map;

public class AdminMenu extends ConnectedMenu {


    public AdminMenu(SignedInUser signedInUser) {
        super("Admin menu", signedInUser);
    }

    @Override
    protected Map<String, Option> initOptions() {
        var options = super.initOptions();
        options.put("3", new Option("add car",this::handleAddCar));
        return options;
    }

    private void handleAddCar(){
        String brand = askForValue(String.class, "> brand: ");
        String model = askForValue(String.class, "> model: ");
        String color = askForValue(String.class, "> color: ");
        int power = askForValue(Integer.class, "> power: ");
        int mileage = askForValue(Integer.class, "> mileage: ");
        int doors = askForValue(Integer.class, "> door count: ");
        double dailyRate = askForValue(Double.class, "> daily rate: ");

        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        car.setColor(color);
        car.setPower(power);
        car.setMileage(mileage);
        car.setDoorCount(doors);
        car.setDailyRate(dailyRate);

        car = getCarService().create(car);

        System.out.println("CREATED:");
        System.out.println(car);
    }

}
