package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Александр on 30.03.2018.
 */
public class Haystack {
    @JacksonXmlProperty(localName = "wheat")
    private String wheat;

    @JacksonXmlProperty(localName = "riddles")
    @JacksonXmlElementWrapper()
    private List<Riddle> riddles = new ArrayList<>();

    public Riddle getRiddle(Random rnd) {
        int next = rnd.nextInt(riddles.size());
        Riddle nextRiddle = riddles.get(next);
        assert nextRiddle.isRelevant(wheat);
        return nextRiddle;
    }

    public String getWheat() {
        return wheat;
    }

    public Riddle getRiddle(String riddleId, Random rnd) {
        for (Riddle riddle : riddles) {
            if (riddle.getId().equals(riddleId)) {
                return riddle;
            }
        }
        return getRiddle(rnd);
    }
}
