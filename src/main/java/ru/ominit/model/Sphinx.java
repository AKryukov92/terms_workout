package ru.ominit.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ominit.diskops.RiddleLoaderService;
import ru.ominit.journey.Journey;

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
    private RiddleLoaderService loader;
    private Random random;

    @Autowired
    public Sphinx(RiddleLoaderService loader, Random random) {
        this.loader = loader;
        this.random = random;
    }

    public Verdict skip(String lastHaystackId, String lastRiddleId) {
        Fate past = determine(lastHaystackId, lastRiddleId).orElseGet(this::random);
        Fate future = random(lastHaystackId);
        return past.skippedVerdict(future);
    }

    public Verdict decide(String lastHaystackId, String lastRiddleId, String originalAttempt, Journey journey) {
        Fate past = determine(lastHaystackId, lastRiddleId).orElseGet(this::random);
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
        boolean isCorrect = past.getRiddle().isCorrect(attempt);
        if (isCorrect) {
            logger.debug("Attempt was correct.");
            Fate future = determine(past.getHaystackId(), past.getNextRiddleId())
                    .orElseGet(() -> random(past.getHaystackId()));
            future.failIfNotRelevant();
            return past.correctVerdict(attempt, future);
        } else {
            logger.debug("Attempt was incorrect");
            boolean isNeedLess = past.getRiddle().isNeedLess(attempt);
            if (isNeedLess) {
                logger.debug("Though, we can give user 'need less' hint");
                return past.needLessVerdict(attempt);
            } else if (attempt.length() > 3) {
                boolean isNeedMore = past.getRiddle().isNeedMore(attempt);
                if (isNeedMore) {
                    logger.debug("Though, we can give user 'need more' hint");
                    return past.needMoreVerdict(attempt);
                }
            }
            return past.incorrectVerdict(attempt);
        }
    }

    public Verdict decide(String haystackId, String riddleId) {
        if (haystackId.isEmpty()) {
            logger.info("Some of identifiers was empty");
            Fate fate = random();
            fate.failIfNotRelevant();
            return fate.freshVerdict();
        } else {
            logger.debug("Pick riddle with id {} in haystackId {}", riddleId, haystackId);
            Fate fate = determine(haystackId, riddleId).orElseGet(() -> random(haystackId));
            fate.failIfNotRelevant();
            return fate.freshVerdict();
        }
    }

    private Fate random(String haystackId) {
        logger.debug("Selecting random riddle in haystack " + haystackId);
        try {
            Haystack haystack = loader.load(haystackId);
            Riddle nextRiddle = haystack.getRiddle(random);
            return new Fate(nextRiddle, haystack, haystackId);
        } catch (IOException e) {
            logger.warn("Random selection failed. Return default fate", e);
            return new Fate(Riddle.DEFAULT(), Haystack.DEFAULT(), "default");
        }
    }

    private Fate random() {
        logger.debug("Selecting random riddle");
        return loader.getAnyHaystackId(random).map(nextHaystackId -> {
            try {
                logger.debug("Pick random riddle in haystackId {}", nextHaystackId);
                Haystack nextHaystack = loader.load(nextHaystackId);
                Riddle nextRiddle = nextHaystack.getRiddle(random);
                return new Fate(nextRiddle, nextHaystack, nextHaystackId);
            } catch (IOException e) {
                logger.error("Random selection failed. Return default fate", e);
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
            logger.debug("Pick riddle with id {} in haystackId {}", riddleId, haystackId);
            Haystack nextHaystack = loader.load(haystackId);
            Optional<Riddle> nextRiddle = nextHaystack.getRiddle(riddleId);
            logger.debug(nextRiddle.map(r -> "riddle was found").orElse("riddle was not found"));
            return nextRiddle.map(r -> new Fate(r, nextHaystack, haystackId));
        } catch (IOException e) {
            logger.error("Failed to load from disk haystackId {} riddleId {}. Fate is uncertain.", haystackId, riddleId);
            return Optional.empty();
        }
    }
}
