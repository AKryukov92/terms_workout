package ru.ominit.model;

/**
 * @author akryukov
 * 03.04.2018
 */
public class Fate {
    private Riddle riddle;
    private String haystack;
    private String haystackId;

    public Fate(Riddle riddle, Haystack haystack, String haystackId) {
        this.riddle = riddle;
        this.haystack = haystack.getWheat();
        this.haystackId = haystackId;
    }

    public Riddle getRiddle() {
        return riddle;
    }

    public String getWheat() {
        return haystack;
    }

    public String getRiddleId() {
        return riddle.getId();
    }

    public String getHaystackId() {
        return haystackId;
    }
}
