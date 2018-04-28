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
    public Sphinx(RiddleLoader loader, Random random) {
        this.loader = loader;
        this.random = random;
    }

    public Verdict decide(String lastHaystackId, String lastRiddleId, String originalAttempt) throws IOException {
        Fate past = determine(lastHaystackId, lastRiddleId);
        if (originalAttempt == null || originalAttempt.isEmpty()) {
            return Verdict.makeFresh(past);
        }
        String attempt = originalAttempt.replaceAll("\\s+", " ").trim();
        boolean isRelevant = past.getHaystack().isRelevant(attempt);
        if (!isRelevant) {
            return Verdict.makeIrrelevant(attempt, past);
        }
        if (past.getRiddle().isCorrect(attempt)) {
            Fate future = predict(past.getRiddle().getNextId());
            if (future.insane()){
                throw new Error();
            }
            return Verdict.makeCorrect(attempt, future);
        } else {
            return Verdict.makeIncorrect(attempt, past);
        }
    }

    public Verdict decide(String haystackId, String riddleId) throws IOException {
        Fate fate;
        fate = determine(haystackId, riddleId);
        if (fate.insane()){
            throw new Error();
        }
        return Verdict.makeFresh(fate);
    }

    /**
     * Случайно выбирает следующий стог и берет из него задачу.
     * Если есть с указанным идентификатором, то ее. Иначе - случайную.
     * @param nextRiddleId желаемый идентификатор следующей задачи
     * @return Данные для отображения на веб-странице
     * @throws IOException если не удается открыть файл с данными
     */
    private Fate predict(String nextRiddleId) throws IOException {
        String nextHaystackId = loader.getAnyHaystackId(random);
        Haystack nextHaystack = loader.load(nextHaystackId);
        Riddle nextRiddle = nextHaystack.getRiddle(nextRiddleId, random);
        return new Fate(nextRiddle, nextHaystack, nextHaystackId);
    }

    private Fate determine(String haystackId, String riddleId) throws IOException {
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
