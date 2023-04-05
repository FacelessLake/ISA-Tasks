package ru.nsu.belozerov.TaskNine;

public class TaskNine {
    static Thread[] philosophers = new Thread[5];
    static Object[] forks = new Object[5];
    static boolean timeOut = true;

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
            while (timeOut) {
                if (number == 4) {
                    try {
                        eatingAndThinking(rightFork, " takes right fork number: ", leftFork, " takes left fork number: ");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        eatingAndThinking(leftFork, " takes left fork number: ", rightFork, " takes right fork number: ");
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        private void eatingAndThinking(Object leftFork, String s, Object rightFork, String s2) throws InterruptedException {
            synchronized (leftFork) {
                System.out.println("Philosopher " + number + s + number);
                synchronized (rightFork) {
                    Thread.sleep(100);
                    System.out.println("Philosopher " + number + s2 + (number + 4) % 5);
                    Thread.sleep(5000);
                }
            }
            System.out.println("Philosopher " + number + " releases both forks: " + number + " and " + (number + 4) % 5);
            Thread.sleep(5000);
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

//        Thread.sleep(30000);
//        timeOut = false;
    }
}

