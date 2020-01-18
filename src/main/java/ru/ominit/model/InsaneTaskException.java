package ru.ominit.model;

/**
 * @author akryukov
 * 03.05.2018
 */
public class InsaneTaskException extends RuntimeException {
    private Answer answer;
    private String grain;
    private String needle;

    public InsaneTaskException(String grain, String needle, Answer answer) {
        this.answer = answer;
        this.grain = grain;
        this.needle = needle;
    }

    @Override
    public String getMessage() {
        return "Задача была поставлена некорректно. Needle: " + needle +
                " answer " + answer.toString() +
                " grain: " + grain;
    }
}
