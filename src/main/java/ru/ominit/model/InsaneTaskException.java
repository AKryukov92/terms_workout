package ru.ominit.model;

/**
 * @author akryukov
 * 03.05.2018
 */
public class InsaneTaskException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Задача была поставлена некорректно";
    }
}
