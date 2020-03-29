package ru.ominit.journey;

import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author akryukov
 * 10.05.2018
 */
public class MultipleAnswerRiddleProgress extends RiddleProgress {
    private final Riddle riddle;
    private Set<Answer> matching;
    private Set<Answer> minimal;
    private Set<Answer> maximal;
    private List<Answer> answers;

    public MultipleAnswerRiddleProgress(Riddle riddle) {
        this.riddle = riddle;
        matching = new HashSet<>();
        minimal = new HashSet<>();
        maximal = new HashSet<>();
        answers = riddle.listAnswers();
    }

    @Override
    public String getRiddleType() {
        return "multi";
    }

    @Override
    public String getRiddleNeedle() {
        return riddle.getNeedle();
    }

    public int countMatching() {
        return matching.size();
    }

    public int countMinimal() {
        return minimal.size();
    }

    public int countMaximal() {
        return maximal.size();
    }

    public int countTotal() {
        return answers.size();
    }

    @Override
    public void update(Step step) {
        for (Answer a : answers) {
            String[] attemptTokens = step.attempt.split(" +");
            if (a.matches(attemptTokens)) {
                matching.add(a);
            }
            if (a.isMinimal(attemptTokens)) {
                minimal.add(a);
            }
            if (a.isMaximal(attemptTokens)) {
                maximal.add(a);
            }
        }
    }
}
