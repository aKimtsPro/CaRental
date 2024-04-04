package be.tftic.java.utils;

public class LoadConfigFailedException extends RuntimeException {
    private final String filename;

    public LoadConfigFailedException(Throwable cause, String fileName) {
        super(STR."Could not load config from \{fileName}",cause);
        this.filename = fileName;
    }

    public String getFilename() {
        return filename;
    }
}
