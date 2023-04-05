package ru.nsu.belozerov.TaskNine;

public class TaskNineWithDeadLock {
    static Thread[] philosophers = new Thread[5];
    static Object[] forks = new Object[5];

    static class Dinner implements Runnable {
        final Object leftFork;
        final Object rightFork;
        int number;

        public Dinner(int number, Object[] forks) {
            this.number = number;
            leftFork = forks[number];
            rightFork = forks[(number + 4) % 5];
        }

        @Override
        public void run() {
            while (true) {
                synchronized (leftFork) {
                    System.out.println("Philosopher " + number + " takes left fork number: " + number);
                    synchronized (rightFork) {
                        System.out.println("Philosopher " + number + " takes right fork number: " + (number + 4) % 5);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        System.out.println("Philosopher " + number + " releases both forks: " + number + " and " + (number + 4) % 5);
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < 5; i++) {
            Dinner task = new Dinner(i, forks);
            philosophers[i] = new Thread(task);
            philosophers[i].start();
        }
        Thread.sleep(30000);
    }
}
