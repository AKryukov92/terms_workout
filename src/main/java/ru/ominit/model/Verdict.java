package ru.ominit.model;

/**
 * Created by Александр on 30.03.2018.
 */
public class Verdict {
    public final Riddle next_riddle;
    public final boolean relevant;
    public final boolean correct;
    public final boolean incorrect;
    public final String last_attempt;

    private Verdict(
            Riddle nextRiddle,
            boolean relevant,
            boolean correct,
            boolean incorrect,
            String lastAttempt
    ) {
        this.next_riddle = nextRiddle;
        this.relevant = relevant;
        this.correct = correct;
        this.incorrect = incorrect;
        this.last_attempt = lastAttempt;
    }

    public static Verdict makeIncorrect(Riddle nextRiddle, String lastAttempt) {
        return new Verdict(nextRiddle, true, false, true, lastAttempt);
    }

    public static Verdict makeCorrect(Riddle nextRiddle, String lastAttempt) {
        return new Verdict(nextRiddle, true, true, false, lastAttempt);
    }

    public static Verdict makeIrrelevant(Riddle nextRiddle, String lastAttempt) {
        return new Verdict(nextRiddle, false, false, true, lastAttempt);
    }

    public static Verdict makeFresh(Riddle nextRiddle){
        return new Verdict(nextRiddle, true, false, false, "");
    }
}
