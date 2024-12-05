package ru.ominit.journey;

import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;
import ru.ominit.model.Verdict;

/**
 * @author akryukov
 * 10.05.2018
 */
public class SingleAnswerRiddleProgress extends RiddleProgress {
    private final Riddle riddle;
    private boolean matchingGiven;
    private boolean minimalGiven;
    private boolean maximalGiven;
    private final Answer answer;

    @Override
    public String getRiddleType() {
        return "single";
    }

    @Override
    public String getRiddleNeedle() {
        return riddle.getNeedle();
    }

    @Override
    public boolean isFullySolved() {
        return matchingGiven;
    }

    @Override
    public boolean isPartiallySolved() {
        return matchingGiven;
    }

    @Override
    public int countMatching() {
        return matchingGiven ? 1 : 0;
    }

    public SingleAnswerRiddleProgress(Riddle riddle) {
        this.riddle = riddle;
        this.matchingGiven = false;
        this.minimalGiven = false;
        this.maximalGiven = false;
        this.answer = riddle.listAnswers().get(0);
    }

    public boolean isMatchingGiven() {
        return matchingGiven;
    }

    public boolean isMinimalGiven() {
        return minimalGiven;
    }

    public boolean isMaximalGiven() {
        return maximalGiven;
    }

    @Override
    public void update(Verdict verdict) {
        String[] attemptTokens = verdict.lastAttemptText.split("\\s+");
        String[] contextTokens = verdict.lastAttemptContext.split("\\s+");
        if (answer.matches(attemptTokens, contextTokens)) {
            matchingGiven = true;
        }
        if (answer.isMinimal(attemptTokens)) {
            minimalGiven = true;
        }
        if (answer.isMaximal(attemptTokens)) {
            maximalGiven = true;
        }
    }
}
