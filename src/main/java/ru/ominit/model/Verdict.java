package ru.ominit.model;

import ru.ominit.journey.Step;

import java.time.LocalDateTime;

/**
 * Created by Александр on 30.03.2018.
 */
public class Verdict {
    public final VerdictDecision decision;
    public final boolean relevant;
    public final boolean correct;
    public final boolean incorrect;
    public final String lastAttemptText;
    public final Fate future;
    public final Fate past;

    public Verdict(
            boolean relevant,
            boolean correct,
            boolean incorrect,
            VerdictDecision decision,
            String lastAttemptText,
            Fate past,
            Fate future
    ) {
        this.relevant = relevant;
        this.correct = correct;
        this.incorrect = incorrect;
        this.decision = decision;
        this.lastAttemptText = lastAttemptText;
        this.future = future;
        this.past = past;
    }

    public Step produceStep(String sessionId) {
        return new Step(
                sessionId,
                decision,
                past.getHaystackId(),
                past.getRiddleId(),
                lastAttemptText,
                LocalDateTime.now()
        );
    }
}
