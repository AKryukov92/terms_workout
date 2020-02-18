package ru.ominit.diskops;

public class MetaFileMissingException extends RuntimeException {
    private String haystackPath;

    public MetaFileMissingException(String haystackPath) {
        this.haystackPath = haystackPath;
    }

    public MetaFileMissingException(String haystackPath, Throwable cause) {
        super(cause);
        this.haystackPath = haystackPath;
    }

    @Override
    public String getMessage() {
        return "Директория с заданиями '" + haystackPath + "' должна содержать файл " + RiddleLoaderService.META_FILENAME + " с корректным содержимым";
    }
}
