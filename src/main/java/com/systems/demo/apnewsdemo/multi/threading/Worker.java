package com.systems.demo.apnewsdemo.multi.threading;

public class Worker implements Runnable {

    // A volatile flag to signal the thread to keep running or stop
    private volatile boolean running = true;

    // Optionally store a name or ID
    private final String workerName;

    public Worker(String workerName) {
        this.workerName = workerName;
    }

    /**
     * Method to signal the thread to stop gracefully.
     */
    public void stopWorker() {
        running = false;
    }

    @Override
    public void run() {
        // Real-life scenario: repeatedly do some work, then check if stop is requested
        while (running) {
            try {
                // Simulate doing some work
                System.out.println(workerName + " is working...");
                Thread.sleep(1000); // Sleep for 1 second
            } catch (InterruptedException e) {
                // If thread is interrupted, it’s often a signal to stop
                System.out.println(workerName + " was interrupted.");
                running = false;
            }
        }
        System.out.println(workerName + " has stopped.");
    }
}

