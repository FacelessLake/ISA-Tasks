package ru.nsu.belozerov;

public class TaskThree {
    private static class MyRunnable implements Runnable {
        private final String[] _list;

        public MyRunnable(String[] list) {
            _list = list.clone();
            new Thread(this).start();
        }

        @Override
        public void run() {
            for (String s : _list) {
                System.out.println(s);
            }
        }
    }

    private static void init(String[] strings, String prefix) {
        for (int i = 0; i < strings.length; i++) {
            strings[i] = prefix + (i + 1);
        }
    }

    public static void main(String[] args) {
        String[] strings = new String[10];
        init(strings, "A");
        new MyRunnable(strings);
        init(strings, "B");
        new MyRunnable(strings);
        init(strings, "C");
        new MyRunnable(strings);
        init(strings, "D");
        new MyRunnable(strings);
    }
}
