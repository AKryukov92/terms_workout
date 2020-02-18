package ru.ominit.highlight;

/**
 * @author akryukov
 * 10.05.2018
 */
public class HaystackFileMissingException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Отсутствуют файлы с задачами";
    }
}
