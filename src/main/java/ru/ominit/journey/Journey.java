package ru.ominit.journey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.ominit.SphinxController;
import ru.ominit.highlight.EscapedHtmlString;
import ru.ominit.highlight.HighlightRange;
import ru.ominit.model.Haystack;
import ru.ominit.model.Verdict;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author akryukov
 * 10.05.2018
 */
public class Journey {
    private Logger logger = LoggerFactory.getLogger(Journey.class);
    private String sessionId;
    private List<Verdict> verdicts;

    public Journey(String sessionId) {
        this.sessionId = sessionId;
        this.verdicts = new ArrayList<>();
    }

    public void addStep(Verdict verdict) {
        logger.info("Assign decision {} for haystackId {} and riddleId {}",
                verdict.decision,
                verdict.future.getHaystackId(),
                verdict.future.getRiddleId()
        );
        verdicts.add(verdict);
    }

    public List<HighlightRange> getSuccessfulAttempts(EscapedHtmlString[] grain, String riddleId) {
        return verdicts.stream()
                .filter(step -> step.decision.correct && step.riddleId.equals(riddleId))
                .flatMap(step -> HighlightRange.highlightAll(grain, EscapedHtmlString.make(step.lastAttemptText).getGrain()).stream())
                .collect(Collectors.toList());
    }

    public String highlightSuccessfulAttempts(Haystack haystack, String riddleId) {
        EscapedHtmlString wheat = EscapedHtmlString.make(haystack.getWheat());
        EscapedHtmlString[] grain = wheat.getGrain();
        List<HighlightRange> successfulAttempts = getSuccessfulAttempts(grain, riddleId);
        HighlightRange.joinRanges(successfulAttempts);
        return String.join("", HighlightRange.tokenize(successfulAttempts, wheat));
    }

    public List<Verdict> getVerdicts() {
        return Collections.unmodifiableList(verdicts);
    }

    public boolean hasVerdicts() {
        return !verdicts.isEmpty();
    }

    public Verdict getLast() {
        return verdicts.get(verdicts.size() - 1);
    }

    public ShortProgress reportProgress(Haystack haystack, String haystackId) {
        HaystackProgress p = new HaystackProgress(haystack, haystackId);
        for (Verdict verdict : verdicts) {
            if (verdict.haystackId.equals(haystackId)) {
                p.update(verdict);
            }
        }
        return new ShortProgress(haystackId, p.maxProgress(), p.currentProgress());
    }

    public boolean hasSuccessfulAttempt(String haystackId, String riddleId) {
        for (Verdict verdict : verdicts) {
            if (verdict.haystackId.equals(haystackId) && verdict.riddleId.equals(riddleId)) {
                return true;
            }
        }
        return false;
    }
}
