package ru.nsu.belozerov;

import java.util.*;

public class TaskTwelve {
    static final List<String> list = new ArrayList<>();
    static Scanner in = new Scanner(System.in);

    private static class MyTread extends Thread {
        @Override
        public void run() {
            while (true) {
                synchronized (list) {
                    Collections.sort(list);
                }
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static List<String> split(String s, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        for (int i = 0; i < s.length(); i += chunkSize) {
            chunks.add(s.substring(i, Math.min(s.length(), i + chunkSize)));
        }
        return chunks;
    }

    public static void main(String[] args) {
        MyTread tread = new MyTread();
        tread.start();
        while (true) {
            String string = in.nextLine();
            if (string.equals("")) {
                synchronized (list) {
                    System.out.println(list);
                }
            } else {
                synchronized (list) {
                    list.addAll(split(string, 80));
                }
            }
        }
    }
}
