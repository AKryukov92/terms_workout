package ru.ominit.model;

import java.time.LocalDateTime;

/**
 * Created by Александр on 30.03.2018.
 */
public class Verdict {
    public final VerdictDecision decision;
    public final String last_attempt;
    public final Fate future;
    public final Fate past;

    public Verdict(
            Fate past,
            VerdictDecision decision,
            String lastAttempt,
            Fate future
    ) {
        this.decision = decision;
        this.last_attempt = lastAttempt;
        this.future = future;
        this.past = past;
    }

    public Step produceStep(String sessionId){
        return new Step(
            sessionId,
            decision,
            past.getHaystackId(),
            past.getRiddleId(),
            last_attempt,
            LocalDateTime.now()
        );
    }
}
