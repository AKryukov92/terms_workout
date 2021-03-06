package ru.ominit.model;

/**
 * @author akryukov
 * 03.04.2018
 */
public class Fate {
    private Riddle riddle;
    private Haystack haystack;
    private String haystackId;

    public Fate(Riddle riddle, Haystack haystack, String haystackId) {
        this.riddle = riddle;
        this.haystack = haystack;
        this.haystackId = haystackId;
    }

    public Riddle getRiddle() {
        return riddle;
    }

    public Haystack getHaystack() {
        return haystack;
    }

    public String getWheat() {
        return haystack.getWheat();
    }

    public String getRiddleId() {
        return riddle.getId();
    }

    public String getNextRiddleId() {
        return riddle.getNextId();
    }

    public String getHaystackId() {
        return haystackId;
    }

    public void insane() {
        riddle.assertRelevant(haystack.getGrain());
    }

    public Verdict incorrectVerdict(String lastAttempt) {
        return new Verdict(true, false, true, false, false, VerdictDecision.INCORRECT, lastAttempt, this, this);
    }

    public Verdict needLessVerdict(String lastAttempt) {
        return new Verdict(true, false, true, true, false, VerdictDecision.NEED_LESS, lastAttempt, this, this);
    }

    public Verdict needMoreVerdict(String lastAttempt) {
        return new Verdict(true, false, true, false, true, VerdictDecision.NEED_MORE, lastAttempt, this, this);
    }

    public Verdict correctVerdict(String lastAttempt, Fate future) {
        return new Verdict(true, true, false, false, false, VerdictDecision.CORRECT, lastAttempt, this, future);
    }

    public Verdict irrelevantVerdict(String lastAttempt) {
        return new Verdict(false, false, false, false, false, VerdictDecision.IRRELEVANT, lastAttempt, this, this);
    }

    public Verdict freshVerdict() {
        return new Verdict(true, false, false, false, false, VerdictDecision.UNDECIDED, "", this, this);

    }

    public Verdict skippedVerdict(Fate future) {
        return new Verdict(false, false, false, false, false, VerdictDecision.UNDECIDED, "", this, future);
    }
}
