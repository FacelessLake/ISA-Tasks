package ru.nsu.belozerov;

public class TaskTen {
    static final Object lock = new Object();
    private static class MyTread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                synchronized (lock){
                    System.out.println("Father?");
                    lock.notify();
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            synchronized (lock){
                lock.notifyAll();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new MyTread().start();
        for (int i = 0; i < 10; i++) {
            synchronized (lock){
                System.out.println("Son?");
                lock.notify();
                lock.wait();
            }
        }
        synchronized (lock){
            lock.notifyAll();
        }
    }
}
