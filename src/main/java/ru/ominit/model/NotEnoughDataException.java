package ru.ominit.model;

public class NotEnoughDataException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Недостаточно информации для создания загадки.";
    }
}
