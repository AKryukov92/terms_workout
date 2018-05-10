package ru.ominit.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private Logger logger = LoggerFactory.getLogger(Sphinx.class);
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
            logger.debug("Attempt was empty. Return same riddle");
            return Verdict.makeFresh(past);
        }
        String attempt = originalAttempt.replaceAll("\\s+", " ").trim();
        boolean isRelevant = past.getHaystack().isRelevant(attempt);
        if (!isRelevant) {
            logger.debug("Attempt was not relevant to answer. Return same riddle");
            return Verdict.makeIrrelevant(attempt, past);
        }
        if (past.getRiddle().isCorrect(attempt)) {
            logger.debug("Attempt was correct.");
            Fate future = determine(past.getHaystackId(), past.getNextRiddleId()).orElseGet(this::guess);
            if (future.insane()){
                logger.error("Chosen riddle was not relevant to haystack");
                throw new InsaneTaskException();
            }
            return Verdict.makeCorrect(attempt, future);
        } else {
            logger.debug("Attempt was incorrect");
            return Verdict.makeIncorrect(attempt, past);
        }
    }

    public Verdict decide(String haystackId, String riddleId) {
        Fate fate = determine(haystackId, riddleId).orElseGet(this::guess);
        if (fate.insane()){
            logger.error("Chosen riddle was not relevant to haystack");
            throw new InsaneTaskException();
        }
        return Verdict.makeFresh(fate);
    }

    private Fate guess() {
        logger.debug("Selecting random riddle");
        String nextHaystackId = loader.getAnyHaystackId(random);
        try {
            Haystack nextHaystack = loader.load(nextHaystackId);
            logger.debug("Pick random riddle in haystackId {}", nextHaystackId);
            Riddle nextRiddle = nextHaystack.getRiddle(random);
            return new Fate(nextRiddle, nextHaystack, nextHaystackId);
        } catch (IOException e) {
            return new Fate(Riddle.DEFAULT(), Haystack.DEFAULT(), "default");
        }
    }

    private Optional<Fate> determine(String haystackId, String riddleId) {
        if (haystackId == null || haystackId.isEmpty()) {
            logger.debug("HaystackId was empty. Fate is uncertain.");
            return Optional.empty();
        }
        if (riddleId == null || riddleId.isEmpty()) {
            logger.debug("RiddleId was empty. Fate is uncertain.");
            return Optional.empty();
        }
        try {
            Haystack nextHaystack = loader.load(haystackId);
            logger.debug("Search for riddleId {}", riddleId);
            Optional<Riddle> nextRiddle = nextHaystack.getRiddle(riddleId);
            logger.debug(nextRiddle.map(r -> "riddle was found").orElse("riddle was not found"));
            return nextRiddle.map(r -> new Fate(r, nextHaystack, haystackId));
        } catch (IOException e) {
            logger.error("Failed to load from disk haystackId {} riddleId {}. Fate is uncertain.", haystackId, riddleId);
            return Optional.empty();
        }
    }
}
