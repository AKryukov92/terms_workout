package ru.ominit.model;

/**
 * @author akryukov
 * 10.05.2018
 */
public class SingleAnswerRiddleProgress {
    private final Riddle riddle;
    private final boolean anyMatchingGiven;
    private final boolean minimalGiven;
    private final boolean maximalGiven;

    public SingleAnswerRiddleProgress(
        Riddle riddle,
        boolean anyMatchingGiven,
        boolean minimalGiven,
        boolean maximalGiven
    ) {
        this.riddle = riddle;
        this.anyMatchingGiven = anyMatchingGiven;
        this.minimalGiven = minimalGiven;
        this.maximalGiven = maximalGiven;
    }
}
