package ru.ominit.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author akryukov
 *         10.05.2018
 */
public class MultipleAnswerRiddleProgress extends RiddleProgress {
    private Set<Answer> matching;
    private Set<Answer> minimal;
    private Set<Answer> maximal;
    private List<Answer> answers;

    public MultipleAnswerRiddleProgress(Riddle riddle) {
        matching = new HashSet<>();
        minimal = new HashSet<>();
        maximal = new HashSet<>();
        answers = riddle.listAnswers();
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

    @Override
    public void update(Step step) {
        for (Answer a : answers) {
            if (a.matches(step.attempt)) {
                matching.add(a);
            }
            if (a.isMinimal(step.attempt)) {
                minimal.add(a);
            }
            if (a.isMaximal(step.attempt)) {
                maximal.add(a);
            }
        }
    }
}
