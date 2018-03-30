package ru.ominit;

import java.io.File;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Александр on 31.03.2018.
 */
public class RiddleIdGenerator {
    public static void main(String[] args) {
        Random rnd = new Random();
        File riddlesDirectory = new File("resources/riddles");
        System.out.println(riddlesDirectory.getAbsoluteFile());
        String[] filenames = riddlesDirectory.list();
        if (filenames != null) {
            Arrays.sort(filenames, String::compareToIgnoreCase);
            int val;
            do {
                val = 10000 + rnd.nextInt(90000);
            } while (Arrays.binarySearch(filenames, val + ".xml") >= 0);
            System.out.println(val + ".xml");
        }
    }
}
