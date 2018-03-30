package ru.ominit.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Александр on 30.03.2018.
 */
public class Sphinx {
    private static Map<Integer, Integer> order = new HashMap<>();
    private static Map<Integer, Riddle> riddleMap = new HashMap<>();

    public Sphinx() {
        order.put(1, 2);
        order.put(2, 3);
        order.put(3, 4);

        String haystack = "int a = 5;\ndouble b = 8.1;\nSystem.out.println(a);";
        riddleMap.put(1, new Riddle(1, haystack, "объявление переменной a", "int a", "int a = 5;"));
        riddleMap.put(2, new Riddle(2, haystack, "объявление переменной b", "double b", "double b = 8.1;"));
        riddleMap.put(3, new Riddle(3, haystack, "присвоение начального значения переменной a", "a = 5", "int a = 5;"));
        riddleMap.put(4, new Riddle(4, haystack, "присвоение начального значения переменной b", "b = 8.1", "double b = 8.1;"));
    }

    public Riddle firstRiddle() {
        return riddleMap.get(1);
    }

    public Riddle riddleById(Integer currentRiddle) {
        if (riddleMap.containsKey(currentRiddle)) {
            return riddleMap.get(currentRiddle);
        } else {
            return firstRiddle();
        }
    }

    public Riddle nextRiddle(Riddle current){
        return nextRiddle(current.getId());
    }

    public Riddle nextRiddle(Integer currentRiddle) {
        if (order.containsKey(currentRiddle)) {
            int nextIndex = order.get(currentRiddle);
            if (riddleMap.containsKey(nextIndex)) {
                return riddleMap.get(nextIndex);
            }
        }
        return firstRiddle();
    }

    public Verdict attempt(int riddleId, String attempt){
        Riddle riddle = riddleById(riddleId);
        boolean isRelevant = riddle.isRelevant(attempt);
        if (isRelevant){
            if (riddle.isCorrect(attempt)){
                return Verdict.makeCorrect(nextRiddle(riddle), attempt);
            } else {
                return Verdict.makeIncorrect(riddle, attempt);
            }
        }
        return Verdict.makeIrrelevant(riddle, attempt);
    }
}
