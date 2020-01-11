package ru.ominit.model;

/**
 * Created by Александр on 30.03.2018.
 */
public class Verdict {
    public final boolean relevant;
    public final boolean correct;
    public final boolean incorrect;
    public final String lastAttemptText;
    public final Fate future;

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
}
