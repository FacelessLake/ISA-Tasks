package ru.nsu.belozerov;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class TaskSeven {
    static class Leibniz implements Runnable {
        private final int startPoint;
        private final int endPoint;
        private double pi = 0;
        private final CyclicBarrier barrier;

        public Leibniz(int start, int end, CyclicBarrier barrier) {
            startPoint = start;
            endPoint = end;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            for (int k = startPoint; k < endPoint; k++) {
                pi += Math.pow(-1, k) / (2 * k + 1);
            }
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }

        public double getPi() {
            return pi;
        }
    }

    public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
        Scanner scanner = new Scanner(System.in);
        int threadCount = Integer.parseInt(scanner.nextLine());

        int iterations = 1000000;
        CyclicBarrier barrier = new CyclicBarrier(threadCount + 1);
        ArrayList<Thread> threadArr = new ArrayList<>();
        ArrayList<Leibniz> taskArr = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            int length = iterations / threadCount;
            int start = length * i;
            int end = i + 1 == threadCount ? iterations : length * (i + 1);
            Leibniz task = new Leibniz(start, end, barrier);
            taskArr.add(task);
            threadArr.add(new Thread(task));
        }
        for (Thread thread : threadArr) {
            thread.start();
        }
        barrier.await();
        double answer = 0;
        for (Leibniz task : taskArr) {
            answer += task.getPi();
        }
        System.out.println("Pi is: " + answer * 4);
    }
}
