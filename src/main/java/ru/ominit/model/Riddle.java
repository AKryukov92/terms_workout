package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.highlight.HighlightRangeType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Александр on 30.03.2018.
 */
public class Riddle {
    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "needle")
    private String needle;

    @JacksonXmlProperty(localName = "answer")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "answer")
    private List<Answer> answers;

    @JacksonXmlProperty(localName = "next")
    private String nextId;

    public Riddle(
            @JacksonXmlProperty(localName = "id") String id,
            @JacksonXmlProperty(localName = "needle") String needle,
            @JacksonXmlProperty(localName = "next") String nextId
    ) {
        this.id = id;
        this.needle = needle;
        this.nextId = nextId;
        this.answers = new ArrayList<>();
    }

    @JacksonXmlProperty(localName = "answer")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "answer")
    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public boolean intersectsAnything(Answer proposedAnswer) {
        for (Answer answer : answers) {
            if (answer.intersects(proposedAnswer)) {
                return true;
            }
        }
        return false;
    }

    public String getId() {
        return id;
    }

    public String getNeedle() {
        return needle;
    }

    public String getNextId() {
        return nextId;
    }

    public boolean hasMultipleAnswers() {
        return answers.size() > 1;
    }

    public List<Answer> listAnswers() {
        return Collections.unmodifiableList(answers);
    }

    public boolean isCorrect(String attempt) {
        boolean found = false;
        for (Answer answer : answers) {
            if (answer.matches(attempt)) {
                found = true;
            }
        }
        return found;
    }

    public void assertRelevant(String grain) {
        for (Answer answer : answers) {
            if (!answer.relevantTo(grain)) {
                throw new InsaneTaskException(grain, needle, answer);
            }
        }
    }

    public List<HighlightRange> joinAnswerRanges(EscapedHtmlString escapedGrain, HighlightRangeType type) {
        List<HighlightRange> ranges = answers.stream()
                .flatMap(a -> a.highlightAll(escapedGrain, type).stream())
                .collect(Collectors.toList());
        HighlightRange.joinRanges(ranges);
        return ranges;
    }

    public List<HighlightRange> shiftForSizeOfTags(List<HighlightRange> ranges, String openTag, String closeTag) {
        List<HighlightRange> result = new ArrayList<>();
        int shiftDistance = openTag.length() + closeTag.length();
        int cumulativeShift = 0;
        for (HighlightRange range : ranges) {
            HighlightRange shifted = new HighlightRange(range.getStartIndex() + cumulativeShift, range.getEndIndex() + cumulativeShift);
            cumulativeShift += shiftDistance;
            result.add(shifted);
        }
        return result;
    }

    public String insert(EscapedHtmlString grain, EscapedHtmlString wheat) {
        List<HighlightRange> highlightRangesMaximal = shiftForSizeOfTags(
                joinAnswerRanges(grain, HighlightRangeType.MAXIMAL),
                HighlightRange.MAX_START,
                HighlightRange.END
        );
        EscapedHtmlString modifiedWheat = wheat;
        EscapedHtmlString modifiedGrain = grain;
        for (HighlightRange range : highlightRangesMaximal) {
            HighlightRange rangeForWheat = range.clarify(grain, modifiedWheat);
            modifiedWheat = rangeForWheat.insert(modifiedWheat, HighlightRange.MAX_START, HighlightRange.END);
            modifiedGrain = range.insert(modifiedGrain, HighlightRange.MAX_START, HighlightRange.END);
        }
        List<HighlightRange> highlightRangesMinimal = shiftForSizeOfTags(
                joinAnswerRanges(grain, HighlightRangeType.MINIMAL),
                HighlightRange.MIN_START,
                HighlightRange.END
        );
        for (HighlightRange range : highlightRangesMinimal) {
            HighlightRange rangeForWheat = range.clarify(grain, modifiedWheat);
            modifiedWheat = rangeForWheat.insert(modifiedWheat, HighlightRange.MIN_START, HighlightRange.END);
        }
        return modifiedWheat.toString();
    }


    public static Riddle DEFAULT() {
        return new Riddle("default", "смысл", "");
    }
}
