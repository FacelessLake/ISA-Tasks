package ru.nsu.belozerov;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskSixteen {

    public static class MyTask implements Runnable {
        private final java.io.BufferedReader reader;
        private final java.util.concurrent.LinkedBlockingQueue<String> queue;

        public MyTask(java.io.BufferedReader reader, java.util.concurrent.LinkedBlockingQueue<String> queue) {
            this.reader = reader;
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                String line = reader.readLine();
                while (line != null) {
                    queue.put(line);
                    line = reader.readLine();
                }
            } catch (java.io.IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner inScanner = new Scanner(System.in);
        HttpURLConnection con = (HttpURLConnection) new URL(inScanner.nextLine()).openConnection();
        con.setRequestMethod("GET");
        con.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        MyTask taskRead = new MyTask(reader, queue);
        Thread readingThread = new Thread(taskRead);
        readingThread.start();

        int count = 0;
        String content = queue.take();
        while (content!= null) {
            if (count < 25) {
                count++;
                System.out.println(content);
            } else {
                count = 0;
                System.out.println("Press enter to scroll down, press q to quit");
                String userKey = inScanner.nextLine();
                if (userKey.equals("q")) {
                    break;
                }
            }
            content = queue.poll(100, java.util.concurrent.TimeUnit.MILLISECONDS);
        }
    }
}
