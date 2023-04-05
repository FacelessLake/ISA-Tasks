package ru.nsu.belozerov;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Phaser;

public class TaskEight {
    static class Leibniz implements Runnable {
        private int startPoint;
        private int endPoint;
        private final int interval;
        private double pi = 0;
        private final Phaser phaser;

        public Leibniz(int start, int end, int interval, Phaser phaser) {
            startPoint = start;
            endPoint = end;
            this.interval = interval;
            this.phaser = phaser;
        }

        @Override
        public void run() {
            while (!phaser.isTerminated()){
                for (int k = startPoint; k <= endPoint; ++k) {
                    pi += Math.pow(-1, k) / (2 * k + 1);
                }
                startPoint += interval;
                endPoint += interval;
                phaser.arriveAndAwaitAdvance();
            }
        }

        public double getPi() {
            return pi;
        }
    }

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        Scanner scanner = new Scanner(System.in);
        int threadCount = Integer.parseInt(scanner.nextLine());

        int length = 1000;
        Phaser phaser = new Phaser(threadCount);
        ArrayList<Thread> threadArr = new ArrayList<>();
        ArrayList<Leibniz> taskArr = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            int start = length * i;
            int end = start + length - 1;
            Leibniz task = new Leibniz(start, end, length * threadCount, phaser);
            taskArr.add(task);
            threadArr.add(new Thread(task));
        }
        for (Thread thread : threadArr) {
            thread.start();
        }

        Thread hook = new Thread(() -> {
            phaser.forceTermination();
            double answer = 0;
            for (Leibniz task : taskArr) {
                answer += task.getPi();
            }
            System.out.println("Pi is: " + answer * 4);
        });

        Runtime.getRuntime().addShutdownHook(hook);
    }
}
