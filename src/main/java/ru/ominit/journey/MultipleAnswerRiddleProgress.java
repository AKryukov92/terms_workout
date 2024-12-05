package ru.ominit.journey;

import ru.ominit.model.Answer;
import ru.ominit.model.Riddle;
import ru.ominit.model.Verdict;

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

    public MultipleAnswerRiddleProgress(Riddle riddle) {
        this.riddle = riddle;
        matching = new HashSet<>();
        minimal = new HashSet<>();
        maximal = new HashSet<>();
    }

    @Override
    public String getRiddleType() {
        return "multi";
    }

    @Override
    public String getRiddleNeedle() {
        return riddle.getNeedle();
    }

    @Override
    public boolean isFullySolved() {
        return matching.size() == riddle.listAnswers().size();
    }

    @Override
    public boolean isPartiallySolved() {
        return !matching.isEmpty();
    }

    @Override
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
        return riddle.listAnswers().size();
    }

    @Override
    public void update(Verdict verdict) {
        for (Answer a : riddle.listAnswers()) {
            String[] attemptTokens = verdict.lastAttemptText.split("\\s+");
            String[] contextTokens = verdict.lastAttemptContext.split("\\s+");
            if (a.matches(attemptTokens, contextTokens)) {
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
