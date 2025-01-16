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
    private final String minimal;
    @JacksonXmlProperty(localName = "max")
    private final String maximal;

    @JacksonXmlProperty(localName = "context")
    private final String context;

    public Answer(
            @JacksonXmlProperty(localName = "min") String minimal,
            @JacksonXmlProperty(localName = "max") String maximal,
            @JacksonXmlProperty(localName = "context") String context) throws IncorrectAnswerException {
        String refinedMin = minimal.replaceAll("\\s+", " ").trim();
        String refinedMax = maximal.replaceAll("\\s+", " ").trim();
        if (!refinedMax.contains(refinedMin)) {
            throw new IncorrectAnswerException(minimal, maximal);
        }
        this.minimal = refinedMin;
        this.maximal = refinedMax;
        if (context != null) {
            this.context = context.replaceAll("\\s+", " ").trim();
        } else {
            this.context = refinedMax;
        }
    }

    public Answer(String both, String context) {
        this.minimal = both;
        this.maximal = both;
        this.context = context;
    }

    @JsonIgnore
    public EscapedHtmlString[] getMinimalFragments() {
        return EscapedHtmlString.make(minimal).getGrain();
    }

    @JsonIgnore
    public EscapedHtmlString[] getMaximalFragments() {
        return EscapedHtmlString.make(maximal).getGrain();
    }

    @JsonIgnore
    public EscapedHtmlString[] getContextFragments() {
        return EscapedHtmlString.make(context).getGrain();
    }

    public boolean matches(String[] attemptTokens, String[] answerContext) {
        String[] minimalTokens = minimal.split("\\s+");
        String[] maximalTokens = maximal.split("\\s+");
        String[] contextTokens;
        if (context == null) {
            contextTokens = maximal.split("\\s+");
        } else {
            contextTokens = context.split("\\s+");
        }
        boolean matchesMinimal = Haystack.indexOfInArr(attemptTokens, minimalTokens) >= 0;
        boolean matchesMaximal = Haystack.indexOfInArr(maximalTokens, attemptTokens) >= 0;
        boolean matchesContext = Haystack.indexOfInArr(contextTokens, answerContext) >= 0;
        return matchesMinimal && matchesMaximal && matchesContext;
    }

    public boolean isNeedLess(String[] attemptTokens, String[] contextTokens) {
        String[] minimalTokens = minimal.split("\\s+");
        return !Arrays.equals(attemptTokens, minimalTokens)
                && Haystack.indexOfInArr(attemptTokens, minimalTokens) >= 0;
    }

    public boolean isNeedMore(String[] attemptTokens, String[] contextTokens) {
        String[] maximalTokens = maximal.split("\\s+");
        return !Arrays.equals(attemptTokens, maximalTokens)
                && Haystack.indexOfInArr(maximalTokens, attemptTokens) >= 0;
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

    @Override
    public String toString() {
        return "Answer{" +
                "minimal='" + minimal + '\'' +
                ", maximal='" + maximal + '\'' +
                ", context='" + (context != null ? context : maximal) + '\'' +
                '}';
    }
}
