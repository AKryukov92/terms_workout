package ru.ominit.model;

import java.time.LocalDateTime;

/**
 * @author akryukov
 * 10.05.2018
 */
public class Step {
    public final String sessionId;

    public Step(
        String sessionId,
        String haystackId,
        String riddleId,
        String attempt,
        LocalDateTime submissionDate
    ) {
        this.sessionId = sessionId;
        this.haystackId = haystackId;
        this.riddleId = riddleId;
        this.attempt = attempt;
        this.submissionDate = submissionDate;
    }

    public final String haystackId;
    public final String riddleId;
    public final String attempt;
    public final LocalDateTime submissionDate;
}
