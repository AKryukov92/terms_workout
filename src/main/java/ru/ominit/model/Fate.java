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

    public String getNextRiddleId() {return riddle.getNextId(); }

    public String getHaystackId() {
        return haystackId;
    }

    public boolean insane(){
        return !riddle.isRelevant(haystack.getGrain());
    }

    public Verdict incorrectVerdict(String lastAttempt) {
        return new Verdict(this, VerdictDecision.INCORRECT, lastAttempt, this);
    }

    public Verdict correctVerdict(String lastAttempt, Fate future) {
        return new Verdict(this, VerdictDecision.CORRECT, lastAttempt, future);
    }

    public Verdict irrelevantVerdict(String lastAttempt) {
        return new Verdict(this, VerdictDecision.IRRELEVANT, lastAttempt, this);
    }

    public Verdict freshVerdict() {
        return new Verdict(this, VerdictDecision.UNDECIDED, "", this);
    }
}
