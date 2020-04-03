package ru.ominit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import ru.ominit.highlight.EscapedHtmlString;

import java.util.Arrays;

/**
 * @author akryukov
 * 28.04.2018
 */
public class Answer {
    @JacksonXmlProperty(localName = "min")
    private String minimal;
    @JacksonXmlProperty(localName = "max")
    private String maximal;

    public Answer(
            @JacksonXmlProperty(localName = "min") String minimal,
            @JacksonXmlProperty(localName = "max") String maximal
    ) throws IncorrectAnswerException {
        String refinedMin = minimal.replaceAll("\\s+", " ").trim();
        String refinedMax = maximal.replaceAll("\\s+", " ").trim();
        if (!refinedMax.contains(refinedMin)) {
            throw new IncorrectAnswerException(minimal, maximal);
        }
        this.minimal = refinedMin;
        this.maximal = refinedMax;
    }

    public Answer(String both) {
        this.minimal = both;
        this.maximal = both;
    }

    @JsonIgnore
    public EscapedHtmlString[] getMinimalFragments() {
        return EscapedHtmlString.make(minimal).splitByWhitespace();
    }

    @JsonIgnore
    public EscapedHtmlString[] getMaximalFragments() {
        return EscapedHtmlString.make(maximal).splitByWhitespace();
    }

    public boolean matches(String[] attemptTokens) {
        String[] minimalTokens = minimal.split("\\s+");
        String[] maximalTokens = maximal.split("\\s+");
        boolean matchesMinimal = Haystack.indexOfInArr(attemptTokens, minimalTokens) >= 0;
        boolean matchesMaximal = Haystack.indexOfInArr(maximalTokens, attemptTokens) >= 0;
        return matchesMinimal && matchesMaximal;
    }

    public boolean isNeedLess(String[] attemptTokens) {
        String[] maximalTokens = minimal.split("\\s+");
        return Haystack.indexOfInArr(attemptTokens, maximalTokens) >= 0;
    }

    public boolean isNeedMore(String[] attemptTokens) {
        String[] minimalTokens = maximal.split("\\s+");
        return Haystack.indexOfInArr(minimalTokens, attemptTokens) >= 0;
    }

    public boolean isMinimal(String[] attemptTokens) {
        return Arrays.equals(minimal.split("\\s+"), attemptTokens);
    }

    public boolean isMaximal(String[] attemptTokens) {
        return Arrays.equals(maximal.split("\\s+"), attemptTokens);
    }

    public static boolean areValid(String minimal, String maximal) {
        String refinedMin = minimal.replaceAll("\\s+", " ").trim();
        String refinedMax = maximal.replaceAll("\\s+", " ").trim();
        return refinedMax.contains(refinedMin);
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

    public boolean relevantTo(String[] grain) {
        String[] minimalTokens = minimal.split("\\s+");
        String[] maximalTokens = maximal.split("\\s+");
        boolean matchesMinimal = Haystack.indexOfInArr(grain, minimalTokens) >= 0;
        boolean matchesMaximal = Haystack.indexOfInArr(grain, maximalTokens) >= 0;
        return matchesMinimal && matchesMaximal;
    }

    public boolean intersects(Answer answer) {
        return this.maximal.contains(answer.maximal) || answer.maximal.contains(this.maximal);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "minimal='" + minimal + '\'' +
                ", maximal='" + maximal + '\'' +
                '}';
    }
}
