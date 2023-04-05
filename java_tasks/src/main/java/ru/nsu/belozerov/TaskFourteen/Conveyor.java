package ru.nsu.belozerov.TaskFourteen;

import java.util.concurrent.Semaphore;

public class Conveyor {
    private final Semaphore storageWidgetA;
    private final Semaphore storageWidgetB;
    private final Semaphore storageWidgetC;
    private int widgetCount;

    public Conveyor() {
        storageWidgetA = new Semaphore(0);
        storageWidgetB = new Semaphore(0);
        storageWidgetC = new Semaphore(0);
        widgetCount = 0;
    }

    public void start() {
        new Thread(new Creator(this, WidgetType.A)).start();
        new Thread(new Creator(this, WidgetType.B)).start();
        new Thread(new Creator(this, WidgetType.C)).start();

        while (true) {
            createAandB();
            try {
                takeWidgetFromStorage(WidgetType.C);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Widgets created: " + ++widgetCount);
        }

    }

    void moveWidgetToStorage(WidgetType widgetType) {
        switch (widgetType) {
            case A -> storageWidgetA.release();
            case B -> storageWidgetB.release();
            case C -> storageWidgetC.release();
        }
    }

    void takeWidgetFromStorage(WidgetType widgetType) throws InterruptedException {
        switch (widgetType) {
            case A -> storageWidgetA.acquire();
            case B -> storageWidgetB.acquire();
            case C -> storageWidgetC.acquire();
        }
    }

    void createAandB() {
        try {
            takeWidgetFromStorage(WidgetType.A);
            takeWidgetFromStorage(WidgetType.B);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
