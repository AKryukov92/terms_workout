package ru.ominit.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ominit.RiddleLoader;

import java.io.IOException;
import java.util.Random;

/**
 * @author akryukov
 * 03.04.2018
 */
@Service
public class Sphinx {
    private RiddleLoader loader;
    private Random random;

    @Autowired
    public Sphinx(RiddleLoader loader, Random random){
        this.loader = loader;
        this.random = random;
    }

    public Verdict decide(String lastHaystackId, String lastRiddleId, String originalAttempt) throws IOException {
        Fate past = behold(lastHaystackId, lastRiddleId);
        if (originalAttempt == null || originalAttempt.isEmpty()) {
            return Verdict.makeFresh(past);
        }
        String attempt = originalAttempt.replaceAll("\\s+", " ").trim();
        boolean isRelevant = past.getWheat().contains(attempt);
        if (!isRelevant) {
            return Verdict.makeIrrelevant(attempt, past);
        }
        if (past.getRiddle().isCorrect(attempt)) {
            String nextHaystackId = loader.getAnyHaystackId(random);
            Haystack nextHaystack = loader.load(nextHaystackId);
            Riddle nextRiddle = nextHaystack.getRiddle(past.getRiddle().getNextId(), random);
            Fate future = new Fate(nextRiddle, nextHaystack, nextHaystackId);
            return Verdict.makeCorrect(attempt, future);
        } else {
            return Verdict.makeIncorrect(attempt, past);
        }
    }

    public Verdict decide(String haystackId, String riddleId) throws IOException {
        Fate fate = behold(haystackId, riddleId);
        return Verdict.makeFresh(fate);
    }

    public Fate behold(String haystackId, String riddleId) throws IOException {
        Riddle nextRiddle;
        Haystack nextHaystack;
        String nextHaystackId = haystackId;
        if (haystackId == null || haystackId.isEmpty()) {
            nextHaystackId = loader.getAnyHaystackId(random);
        }
        nextHaystack = loader.load(nextHaystackId);
        if (riddleId == null || riddleId.isEmpty()) {
            nextRiddle = nextHaystack.getRiddle(random);
        } else {
            nextRiddle = nextHaystack.getRiddle(riddleId, random);
        }
        return new Fate(nextRiddle, nextHaystack, nextHaystackId);
    }
}
