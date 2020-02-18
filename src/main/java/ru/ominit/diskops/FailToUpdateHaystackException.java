package ru.ominit.diskops;

public class FailToUpdateHaystackException extends RuntimeException {
    private String haystackId;

    public FailToUpdateHaystackException(String haystackId) {
        this.haystackId = haystackId;
    }

    @Override
    public String getMessage() {
        return "Не удалось удалить Haystack " + haystackId;
    }
}
