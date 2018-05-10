package ru.ominit.model;

/**
 * Created by Александр on 30.03.2018.
 */
public class Verdict {
    public final boolean relevant;
    public final boolean correct;
    public final boolean incorrect;
    public final String last_attempt;
    public final Fate future;

    private Verdict(
            boolean relevant,
            boolean correct,
            boolean incorrect,
            String lastAttempt,
            Fate future
    ) {
        this.relevant = relevant;
        this.correct = correct;
        this.incorrect = incorrect;
        this.last_attempt = lastAttempt;
        this.future = future;
    }

    public static Verdict makeIncorrect(String lastAttempt, Fate future) {
        return new Verdict(true, false, true, lastAttempt, future);
    }

    public static Verdict makeCorrect(String lastAttempt, Fate future) {
        return new Verdict(true, true, false, lastAttempt, future);
    }

    public static Verdict makeIrrelevant(String lastAttempt, Fate future) {
        return new Verdict(false, false, false, lastAttempt, future);
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
}
