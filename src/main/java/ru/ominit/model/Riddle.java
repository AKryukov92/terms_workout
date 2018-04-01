package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * Created by Александр on 30.03.2018.
 */
public class Riddle {
    @JacksonXmlProperty(localName = "id")
    private String id;
    @JacksonXmlProperty(localName = "needle")
    private String needle;
    @JacksonXmlProperty(localName = "min_answer")
    private String minAnswer;
    @JacksonXmlProperty(localName = "max_answer")
    private String maxAnswer;
    @JacksonXmlProperty(localName = "next")
    private String nextId;

    public Riddle(){}

    public Riddle(String id, String haystack, String needle, String minAnswer, String maxAnswer, String nextId) {
        if (!maxAnswer.contains(minAnswer)) {
            throw new IllegalStateException("Полный ответ должен содержать краткий ответ");
        }
        if (!haystack.contains(maxAnswer)) {
            throw new IllegalStateException("Исходный текст должен содержать полный ответ");
        }
        this.id = id;
        this.needle = needle;
        this.minAnswer = minAnswer;
        this.maxAnswer = maxAnswer;
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
        return answer.contains(minAnswer) && maxAnswer.contains(answer);
    }

    public boolean isRelevant(String haystack){return haystack.contains(minAnswer) && haystack.contains(maxAnswer); }
}
