package ru.ominit.model;

public class IncorrectAnswerException extends RuntimeException {
    private String minimal;
    private String maximal;

    public IncorrectAnswerException(String minimal, String maximal) {
        this.minimal = minimal;
        this.maximal = maximal;
    }

    @Override
    public String getMessage() {
        return "Полный ответ должен содрежать краткий ответ. Фактически было: min '" + minimal + "' max '" + maximal + "'";
    }
}
