package be.tftic.java.presentation;

public class Option {
    private final String label;
    private final Runnable action;

    public Option(String name, Runnable action) {
        this.label = name;
        this.action = action;
    }

    public String getName() {
        return label;
    }

    public Runnable getAction() {
        return action;
    }
}
