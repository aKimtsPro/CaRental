package be.tftic.java.presentation;

import be.tftic.java.presentation.impl.VisitorMenu;

public abstract class MenuFactory {


    IMenu getVisitorMenu(){
        return new VisitorMenu();
    }

//    IMenu get
}
