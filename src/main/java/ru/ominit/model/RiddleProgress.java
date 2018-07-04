package ru.ominit.model;

/**
 * Created by Александр on 09.06.2018.
 */
public abstract class RiddleProgress {

    public abstract String getRiddleType();

    public abstract String getRiddleNeedle();

    public abstract void update(Step step);

    public static RiddleProgress forRiddle(Riddle riddle){
        if (riddle.hasMultipleAnswers()){
            return new MultipleAnswerRiddleProgress(riddle);
        } else {
            return new SingleAnswerRiddleProgress(riddle);
        }
    }
}
