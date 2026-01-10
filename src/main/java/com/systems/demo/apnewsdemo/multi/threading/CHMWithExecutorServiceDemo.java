package com.systems.demo.apnewsdemo.multi.threading;

import java.util.concurrent.*;
import java.util.*;

public class CHMWithExecutorServiceDemo {

  public static void main(String[] args) throws Exception {

    ConcurrentHashMap<String, Integer> stats = new ConcurrentHashMap<>();

    // Production-safe pool: bounded queue + CallerRunsPolicy (backpressure)
    int threads = 4;
    int queueSize = 50;

    ThreadPoolExecutor pool = new ThreadPoolExecutor(
        threads,
        threads,
        0L, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<>(queueSize),
        new ThreadFactory() {
          private final ThreadFactory def = Executors.defaultThreadFactory();
          private int n = 1;
          @Override public Thread newThread(Runnable r) {
            Thread t = def.newThread(r);
            t.setName("worker-" + (n++));
            return t;
          }
        },
        new ThreadPoolExecutor.CallerRunsPolicy() // if queue full, caller executes task
    );

    // Simulate tasks producing events
    List<String> events = List.of("LOGIN", "LOGIN", "PAYMENT", "PAYMENT", "PAYMENT", "LOGOUT");

    Runnable submitter = () -> {
      for (int i = 0; i < 10_000; i++) {
        String ev = events.get(i % events.size());
        pool.execute(() -> {
          // Atomic aggregation:
          stats.merge(ev, 1, Integer::sum);
        });
      }
    };

    Thread producer1 = new Thread(submitter, "producer-1");
    Thread producer2 = new Thread(submitter, "producer-2");

    producer1.start();
    producer2.start();
    producer1.join();
    producer2.join();

    // ExecutorService lifecycle: shutdown -> await -> optional shutdownNow
    pool.shutdown();
    boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
    if (!finished) {
      pool.shutdownNow();
    }

    System.out.println("Stats = " + stats);
  }
}