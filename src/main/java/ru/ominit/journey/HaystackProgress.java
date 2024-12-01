package ru.ominit.journey;

import ru.ominit.model.Haystack;
import ru.ominit.model.Riddle;
import ru.ominit.model.Verdict;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Александр on 09.06.2018.
 */
public class HaystackProgress {
    private Map<String, RiddleProgress> riddlesProgress = new HashMap<>();
    private String haystackId;
    private Haystack haystack;

    public Map<String, RiddleProgress> getRiddlesProgress() {
        return Collections.unmodifiableMap(riddlesProgress);
    }

    public String getHaystackId() {
        return haystackId;
    }

    public HaystackProgress(Haystack haystack, String haystackId) {
        this.haystackId = haystackId;
        this.haystack = haystack;

        List<Riddle> riddles = haystack.listRiddles();
        for (Riddle riddle : riddles) {
            riddlesProgress.put(riddle.getId(), RiddleProgress.forRiddle(riddle));
        }
    }

    public int maxProgress() {
        return riddlesProgress.size();
    }

    public int currentProgress() {
        return (int) riddlesProgress.values().stream().filter(RiddleProgress::isPartiallySolved).count();
    }

    public int maxAnswerProgress() {
        return haystack.listRiddles().stream().map(r -> r.listAnswers().size()).mapToInt(Integer::intValue).sum();
    }

    public int currentAnswerProgress() {
        return riddlesProgress.values().stream().map(RiddleProgress::countMatching).mapToInt(Integer::intValue).sum();
    }

    public void update(Verdict verdict) {
        if (!verdict.haystackId.equals(haystackId)) {
            return;
        }
        if (!riddlesProgress.containsKey(verdict.riddleId)) {
            return;
        }
        RiddleProgress progress = riddlesProgress.get(verdict.riddleId);
        progress.update(verdict);
    }
}
