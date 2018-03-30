package ru.ominit.model;

/**
 * Created by Александр on 30.03.2018.
 */
public class Riddle {
    private int id;
    private String haystack;
    private String needle;
    private String minAnswer;
    private String maxAnswer;

    public Riddle(int id, String haystack, String needle, String minAnswer, String maxAnswer){
        if (!maxAnswer.contains(minAnswer)){
            throw new IllegalStateException("Полный ответ должен содержать краткий ответ");
        }
        if (!haystack.contains(maxAnswer)){
            throw new IllegalStateException("Исходный текст должен содержать полный ответ");
        }
        this.id = id;
        this.haystack = haystack;
        this.needle = needle;
        this.minAnswer = minAnswer;
        this.maxAnswer = maxAnswer;
    }

    public int getId() {
        return id;
    }

    public String getHaystack() {
        return haystack;
    }

    public String getNeedle() {
        return needle;
    }

    public boolean validate(String answer){
        return answer.contains(minAnswer) && maxAnswer.contains(answer);
    }
}
