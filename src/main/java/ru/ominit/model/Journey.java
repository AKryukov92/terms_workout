package ru.ominit.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author akryukov
 * 10.05.2018
 */
public class Journey {
    private String sessionId;
    private List<Step> steps;

    public Journey(String sessionId) {
        this.sessionId = sessionId;
        this.steps = new ArrayList<>();
    }

    public String getSessionId() {
        return sessionId;
    }

    public void addStep(Step step){
        steps.add(step);
    }

    public List<Step> getSteps(){
        return Collections.unmodifiableList(steps);
    }
}
