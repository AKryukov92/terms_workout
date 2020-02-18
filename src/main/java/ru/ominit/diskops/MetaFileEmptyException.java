package ru.ominit.diskops;

public class MetaFileEmptyException extends RuntimeException {
    private String haystackPath;

    public MetaFileEmptyException(String haystackPath) {
        this.haystackPath = haystackPath;
    }

    @Override
    public String getMessage() {
        return "Мета файл '" + haystackPath + "/" + RiddleLoaderService.META_FILENAME + "' пуст";
    }
}
