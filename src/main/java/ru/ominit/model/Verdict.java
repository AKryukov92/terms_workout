package ru.ominit.model;

import java.time.LocalDateTime;

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

    private Verdict(
            boolean relevant,
            boolean correct,
            boolean incorrect,
            String lastAttemptText,
            Fate future
    ) {
        this.relevant = relevant;
        this.correct = correct;
        this.incorrect = incorrect;
        this.lastAttemptText = lastAttemptText;
        this.future = future;
    }

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

    public static Verdict makeIncorrect(String lastAttemptText, Fate future) {
        return new Verdict(true, false, true, lastAttemptText, future);
    }

    public static Verdict makeCorrect(String lastAttemptText, Fate future) {
        return new Verdict(true, true, false, lastAttemptText, future);
    }

    public static Verdict makeIrrelevant(String lastAttemptText, Fate future) {
        return new Verdict(false, false, false, lastAttemptText, future);
    }

    public static Verdict makeFresh(Fate future){
        return new Verdict(true, false, false, "", future);
    }

    public String decision(){
        if (!relevant){
            return "Irrelevant";
        } else if (correct){
            return "Correct";
        } else if (incorrect){
            return "Incorrect";
        } else {
            return "Undecided";
        }
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
