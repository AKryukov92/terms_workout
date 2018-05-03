package ru.ominit.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ominit.RiddleLoader;

import java.io.IOException;
import java.util.Optional;
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
    public Sphinx(RiddleLoader loader, Random random) {
        this.loader = loader;
        this.random = random;
    }

    public Verdict decide(String lastHaystackId, String lastRiddleId, String originalAttempt) {
        Fate past = determine(lastHaystackId, lastRiddleId).orElseGet(this::guess);
        if (originalAttempt == null || originalAttempt.isEmpty()) {
            return Verdict.makeFresh(past);
        }
        String attempt = originalAttempt.replaceAll("\\s+", " ").trim();
        boolean isRelevant = past.getHaystack().isRelevant(attempt);
        if (!isRelevant) {
            return Verdict.makeIrrelevant(attempt, past);
        }
        if (past.getRiddle().isCorrect(attempt)) {
            Fate future = determine(past.getHaystackId(), past.getNextRiddleId()).orElseGet(this::guess);
            if (future.insane()){
                throw new InsaneTaskException();
            }
            return Verdict.makeCorrect(attempt, future);
        } else {
            return Verdict.makeIncorrect(attempt, past);
        }
    }

    public Verdict decide(String haystackId, String riddleId) {
        Fate fate = determine(haystackId, riddleId).orElseGet(this::guess);
        if (fate.insane()){
            throw new InsaneTaskException();
        }
        return Verdict.makeFresh(fate);
    }

    private Fate guess() {
        String nextHaystackId = loader.getAnyHaystackId(random);
        try {
            Haystack nextHaystack = loader.load(nextHaystackId);
            Riddle nextRiddle = nextHaystack.getRiddle(random);
            return new Fate(nextRiddle, nextHaystack, nextHaystackId);
        } catch (IOException e) {
            return new Fate(Riddle.DEFAULT(), Haystack.DEFAULT(), "default");
        }
    }

    private Optional<Fate> determine(String haystackId, String riddleId) {
        if (haystackId == null || haystackId.isEmpty()) {
            return Optional.empty();
        }
        if (riddleId == null || riddleId.isEmpty()) {
            return Optional.empty();
        }
        try {
            Haystack nextHaystack = loader.load(haystackId);
            Optional<Riddle> nextRiddle = nextHaystack.getRiddle(riddleId);
            return nextRiddle.map(r -> new Fate(r, nextHaystack, haystackId));
        } catch (IOException e) {
            return Optional.empty();
        }
    }
}
