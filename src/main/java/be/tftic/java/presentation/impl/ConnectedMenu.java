package be.tftic.java.presentation.impl;

import be.tftic.java.domain.models.business.SignedInUser;
import be.tftic.java.presentation.Option;

import java.util.Map;

public abstract class ConnectedMenu extends EveryoneMenu {
    private final SignedInUser user;

    public ConnectedMenu(String title, SignedInUser user) {
        super(title);
        this.user = user;
    }

    @Override
    protected Map<String, Option> initOptions() {
        var options = super.initOptions();
        options.put("2", new Option("sign out", this::handleSignout));
        return options;
    }

    private void handleSignout(){
        System.out.println("SIGNING OUT");
        switchTo(new VisitorMenu());
    }

    protected SignedInUser getUser() {
        return user;
    }
}
