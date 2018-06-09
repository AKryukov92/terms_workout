package ru.ominit.model;

import java.time.LocalDateTime;

/**
 * @author akryukov
 * 10.05.2018
 */
public class Step {
    public final String sessionId;
    public final VerdictDecision decision;
    public final String haystackId;
    public final String riddleId;
    public final String attempt;
    public final LocalDateTime submissionDate;

    public Step(
        String sessionId,
        VerdictDecision decision,
        String haystackId,
        String riddleId,
        String attempt,
        LocalDateTime submissionDate
    ) {
        this.sessionId = sessionId;
        this.decision = decision;
        this.haystackId = haystackId;
        this.riddleId = riddleId;
        this.attempt = attempt;
        this.submissionDate = submissionDate;
    }
}
