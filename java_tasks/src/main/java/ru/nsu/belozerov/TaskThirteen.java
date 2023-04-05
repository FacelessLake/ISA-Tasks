package ru.nsu.belozerov;

import java.util.concurrent.Semaphore;

public class TaskThirteen {
    static Thread[] philosophers = new Thread[5];
    static Semaphore[] forks = new Semaphore[5];
    static boolean timeOut = true;

    static class Dinner implements Runnable {
        final Semaphore leftFork;
        final Semaphore rightFork;
        int number;

        public Dinner(int number, Semaphore[] forks) {
            this.number = number;
            leftFork = forks[number];
            rightFork = forks[(number + 4) % 5];
        }

        @Override
        public void run() {
            while (timeOut) {
                try {
                    eatingAndThinking(leftFork, rightFork);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void eatingAndThinking(Semaphore leftFork, Semaphore rightFork) throws InterruptedException {
            if (leftFork.tryAcquire()) {
                System.out.println("Philosopher " + number + " takes left fork number: " + number);
                if (rightFork.tryAcquire()) {
                    Thread.sleep(100);
                    System.out.println("Philosopher " + number + " takes right fork number: " + (number + 4) % 5);
                    System.out.println("Philosopher " + number + " is eating");
                    Thread.sleep(5000);
                    rightFork.release();
                    System.out.println("Philosopher " + number + " releases right fork number: " + (number + 4) % 5);
                }
                leftFork.release();
                System.out.println("Philosopher " + number + " releases left fork number: " + number);
            }
            Thread.sleep(5000);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            forks[i] = new Semaphore(1);
        }

        for (int i = 0; i < 5; i++) {
            Dinner task = new Dinner(i, forks);
            philosophers[i] = new Thread(task);
            philosophers[i].start();
        }
    }
}
