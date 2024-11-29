package ru.ominit.journey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.ominit.diskops.RiddleLoaderService;
import ru.ominit.model.Haystack;
import ru.ominit.model.Verdict;

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

    public Journey getJourney(String sessionId) {
        logger.info("Get journey for user session {}", sessionId);
        if (!journeys.containsKey(sessionId)) {
            logger.info("Create journey for user session {}", sessionId);
            journeys.put(sessionId, new Journey(sessionId));
        }
        return journeys.get(sessionId);
    }

    public Map<String, HaystackProgress> reportProgress(String sessionId) {
        Journey journey = getJourney(sessionId);
        List<Verdict> stepList = journey.getVerdicts();
        Set<String> haystackIdSet = new HashSet<>();
        for (Verdict verdict : stepList) {
            haystackIdSet.add(verdict.haystackId);
        }

        Map<String, HaystackProgress> progressMap = new HashMap<>();
        for (String id : haystackIdSet) {
            Optional<Haystack> haystackOpt = loader.loadOptional(id);
            if (haystackOpt.isPresent()) {
                progressMap.put(id, new HaystackProgress(haystackOpt.get(), id));
            } else {
                haystackIdSet.remove(id);
            }
        }
        for (Verdict verdict : stepList) {
            if (progressMap.containsKey(verdict.haystackId)) {
                progressMap.get(verdict.haystackId).update(verdict);
            }
        }
        return progressMap;
    }
}
