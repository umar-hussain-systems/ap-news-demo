package com.systems.demo.apnewsdemo.multi.threading.manger;

import com.systems.demo.apnewsdemo.multi.threading.Worker;

public class Manager {
    public static void main(String[] args) throws InterruptedException {
        // Create two worker instances
        Worker worker1 = new Worker("Worker-1");
        Worker worker2 = new Worker("Worker-2");

        // Wrap them in Thread objects
        Thread thread1 = new Thread(worker1);
        Thread thread2 = new Thread(worker2);

        // Start the threads
        thread1.start();
        thread2.start();

        // Let them run for 5 seconds
        Thread.sleep(5000);

        System.out.println("Main thread: Requesting workers to stop...");

        // Signal both workers to stop
        worker1.stopWorker();
        worker2.stopWorker();

        // In case they are sleeping, interrupt them so they wake up and check the running flag
        thread1.interrupt();
        thread2.interrupt();

        // Wait for threads to finish
        thread1.join();
        thread2.join();

        System.out.println("Main thread: All workers have stopped. Exiting...");
    }
}

