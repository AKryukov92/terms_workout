package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by Александр on 30.03.2018.
 */
public class Riddle {
    private String id;
    private String needle;
    private String minAnswer;
    private String maxAnswer;
    private String nextId;

    public Riddle(
        @JacksonXmlProperty(localName = "id") String id,
        @JacksonXmlProperty(localName = "needle") String needle,
        @JacksonXmlProperty(localName = "min_answer") String minAnswer,
        @JacksonXmlProperty(localName = "max_answer") String maxAnswer,
        @JacksonXmlProperty(localName = "next") String nextId
    ) {
        if (!maxAnswer.contains(minAnswer)) {
            throw new IllegalStateException("Полный ответ должен содержать краткий ответ");
        }
        this.id = id;
        this.needle = needle;
        this.minAnswer = minAnswer.replaceAll("\\s+", " ");
        this.maxAnswer = maxAnswer.replaceAll("\\s+", " ");
        this.nextId = nextId;
    }

    public String getId() {
        return id;
    }

    public String getNeedle() {
        return needle;
    }

    public String getNextId() {
        return nextId;
    }

    public boolean isCorrect(String answer) {
        String min = minAnswer;
        String max = maxAnswer;
        return answer.contains(min) && max.contains(answer);
    }

    public boolean isRelevant(String haystack){
        String min = minAnswer;
        String max = maxAnswer;
        return haystack.contains(min) && haystack.contains(max);
    }
}
