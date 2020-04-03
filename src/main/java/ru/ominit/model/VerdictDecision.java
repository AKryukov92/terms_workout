package ru.ominit.model;

/**
 * @author akryukov
 * 10.05.2018
 */
public enum VerdictDecision {
    IRRELEVANT(false, false, false, "Irrelevant"),
    CORRECT(true, true, false, "Correct"),
    INCORRECT(true, false, true, "Incorrect"),
    UNDECIDED(true, false, false, "Undecided"),
    NEED_LESS(true, false, true, "Incorrect. Need less"),
    NEED_MORE(true, false, true, "Incorrect. Need more");

    public final boolean relevant;
    public final boolean correct;
    public final boolean incorrect;
    public final String value;

    VerdictDecision(boolean relevant, boolean correct, boolean incorrect, String value) {
        this.relevant = relevant;
        this.correct = correct;
        this.incorrect = incorrect;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
