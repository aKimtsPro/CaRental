package be.tftic.java.presentation.impl;

import be.tftic.java.bll.impl.RentalService;
import be.tftic.java.domain.cdi.ServiceContainer;
import be.tftic.java.domain.models.business.SignedInUser;
import be.tftic.java.domain.models.entity.Rental;
import be.tftic.java.presentation.Option;

import java.time.LocalDateTime;
import java.util.Map;

public class UserMenu extends ConnectedMenu {

    private final RentalService rentalService;
    private final String format = "dd/MM/yy HH:mm";

    public UserMenu(SignedInUser user) {
        super("User menu", user);
        rentalService = ServiceContainer.getInstance(RentalService.class);
    }

    @Override
    protected Map<String, Option> initOptions() {
        var options = super.initOptions();
        options.put("3", new Option("create rental", this::handleCreateRental));
        return options;
    }

    private void handleCreateRental(){
        long carId = askForValue(Long.class, "> car id: ");
        LocalDateTime startDate = askForDateTime("> start: ", format);
        LocalDateTime endDate = askForDateTime("> end: ", format);
        Rental rental = rentalService.createRental(carId, startDate, endDate, getUser().username());
        System.out.println(rental);
    }
}
