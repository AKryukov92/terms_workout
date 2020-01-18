package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.web.util.HtmlUtils;

import java.util.Optional;

import static ru.ominit.model.HighlightRange.*;

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

    public Optional<HighlightRange> highlight(String grain, HighlightRangeType type) {
        String text = type == HighlightRangeType.MINIMAL ? minimal : maximal;
        int maxStart = grain.indexOf(HtmlUtils.htmlEscape(text));
        if (maxStart < 0) {
            return Optional.empty();
        } else {
            return Optional.of(new HighlightRange(
                    maxStart,
                    maxStart + text.length(),
                    type
            ));
        }
    }

    public Optional<String> highlightIn(String grain) {
        int maxStart = grain.indexOf(maximal);
        if (maxStart < 0) {
            return Optional.empty();
        }
        String left = grain.substring(0, maxStart);
        String right = grain.substring(maxStart + maximal.length());
        if (minimal.equals(maximal)) {
            return Optional.of(left + MAX_START + maximal + END + right);
        }
        int minStart = maximal.indexOf(minimal);
        if (minStart < 0) {
            return Optional.empty();
        }
        String beforeMin = maximal.substring(0, minStart);
        String afterMin = maximal.substring(minStart + minimal.length());
        return Optional.of(left + MAX_START + beforeMin + MIN_START + minimal + END + afterMin + END + right);
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

    public boolean relevantTo(String grain) {
        return grain.contains(minimal) && grain.contains(maximal);
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
