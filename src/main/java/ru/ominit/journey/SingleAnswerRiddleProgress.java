package ru.ominit.journey;

import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;

/**
 * @author akryukov
 *         10.05.2018
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
    public void update(Step step) {
        if (answer.matches(step.attempt)) {
            matchingGiven = true;
        }
        if (answer.isMinimal(step.attempt)) {
            minimalGiven = true;
        }
        if (answer.isMaximal(step.attempt)) {
            maximalGiven = true;
        }
    }
}
