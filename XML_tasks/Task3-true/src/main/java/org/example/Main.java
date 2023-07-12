package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.function.Function;

public class Main {
    private static final int cacheSize = 2000;
    private static final Random random = new Random();
    private static final Function<Integer, String> producer = String::valueOf;

    public static void main(String[] args) throws IOException {
        Cache<Integer, String> cache = new Cache<>(producer);
        for (int i = 0; i < 100000000; ++i) {
            cache.getValue(getRandomKey());
        }
        FileWriter fileWriter1 = new FileWriter("phantom.csv");
        FileWriter fileWriter2 = new FileWriter("phantomLifeTime.csv");

        for (int value = 0; value < cacheSize; ++value) {
            fileWriter1.write(value+" ");
            fileWriter1.write(String.valueOf(cache.getProbability(value)));
            fileWriter1.write("\n");
            fileWriter2.write(value+", ");
            fileWriter2.write(String.valueOf(cache.getLifetime(value)));
            fileWriter2.write("\n");
        }
        fileWriter1.flush();
    }

    private static Integer getRandomKey() {
        int randomKey = (int) (random.nextGaussian() * 1500);
        if (randomKey < 0 || randomKey >= cacheSize) {
            return getRandomKey();
        }
        return randomKey;
    }
}
