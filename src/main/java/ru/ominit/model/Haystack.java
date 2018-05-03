package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by Александр on 30.03.2018.
 */
public class Haystack {

    private String wheat;
    private String grain;

    @JacksonXmlElementWrapper()
    private List<Riddle> riddles;

    public Haystack(
        @JacksonXmlProperty(localName = "wheat") String wheat,
        @JacksonXmlProperty(localName = "riddles") List<Riddle> riddles
    ) {
        this.wheat = wheat;
        this.grain = wheat.replaceAll("\\s+", " ").trim();
        this.riddles = riddles;
    }

    public Riddle getRiddle(Random rnd) {
        int next = rnd.nextInt(riddles.size());
        return riddles.get(next);
    }

    public String getWheat() {
        return wheat;
    }

    public String getGrain() {
        return grain;
    }

    public boolean isRelevant(String attempt) {
        return getGrain().contains(attempt);
    }

    /**
     * Получение задачи из стога.
     *
     * @param riddleId идентификатор задачи
     * @return одну из задач этого стога
     */
    public Optional<Riddle> getRiddle(String riddleId) {
        for (Riddle riddle : riddles) {
            if (riddle.getId().equals(riddleId)) {
                return Optional.of(riddle);
            }
        }
        return Optional.empty();
    }

    public static Haystack DEFAULT(){
        ArrayList<Riddle> riddles = new ArrayList<>();
        riddles.add(Riddle.DEFAULT());
        return new Haystack("жизнь", riddles);
    }
}
