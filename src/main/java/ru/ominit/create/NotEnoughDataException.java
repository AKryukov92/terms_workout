package ru.ominit.create;

public class NotEnoughDataException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Недостаточно информации для создания загадки.";
    }
}
