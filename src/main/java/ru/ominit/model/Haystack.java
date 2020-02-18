package ru.ominit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import org.springframework.web.util.HtmlUtils;
import ru.ominit.highlight.EscapedHtmlString;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Александр on 30.03.2018.
 */
public class Haystack {
    @JacksonXmlProperty(localName = "wheat")
    private String wheat;
    @JsonIgnore
    private String grain;
    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlElementWrapper(localName = "riddles")
    @JacksonXmlProperty(localName = "riddle")
    @JsonSetter(nulls = Nulls.AS_EMPTY)
    private List<Riddle> riddles;

    public Haystack() {
        riddles = new ArrayList<>();
    }

    public Haystack(
            String wheat,
            List<Riddle> riddles
    ) {
        this.wheat = wheat;
        this.grain = getGrain();
        this.riddles = riddles;
    }

    /**
     * Получение случайной задачи из стога
     *
     * @param rnd ГПСЧ
     * @return одну из задач этого стога
     */
    public Riddle getRiddle(Random rnd) {
        int next = rnd.nextInt(riddles.size());
        return riddles.get(next);
    }

    public String getWheat() {
        return wheat;
    }

    public String getGrain() {
        if (grain == null) {
            grain = wheat.replaceAll("\\s+", " ").trim();
        }
        return grain;
    }

    public boolean isRelevant(String attempt) {
        return getGrain().contains(attempt);
    }

    /**
     * Получение задачи из стога по идентификатору.
     *
     * @param riddleId идентификатор задачи
     * @return задачу из стога с совпадающим идентификатором или None
     */
    public Optional<Riddle> getRiddle(String riddleId) {
        for (Riddle riddle : riddles) {
            if (riddle.getId().equals(riddleId)) {
                return Optional.of(riddle);
            }
        }
        return Optional.empty();
    }

    /**
     * Получение задачи из стога по тексту загадки.
     *
     * @param needle текст загадки
     * @return задачу из стога с совпадающим текстом загадки или None
     */
    public Optional<Riddle> getRiddleByNeedle(String needle) {
        for (Riddle riddle : riddles) {
            if (riddle.getNeedle().equals(needle)) {
                return Optional.of(riddle);
            }
        }
        return Optional.empty();
    }

    public String highlightNeedle(String needle) {
        return getRiddleByNeedle(needle)
                .map(riddle -> riddle.insert(EscapedHtmlString.make(getGrain()), EscapedHtmlString.make(getWheat())))
                .orElseGet(() -> HtmlUtils.htmlEscape(getWheat()));
    }

    public void addRiddle(Riddle riddle) {
        riddles.add(riddle);
    }

    public List<String> listRiddleIds() {
        return riddles
                .stream()
                .map(Riddle::getId)
                .collect(Collectors.toList());
    }

    public List<Riddle> listRiddles() {
        return Collections.unmodifiableList(riddles);
    }

    public static Haystack DEFAULT() {
        ArrayList<Riddle> riddles = new ArrayList<>();
        riddles.add(Riddle.DEFAULT());
        return new Haystack("жизнь", riddles);
    }
}
