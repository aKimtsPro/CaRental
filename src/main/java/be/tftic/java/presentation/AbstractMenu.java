package be.tftic.java.presentation;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractMenu implements IMenu {
    protected final Scanner scanner = new Scanner(System.in);
    private final String title;
    private final Map<String, Option> options;
    private boolean quit = false;
    private IMenu switchMenu = null;

    public AbstractMenu(String title) {
        this.title = title;
        this.options = initOptions();
    }

    protected Map<String, Option> initOptions() {
        return new HashMap<>();
    }

    public void start(){
        String command;
        do {
            try {
                printMenu();
                List<String> acceptableCommand = new ArrayList<>(options.keySet().size()+1);
                acceptableCommand.addAll(options.keySet());
                acceptableCommand.add("q");
                command = askForCommand(acceptableCommand.toArray(new String[0]));
                handleCommand(command);
            }
            catch (Exception e) {
                if(!handleError(e)){
                    throw e;
                }
                command = null;
            }

        } while(!quit);
        if(switchMenu != null){
            switchMenu.start();
        }
    }


    private void printMenu(){
        String optionLines = options.entrySet().stream()
                        .map(entry -> STR." \{entry.getKey()} - \{entry.getValue().getName()}")
                        .collect(Collectors.joining("\n"));
        optionLines += "\n q - quit";
        System.out.println(
                STR."""
                -------------------------
                |=> \{title}
                -------------------------
                \{optionLines}
                -------------------------

                """
        );
    }
    protected String askForCommand(String[] possibleCommands){
        return askForCommand("Please issue a command", possibleCommands);
    }
    protected String askForCommand(String message, String ...possibleCommands){
        System.out.println(message);
        String command;
        boolean included;
        do {
            System.out.print("> ");
            command = scanner.nextLine().trim();

            included = possibleCommands.length == 0;
            for (String possibleCommand : possibleCommands) {
                if(Objects.equals(possibleCommand, command)){
                    included = true;
                    break;
                }
            }
            if( !included )
                System.out.println("Invalid input, retry (possible commands: "+ Arrays.toString(possibleCommands)+") :");
        } while( !included );
        return command;
    }


    protected  <T> T askForValue(Class<T> type, String message){
        System.out.print(message);
        do {
            try {
                String valueString = scanner.nextLine().trim();
                if (type == Long.class) {
                    return (T) Long.valueOf( Long.parseLong(valueString) );
                } else if (type == Double.class) {
                    return (T) Double.valueOf( Double.parseDouble(valueString) );
                } else if (type == String.class) {
                    return (T) valueString;
                } else if (type == Integer.class){
                    return (T) Integer.valueOf( Integer.parseInt(valueString) );
                }
            }
            catch (Exception ex){
                System.out.println("Invalid value type");
                System.out.print(message);
            }
        } while (true);
    }

    protected LocalDateTime askForDateTime(String message, String pattern){
        System.out.print(message);
        do {
            try {
                String valueString = scanner.nextLine().trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(valueString, formatter);
            }
            catch (DateTimeParseException ex){
                System.out.println(STR."Invalid format (\{pattern})");
                System.out.print(message);
            }
            catch (IllegalArgumentException ex){
                System.out.println("invalid format");
            }
        } while (true);
    }

    protected LocalDateTime askForDateTime(String message){
        return this.askForDateTime(message, "dd/MM/yy HH:mm");
    }

    private void handleCommand(String command){
        if(command.trim().toLowerCase().charAt(0) == 'q'){
            handleQuit();
        }
        else {
            this.getOption(command)
                    .orElseThrow(() -> new IllegalArgumentException(STR."Invalid command: \{command}"))
                    .getAction()
                    .run();
        }
    }

    protected void quit(){
        quit = true;
    }

    protected void switchTo(IMenu menu){
        quit = true;
        switchMenu = menu;
    }

    protected void handleQuit(){
        System.out.println("Bye!");
        quit();
    }

    protected Optional<Option> getOption(String option){
        return Optional.ofNullable(options.get(option));
    }


    protected abstract boolean handleError(Exception e);

}
