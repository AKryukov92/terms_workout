package ru.ominit;

import java.util.UUID;

/**
 * Created by Александр on 31.03.2018.
 */
public class RiddleIdGenerator {
    public static void main(String[] args) {
        UUID id = UUID.randomUUID();
        System.out.println(id);
    }
}
