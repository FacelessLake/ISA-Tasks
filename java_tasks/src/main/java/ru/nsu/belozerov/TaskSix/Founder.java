package ru.nsu.belozerov.TaskSix;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class Founder {
    private final List<Worker> workers;
    private final Company company;
    private final CyclicBarrier barrier;

    public Founder(Company company) {
        this.company = company;
        barrier = new CyclicBarrier(company.getDepartmentsCount()+1);
        workers = new ArrayList<>(company.getDepartmentsCount());
        for (int i = 0; i < company.getDepartmentsCount(); i++) {
            workers.add(i, new Worker(company.getFreeDepartment(i), barrier));
        }
    }

    public void start() throws BrokenBarrierException, InterruptedException {
        for (Worker worker : workers) {
            new Thread(worker).start();
        }
        barrier.await();
        company.showCollaborativeResult();
    }
}
