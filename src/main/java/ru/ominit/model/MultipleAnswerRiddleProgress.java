package ru.ominit.model;

/**
 * @author akryukov
 * 10.05.2018
 */
public class MultipleAnswerRiddleProgress {
    private final Riddle riddle;
    private final int countMatching;
    private final int countMinimal;
    private final int countMaximal;

    public MultipleAnswerRiddleProgress(
        Riddle riddle,
        int countMatching,
        int countMinimal,
        int countMaximal
    ) {
        this.riddle = riddle;
        this.countMatching = countMatching;
        this.countMinimal = countMinimal;
        this.countMaximal = countMaximal;
    }
}
