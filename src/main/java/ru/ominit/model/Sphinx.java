package ru.ominit.model;

/**
 * Created by Александр on 30.03.2018.
 */
public class Sphinx {
    public Verdict decide(Riddle riddle, String originalAttempt){
        String attempt = originalAttempt.trim();
        boolean isRelevant = riddle.isRelevant(attempt);
        if (isRelevant){
            if (riddle.isCorrect(attempt)){
                return Verdict.makeCorrect(attempt);
            } else {
                return Verdict.makeIncorrect(attempt);
            }
        }
        return Verdict.makeIrrelevant(attempt);
    }
}
