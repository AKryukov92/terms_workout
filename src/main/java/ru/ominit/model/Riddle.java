package ru.ominit.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Александр on 30.03.2018.
 */
public class Riddle {
    @JacksonXmlProperty(localName = "id")
    private String id;

    @JacksonXmlProperty(localName = "needle")
    private String needle;

    @JacksonXmlProperty(localName = "answer")
    @JacksonXmlElementWrapper(useWrapping = false, localName = "answer")
    private List<Answer> answers;

    @JacksonXmlProperty(localName = "next")
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
    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public boolean intersectsAnything(Answer proposedAnswer) {
        for (Answer answer : answers) {
            if (answer.intersects(proposedAnswer)) {
                return true;
            }
        }
        return false;
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

    public boolean hasMultipleAnswers(){
        return answers.size() > 1;
    }

    public List<Answer> listAnswers(){
        return Collections.unmodifiableList(answers);
    }

    public boolean isCorrect(String attempt) {
        boolean found = false;
        for (Answer answer : answers) {
            if (answer.matches(attempt)) {
                found = true;
            }
        }
        return found;
    }

    public void assertRelevant(String haystack) {
        for (Answer answer : answers) {
            if (!answer.relevantTo(haystack)) {
                throw new InsaneTaskException(haystack, needle, answer);
            }
        }
    }

    public static Riddle DEFAULT() {
        return new Riddle("default", "смысл", "");
    }
}
