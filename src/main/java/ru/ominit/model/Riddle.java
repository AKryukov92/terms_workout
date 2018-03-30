package ru.ominit.model;

/**
 * Created by Александр on 30.03.2018.
 */
public class Riddle {
    private String id;
    private String haystack;
    private String needle;
    private String minAnswer;
    private String maxAnswer;
    private String nextId;

    public Riddle(String id, String haystack, String needle, String minAnswer, String maxAnswer, String nextId) {
        if (!maxAnswer.contains(minAnswer)) {
            throw new IllegalStateException("Полный ответ должен содержать краткий ответ");
        }
        if (!haystack.contains(maxAnswer)) {
            throw new IllegalStateException("Исходный текст должен содержать полный ответ");
        }
        this.id = id;
        this.haystack = haystack;
        this.needle = needle;
        this.minAnswer = minAnswer;
        this.maxAnswer = maxAnswer;
        this.nextId = nextId;
    }

    public String getId() {
        return id;
    }

    public String getHaystack() {
        return haystack;
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

    public boolean isRelevant(String answer) {
        return haystack.contains(answer);
    }
}
