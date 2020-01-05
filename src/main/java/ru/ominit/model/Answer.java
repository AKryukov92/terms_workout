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

    public boolean isMinimal(String attempt) {
        return minimal.equals(attempt);
    }

    public boolean isMaximal(String attempt) {
        return maximal.equals(attempt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Answer answer = (Answer) o;

        if (!minimal.equals(answer.minimal)) return false;
        return maximal.equals(answer.maximal);
    }

    @Override
    public int hashCode() {
        int result = minimal.hashCode();
        result = 31 * result + maximal.hashCode();
        return result;
    }

    public boolean relevantTo(String grain) {
        return grain.contains(minimal) && grain.contains(maximal);
    }

    public boolean intersects(Answer answer) {
        return this.maximal.contains(answer.maximal) || answer.maximal.contains(this.maximal);
    }
}
