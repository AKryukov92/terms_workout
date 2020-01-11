package ru.ominit.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ominit.RiddleLoaderService;

import java.util.*;

/**
 * @author akryukov
 * 10.05.2018
 */
@Service
public class JourneyManager {
    private Logger logger = LoggerFactory.getLogger(JourneyManager.class);

    @Autowired
    private RiddleLoaderService loader;

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

    public Map<String, HaystackProgress> reportProgress(String sessionId){
        Journey journey = getJourney(sessionId);
        List<Step> stepList = journey.getSteps();
        Set<String> haystackIdSet = new HashSet<>();
        for (Step step : stepList){
            haystackIdSet.add(step.haystackId);
        }

        Map<String, HaystackProgress> progressMap = new HashMap<>();
        for (String id : haystackIdSet){
            Optional<Haystack> haystackOpt = loader.loadOptional(id);
            if (haystackOpt.isPresent()){
                progressMap.put(id, new HaystackProgress(haystackOpt.get(), id));
            } else {
                haystackIdSet.remove(id);
            }
        }
        for (Step step : stepList){
            if (progressMap.containsKey(step.haystackId)){
                progressMap.get(step.haystackId).update(step);
            }
        }
        return progressMap;
    }

}
