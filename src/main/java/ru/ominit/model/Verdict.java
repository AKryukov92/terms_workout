package ru.ominit.model;

/**
 * Created by Александр on 30.03.2018.
 */
public class Verdict {
    public final VerdictDecision decision;
    public final boolean relevant;
    public final boolean correct;
    public final boolean incorrect;
    public final boolean needless;
    public final boolean needmore;
    public final String lastAttemptText;
    public final Fate future;
    public final Fate past;

    public Verdict(
            boolean relevant,
            boolean correct,
            boolean incorrect,
            boolean needless,
            boolean needmore,
            VerdictDecision decision,
            String lastAttemptText,
            Fate past,
            Fate future
    ) {
        this.relevant = relevant;
        this.correct = correct;
        this.incorrect = incorrect;
        this.decision = decision;
        this.needless = needless;
        this.needmore = needmore;
        this.lastAttemptText = lastAttemptText;
        this.future = future;
        this.past = past;
    }
}
