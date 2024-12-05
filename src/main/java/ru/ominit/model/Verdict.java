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
    public final String haystackId;
    public final String riddleId;
    public final String lastAttemptText;
    public final String lastAttemptContext;
    public final Fate past;

    public Verdict(
            boolean relevant,
            boolean correct,
            boolean incorrect,
            boolean needless,
            boolean needmore,
            VerdictDecision decision,
            String haystackId,
            String riddleId,
            String lastAttemptText,
            String lastAttemptContext,
            Fate past
    ) {
        this.relevant = relevant;
        this.correct = correct;
        this.incorrect = incorrect;
        this.decision = decision;
        this.needless = needless;
        this.needmore = needmore;
        this.haystackId = haystackId;
        this.riddleId = riddleId;
        this.lastAttemptText = lastAttemptText;
        this.lastAttemptContext = lastAttemptContext;
        this.past = past;
    }
}
