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

public class VisitorMenu extends EveryoneMenu {

    private final UserService userService;

    public VisitorMenu() {
        super("Main Menu");
        userService = ServiceContainer.getInstance(UserService.class);
    }

    @Override
    protected Map<String, Option> initOptions() {
        Map<String, Option> options =  super.initOptions();
        options.put("2", new Option("sign in", this::handleSignIn));
        options.put("3", new Option("sign up", this::handleSignUp));
        return options;
    }

    public void handleSignIn(){
        String username = this.askForValue(String.class, "> username: ");
        String password = this.askForValue(String.class, "> password: ");
        SignedInUser user = userService.signin(username,password);
        IMenu connectedMenu = switch (user.role()){
            case "USER" -> new UserMenu(user);
            case "ADMIN" -> new AdminMenu(user);
            default -> throw new RuntimeException("invalid role");
        };
        switchTo(connectedMenu);
    }

    public void handleSignUp(){
        String username = this.askForValue(String.class, "> username: ");
        String password = this.askForValue(String.class, "> password: ");
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        SignedInUser signedInUser = userService.signup(user);
        IMenu connectedMenu = switch (signedInUser.role()){
            case "USER" -> new UserMenu(signedInUser);
            case "ADMIN" -> new AdminMenu(signedInUser);
            default -> throw new RuntimeException("invalid role");
        };
        switchTo(connectedMenu);
    }

}
