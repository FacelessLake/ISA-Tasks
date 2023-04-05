package ru.nsu.belozerov;

import static java.lang.Thread.sleep;

public class TaskOneAndTwo {
    private static class MyTread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                System.out.println("Oh, hi");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MyTread().start();
        sleep(100);
        for (int i = 0; i < 10; i++) {
            System.out.println("Oh, bye");
        }
    }
}
