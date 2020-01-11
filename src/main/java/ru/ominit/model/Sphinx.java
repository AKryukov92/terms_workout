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
            return past.freshVerdict();
        }
        String attempt = originalAttempt.replaceAll("\\s+", " ").trim();
        boolean isRelevant = past.getHaystack().isRelevant(attempt);
        if (!isRelevant) {
            logger.debug("Attempt was not relevant to answer. Return same riddle");
            return past.irrelevantVerdict(attempt);
        }
        if (past.getRiddle().isCorrect(attempt)) {
            logger.debug("Attempt was correct.");
            Fate future = determine(past.getHaystackId(), past.getNextRiddleId()).orElseGet(this::guess);
            future.insane();
            return past.correctVerdict(attempt, future);
        } else {
            logger.debug("Attempt was incorrect");
            return past.incorrectVerdict(attempt);
        }
    }

    public Verdict decide(String haystackId, String riddleId) {
        if (riddleId.isEmpty()) {
            Fate fate = determine(haystackId, riddleId).orElseGet(() -> guess(haystackId));
            fate.insane();
            return fate.freshVerdict();
        } else {
            Fate fate = determine(haystackId, riddleId).orElseGet(this::guess);
            fate.insane();
            return fate.freshVerdict();
        }
    }

    private Fate guess(String haystackId) {
        logger.debug("Selecting random riddle in haystack " + haystackId);
        Haystack haystack = null;
        try {
            haystack = loader.load(haystackId);
            Riddle nextRiddle = haystack.getRiddle(random);
            return new Fate(nextRiddle, haystack, haystackId);
        } catch (IOException e) {
            logger.warn("Random selection failed. Return default fate");
            return new Fate(Riddle.DEFAULT(), Haystack.DEFAULT(), "default");
        }
    }

    private Fate guess() {
        logger.debug("Selecting random riddle");
        return loader.getAnyHaystackId(random).map(nextHaystackId -> {
            try {
                Haystack nextHaystack = loader.load(nextHaystackId);
                logger.debug("Pick random riddle in haystackId {}", nextHaystackId);
                Riddle nextRiddle = nextHaystack.getRiddle(random);
                return new Fate(nextRiddle, nextHaystack, nextHaystackId);
            } catch (IOException e) {
                logger.error(e.getMessage());
                return new Fate(Riddle.DEFAULT(), Haystack.DEFAULT(), "default");
            }
        }).orElseGet(() -> {
            logger.warn("Random selection failed. Return default fate");
            return new Fate(Riddle.DEFAULT(), Haystack.DEFAULT(), "default");
        });
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
