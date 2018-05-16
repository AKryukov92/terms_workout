package ru.ominit.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ominit.RiddleLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author akryukov
 * 10.05.2018
 */
@Service
public class JourneyManager {
    private Logger logger = LoggerFactory.getLogger(JourneyManager.class);

    @Autowired
    private RiddleLoader loader;

    private Map<String, Journey> journeys = new HashMap<>();

    public void createJourney(String sessionId){
        journeys.put(sessionId, new Journey(sessionId));
    }

    public Journey getJourney(String sessionId){
        if (!journeys.containsKey(sessionId)){
            logger.warn("Application lost user journey by session {}", sessionId);
            createJourney(sessionId);
        }
        return journeys.get(sessionId);
    }

    public void addStep(String sessionId, Verdict verdict){
        Journey journey = getJourney(sessionId);
        journey.addStep(verdict.produceStep(sessionId));
    }

    public String reportProgress(String sessionId){
        Journey journey = getJourney(sessionId);
        Map<String, Optional<Haystack>> relevantHaystacks = new HashMap<>();
        List<Step> stepList = journey.getSteps();
        for (Step step : stepList) {
            Optional<Haystack> optionalHaystack;
            if (!relevantHaystacks.containsKey(step.haystackId)){
                optionalHaystack = loader.loadOptional(step.haystackId);
                relevantHaystacks.put(step.haystackId, optionalHaystack);
            } else {
                optionalHaystack = relevantHaystacks.get(step.haystackId);
            }
            if (optionalHaystack.isPresent()){

            }
        }
        return "placeholder";
    }

}
