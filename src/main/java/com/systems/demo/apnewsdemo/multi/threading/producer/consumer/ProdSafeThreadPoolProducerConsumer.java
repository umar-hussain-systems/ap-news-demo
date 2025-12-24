package com.systems.demo.apnewsdemo.multi.threading.producer.consumer;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProdSafeThreadPoolProducerConsumer {

  public static void main(String[] args) throws InterruptedException {

    SharedResource shared = new SharedResource();

    // Consumers: long-running tasks => keep a small fixed pool
    ThreadPoolExecutor consumerPool = new ThreadPoolExecutor(
        2,                           // core
        2,                           // max (fixed)
        0L, TimeUnit.MILLISECONDS,   // keepAlive (irrelevant for fixed)
        new ArrayBlockingQueue<>(10),// bounded task queue
        namedFactory("consumer-"),
        new ThreadPoolExecutor.AbortPolicy() // reject if queue full
    );
    consumerPool.prestartAllCoreThreads(); // optional: start consumer threads early

    // Producers: short-lived tasks => allow small burst, bounded queue
    ThreadPoolExecutor producerPool = new ThreadPoolExecutor(
        2,                           // core
        4,                           // max (burst)
        30L, TimeUnit.SECONDS,       // keepAlive for extra threads above core
        new ArrayBlockingQueue<>(20), // bounded task queue
        namedFactory("producer-"),
        new ThreadPoolExecutor.CallerRunsPolicy() // backpressure
    );

    int consumers = 2;
    int producers = 8; // deliberately more to show queue/backpressure
    int itemsPerProducer = 5;

    // 1) Submit consumers (long-running)
    for (int i = 0; i < consumers; i++) {
      int id = i;
      consumerPool.execute(() -> consumer(shared, id));
    }

    // 2) Submit producers (many short tasks)
    for (int i = 0; i < producers; i++) {
      int id = i;
      producerPool.execute(() -> producer(shared, id, itemsPerProducer));
    }

    // 3) Shutdown producers gracefully
    System.out.println("\n== shutdown() producers ==");
    producerPool.shutdown();

    boolean producersDone = producerPool.awaitTermination(10, TimeUnit.SECONDS);
    System.out.println("producersDone=" + producersDone);

    // 4) Stop consumers with poison pills (one per consumer)
    for (int i = 0; i < consumers; i++) {
      shared.produce(SharedResource.STOP);
    }

    // 5) Shutdown consumers gracefully
    System.out.println("\n== shutdown() consumers ==");
    consumerPool.shutdown();

    boolean consumersDone = consumerPool.awaitTermination(10, TimeUnit.SECONDS);
    System.out.println("consumersDone=" + consumersDone);

    // 6) Force shutdown if stuck
    if (!consumersDone) {
      System.out.println("\n== shutdownNow() consumers (FORCE) ==");
      List<Runnable> dropped = consumerPool.shutdownNow();
      System.out.println("Dropped consumer queued tasks=" + dropped.size());
    }

    System.out.println("\n== Final executor stats ==");
    printStats("producerPool", producerPool);
    printStats("consumerPool", consumerPool);
  }

  // Producer task: produces N items
  static void producer(SharedResource shared, int producerId, int items) {
    try {
      for (int i = 1; i <= items; i++) {
        String item = "P" + producerId + "-item" + i;
        shared.produce(item);
        System.out.printf("[%s] produced %s%n", Thread.currentThread().getName(), item);
        Thread.sleep(50);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.printf("[%s] producer interrupted%n", Thread.currentThread().getName());
    }
  }

  // Consumer task: runs until STOP
  static void consumer(SharedResource shared, int consumerId) {
    try {
      while (true) {
        String item = shared.consume();
        if (SharedResource.STOP.equals(item)) {
          System.out.printf("[%s] got STOP, exiting%n", Thread.currentThread().getName());
          return;
        }
        System.out.printf("[%s] consumed %s%n", Thread.currentThread().getName(), item);
        Thread.sleep(120);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.printf("[%s] consumer interrupted, exiting%n", Thread.currentThread().getName());
    }
  }

  // SharedResource (single-slot buffer) using synchronized + wait/notifyAll
  static class SharedResource {
    static final String STOP = "__STOP__";

    private String data;
    private boolean available = false;

    public void produce(String value) throws InterruptedException {
      synchronized (this) {
        while (available) {
          this.wait();
        }
        data = value;
        available = true;
        this.notifyAll();
      }
    }

    public String consume() throws InterruptedException {
      synchronized (this) {
        while (!available) {
          this.wait();
        }
        String value = data;
        available = false;
        this.notifyAll();
        return value;
      }
    }
  }

  // Named threads for debugging
  static ThreadFactory namedFactory(String prefix) {
    AtomicInteger seq = new AtomicInteger(1);
    return r -> {
      Thread t = new Thread(r);
      t.setName(prefix + seq.getAndIncrement());
      t.setDaemon(false);
      return t;
    };
  }

  static void printStats(String name, ThreadPoolExecutor ex) {
    System.out.println(name + ": " +
        "poolSize=" + ex.getPoolSize() +
        ", active=" + ex.getActiveCount() +
        ", queued=" + ex.getQueue().size() +
        ", completed=" + ex.getCompletedTaskCount() +
        ", isShutdown=" + ex.isShutdown() +
        ", isTerminated=" + ex.isTerminated());
  }
}

