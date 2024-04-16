package be.tftic.java.presentation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class MenuBuilder {

    private final String title;
    private final Map<String, Option> options = new HashMap<>();
    private final Map<Exception, BooleanSupplier> exceptionHandlers = new HashMap<>();

    private MenuBuilder(String title){
        this.title = title;
    }

    public static MenuBuilder builder(String title){
        return new MenuBuilder(title);
    }

    public MenuBuilder addOption(String command, Option option){
        options.put(command, option);
        return this;
    }

    public MenuBuilder addExceptionHandler(Exception exception, BooleanSupplier supplier){
        exceptionHandlers.put(exception, supplier);
        return this;
    }

    public IMenu build(){
        return new AbstractMenu(title) {
            private final Map<Exception, BooleanSupplier> exceptionHandlers = MenuBuilder.this.exceptionHandlers;
            @Override
            protected boolean handleError(Exception e) {
                return this.exceptionHandlers.get(e).getAsBoolean();
            }

            @Override
            protected Map<String, Option> initOptions() {
                var options =  super.initOptions();
                options.putAll(MenuBuilder.this.options);
                return options;
            }
        };
    }

}
