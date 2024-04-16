package be.tftic.java.presentation.impl;

import be.tftic.java.bll.impl.CarService;
import be.tftic.java.bll.impl.UserService;
import be.tftic.java.domain.cdi.ServiceContainer;
import be.tftic.java.domain.models.business.SignedInUser;
import be.tftic.java.domain.models.entity.User;
import be.tftic.java.presentation.AbstractMenu;
import be.tftic.java.presentation.IMenu;
import be.tftic.java.presentation.Option;

import java.util.Map;

public abstract class EveryoneMenu extends AbstractMenu {

    private final CarService carService;

    public EveryoneMenu(String title) {
        super(title);
        carService = ServiceContainer.getInstance(CarService.class);
    }

    @Override
    protected Map<String, Option> initOptions() {
        Map<String, Option> options =  super.initOptions();
        options.put("1", new Option("see available cars", this::handleSeeAvailableCars));
        return options;
    }

    public void handleSeeAvailableCars(){
        carService.getAll().forEach(
                (car) -> System.out.println(STR."\{car.getBrand()}, \{car.getModel()}, \{car.getColor()}")
        );
    }


    @Override
    protected boolean handleError(Exception e) {
        if(e instanceof RuntimeException){
            System.out.println(e.getMessage());
            return false;
        }


        return true;
    }

    protected CarService getCarService() {
        return carService;
    }
}
