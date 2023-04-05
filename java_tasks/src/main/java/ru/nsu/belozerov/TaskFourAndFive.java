package ru.nsu.belozerov;

import static java.lang.Thread.sleep;

public class TaskFourAndFive {
    static class Task implements Runnable {
        @Override
        public void run() {
            int i = 0;
            while(true) {
                System.out.println("[" + Thread.currentThread().getName() + "] counter is " + i);
                i++;
                if (Thread.interrupted()) {
                    System.out.println("This thread was interrupted!");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final Task task = new Task();
        Thread myTread = new Thread(task);
        myTread.start();
        sleep(2000);
        myTread.interrupt();
    }
}

