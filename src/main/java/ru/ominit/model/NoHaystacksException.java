package ru.ominit.model;

/**
 * @author akryukov
 * 10.05.2018
 */
public class NoHaystacksException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Отсутствуют файлы с задачами";
    }
}
