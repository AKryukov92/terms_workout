package ru.ominit.journey;

import ru.ominit.model.Haystack;
import ru.ominit.model.Riddle;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Александр on 09.06.2018.
 */
public class HaystackProgress {
    private Map<String, RiddleProgress> riddlesProgress = new HashMap<>();
    private String haystackId;
    private String wheat;

    public Map<String, RiddleProgress> getRiddlesProgress() {
        return Collections.unmodifiableMap(riddlesProgress);
    }

    public String getHaystackId() {
        return haystackId;
    }

    public String getWheat() {
        return wheat;
    }

    public HaystackProgress(Haystack haystack, String haystackId) {
        this.haystackId = haystackId;

        this.wheat = haystack.getWheat();

        List<Riddle> riddles = haystack.listRiddles();
        for (Riddle riddle : riddles) {
            riddlesProgress.put(riddle.getId(), RiddleProgress.forRiddle(riddle));
        }
    }

    public int maxProgress() {
        return riddlesProgress.size();
    }

    public int currentProgress() {
        return (int) riddlesProgress.values().stream().filter(RiddleProgress::isSolved).count();
    }

    public void update(Step step) {
        if (!step.haystackId.equals(haystackId)) {
            return;
        }
        if (!riddlesProgress.containsKey(step.riddleId)) {
            return;
        }
        RiddleProgress progress = riddlesProgress.get(step.riddleId);
        progress.update(step);
    }
}
