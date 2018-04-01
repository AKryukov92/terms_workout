package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Александр on 30.03.2018.
 */
public class Sphinx {
    @JacksonXmlProperty(localName = "haystack")
    private String haystack;

    @JacksonXmlProperty(localName = "riddles")
    @JacksonXmlElementWrapper()
    private List<Riddle> riddles = new ArrayList<>();

    public Riddle getRiddle(Random rnd) {
        int next = rnd.nextInt(riddles.size());
        Riddle nextRiddle = riddles.get(next);
        assert nextRiddle.isRelevant(haystack);
        return nextRiddle;
    }

    public String getHaystack() {
        return haystack;
    }

    public Riddle getRiddle(String riddleId, Random rnd) {
        for (Riddle riddle : riddles) {
            if (riddle.getId().equals(riddleId)) {
                return riddle;
            }
        }
        return getRiddle(rnd);
    }

    public Verdict decide(Riddle riddle, String originalAttempt) {
        String attempt = originalAttempt.trim();
        boolean isRelevant = haystack.contains(attempt);
        if (isRelevant) {
            if (riddle.isCorrect(attempt)) {
                return Verdict.makeCorrect(attempt);
            } else {
                return Verdict.makeIncorrect(attempt);
            }
        }
        return Verdict.makeIrrelevant(attempt);
    }
}
