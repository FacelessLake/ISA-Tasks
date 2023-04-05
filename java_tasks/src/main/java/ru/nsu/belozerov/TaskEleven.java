package ru.nsu.belozerov;

import java.util.concurrent.Semaphore;

public class TaskEleven {
    static Semaphore semaphore1 = new Semaphore(1);
    static Semaphore semaphore2 = new Semaphore(1);

    private static class MyTread extends Thread {
        @Override
        public void run() {
            try {
                printString();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        public void printString() throws InterruptedException {
            for (int i = 0; i < 10; i++) {
                semaphore1.acquire();
                System.out.println("Father?");
                semaphore2.release();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        semaphore1.acquire();
        new MyTread().start();
        for (int i = 0; i < 10; i++) {
            semaphore2.acquire();
            System.out.println("Son?");
            semaphore1.release();
        }
    }
}
