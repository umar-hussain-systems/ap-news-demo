package com.systems.demo.apnewsdemo.multi.threading.excutor.service;

import com.systems.demo.apnewsdemo.multi.threading.Worker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorManager {
    public static void main(String[] args) throws InterruptedException {
        // Create a thread pool with 2 threads
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Submit two tasks
        executor.submit(new Worker("Worker-1"));
        executor.submit(new Worker("Worker-2"));

        // Let them run for 5 seconds
        Thread.sleep(5000);

        // Ask the executor to shut down gracefully
        executor.shutdown();

        // If tasks are still running after a timeout, force shutdown
        if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        System.out.println("Main thread: Executor has stopped. Exiting...");
    }
}

