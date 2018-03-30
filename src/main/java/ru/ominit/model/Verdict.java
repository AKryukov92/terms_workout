package ru.ominit.model;

/**
 * Created by Александр on 30.03.2018.
 */
public class Verdict {
    public final boolean relevant;
    public final boolean correct;
    public final boolean incorrect;
    public final String last_attempt;

    private Verdict(
            boolean relevant,
            boolean correct,
            boolean incorrect,
            String lastAttempt
    ) {
        this.relevant = relevant;
        this.correct = correct;
        this.incorrect = incorrect;
        this.last_attempt = lastAttempt;
    }

    public static Verdict makeIncorrect(String lastAttempt) {
        return new Verdict(true, false, true, lastAttempt);
    }

    public static Verdict makeCorrect(String lastAttempt) {
        return new Verdict(true, true, false, lastAttempt);
    }

    public static Verdict makeIrrelevant(String lastAttempt) {
        return new Verdict(false, false, true, lastAttempt);
    }

    public static Verdict makeFresh(){
        return new Verdict(true, false, false, "");
    }
}
