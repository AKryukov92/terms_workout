package ru.ominit.journey;

import ru.ominit.model.Verdict;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public List<Step> getSteps() {
        return Collections.unmodifiableList(steps);
    }
}
