package ru.ominit.model;

import ru.ominit.RiddleLoader;

public class MetaFileEmptyException extends RuntimeException {
    private String haystackPath;

    public MetaFileEmptyException(String haystackPath) {
        this.haystackPath = haystackPath;
    }

    @Override
    public String getMessage() {
        return "Мета файл '" + haystackPath + "/" + RiddleLoader.META_FILENAME + "' пуст";
    }
}
