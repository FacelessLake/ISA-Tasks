package ru.nsu.belozerov.TaskFourteen;

public class Creator implements Runnable {
    private final int time;
    private final Conveyor conveyor;
    private final WidgetType widgetType;

    public Creator(Conveyor conveyor, WidgetType widgetType) {
        this.conveyor = conveyor;
        this.widgetType = widgetType;
        switch (widgetType) {
            case A -> time = 1000;
            case B -> time = 2000;
            case C -> time = 3000;
            default -> time = 0;
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(time);
                conveyor.moveWidgetToStorage(widgetType);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
