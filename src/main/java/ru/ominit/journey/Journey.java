package ru.ominit.journey;

import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Verdict;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akryukov
 * 10.05.2018
 */
public class Journey {
    private String sessionId;
    private List<Step> steps;

    public Journey(String sessionId) {
        this.sessionId = sessionId;
        this.steps = new ArrayList<>();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void addStep(Verdict verdict, String sessionId) {
        steps.add(new Step(
                sessionId,
                verdict.decision,
                verdict.past.getHaystackId(),
                verdict.past.getRiddleId(),
                verdict.lastAttemptText,
                LocalDateTime.now()
        ));
    }

    public List<HighlightRange> getSuccessfulAttempts(EscapedHtmlString[] grain, String riddleId) {
        return steps.stream()
                .filter(step -> step.decision.correct && step.riddleId.equals(riddleId))
                .flatMap(step -> HighlightRange.highlightAll(grain, EscapedHtmlString.make(step.attempt).getGrain()).stream())
                .collect(Collectors.toList());
    }

    public String highlightSuccessfulAttempts(Verdict verdict) {
        EscapedHtmlString wheat = EscapedHtmlString.make(verdict.future.getHaystack().getWheat());
        EscapedHtmlString[] grain = wheat.getGrain();
        List<HighlightRange> successfulAttempts = getSuccessfulAttempts(grain, verdict.future.getRiddleId());
        HighlightRange.joinRanges(successfulAttempts);
        return String.join("", HighlightRange.tokenize(successfulAttempts, wheat));
    }


    public List<Step> getSteps() {
        return Collections.unmodifiableList(steps);
    }
}
