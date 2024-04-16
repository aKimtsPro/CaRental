package be.tftic.java;

import be.tftic.java.presentation.IMenu;
import be.tftic.java.presentation.impl.VisitorMenu;

public class MenuMain {


    public static void main(String[] args) {
        IMenu menu = new VisitorMenu();
        menu.start();
    }
}
