package ru.nsu.belozerov.TaskSix;

import java.util.concurrent.BrokenBarrierException;

public class Main {
    public static void main(String[] arg) throws BrokenBarrierException, InterruptedException {
        Company company = new Company(6);
        Founder founder = new Founder(company);
        founder.start();
    }
}
