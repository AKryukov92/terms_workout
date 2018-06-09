package ru.ominit.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Александр on 09.06.2018.
 */
public class HaystackProgress {
    private Map<String, RiddleProgress> riddlesProgress = new HashMap<>();
    private String haystackId;

    public HaystackProgress(Haystack haystack, String haystackId) {
        this.haystackId = haystackId;

        List<Riddle> riddles = haystack.listRiddles();
        for (Riddle riddle : riddles) {
            riddlesProgress.put(riddle.getId(), RiddleProgress.forRiddle(riddle));
        }
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
