package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Александр on 30.03.2018.
 */
public class Riddle {
    private String id;
    private String needle;
    private List<Answer> answers;
    private String nextId;

    public Riddle(
        @JacksonXmlProperty(localName = "id") String id,
        @JacksonXmlProperty(localName = "needle") String needle,
        @JacksonXmlProperty(localName = "next") String nextId
    ) {
        this.id = id;
        this.needle = needle;
        this.nextId = nextId;
        this.answers = new ArrayList<>();
    }

    @JacksonXmlProperty(localName = "answer")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "answer")
    public void addAnswer(Answer answer){
        answers.add(answer);
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

    public boolean isCorrect(String attempt) {
        for (Answer answer : answers) {
            return answer.matches(attempt);
        }
        return false;
    }

    public boolean isRelevant(String haystack) {
        for (Answer answer : answers) {
            if (!answer.relevantTo(haystack)) {
                return false;
            }
        }
        return true;
    }

    public static Riddle DEFAULT(){
        return new Riddle("default", "смысл", "");
    }
}
