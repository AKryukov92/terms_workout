package ru.ominit.model;

/**
 * @author akryukov
 * 03.05.2018
 */
public class InsaneTaskException extends RuntimeException {
    private Answer answer;
    private String haystack;
    private String needle;

    public InsaneTaskException(String haystack, String needle, Answer answer) {
        this.answer = answer;
        this.haystack = haystack;
        this.needle = needle;
    }

    @Override
    public String getMessage() {
        return "Задача была поставлена некорректно. Needle: " + needle +
                " answer " + answer.toString() +
                " haystack: " + haystack;
    }
}
