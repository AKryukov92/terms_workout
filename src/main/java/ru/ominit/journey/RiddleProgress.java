package ru.ominit.journey;

import ru.ominit.model.Riddle;
import ru.ominit.model.Verdict;

/**
 * Created by Александр on 09.06.2018.
 */
public abstract class RiddleProgress {

    public abstract String getRiddleType();

    public abstract String getRiddleNeedle();

    public abstract boolean isFullySolved();
    public abstract boolean isPartiallySolved();

    public abstract int countMatching();

    public abstract void update(Verdict verdict);

    public static RiddleProgress forRiddle(Riddle riddle){
        if (riddle.hasMultipleAnswers()){
            return new MultipleAnswerRiddleProgress(riddle);
        } else {
            return new SingleAnswerRiddleProgress(riddle);
        }
    }
}
