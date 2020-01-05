package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * @author akryukov
 * 28.04.2018
 */
public class Answer {
    @JacksonXmlProperty(localName = "min")
    private String minimal;
    @JacksonXmlProperty(localName = "max")
    private String maximal;

    public Answer() {

    }

    public Answer(
            String minimal,
            String maximal
    ) {
        String refinedMin = minimal.replaceAll("\\s+", " ").trim();
        String refinedMax = maximal.replaceAll("\\s+", " ").trim();
        if (!refinedMax.contains(refinedMin)) {
            throw new IllegalStateException("Полный ответ должен содержать краткий ответ");
        }
        this.minimal = refinedMin;
        this.maximal = refinedMax;
    }

    public boolean matches(String attempt) {
        return attempt.contains(minimal) && maximal.contains(attempt);
    }

    public boolean relevantTo(String grain) {
        return grain.contains(minimal) && grain.contains(maximal);
    }

    public boolean intersects(Answer answer) {
        return this.maximal.contains(answer.maximal) || answer.maximal.contains(this.maximal);
    }
}
