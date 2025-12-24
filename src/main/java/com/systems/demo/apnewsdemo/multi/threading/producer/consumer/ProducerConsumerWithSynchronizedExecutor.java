package com.systems.demo.apnewsdemo.multi.threading.producer.consumer;

import java.util.List;
import java.util.concurrent.*;

public class ProducerConsumerWithSynchronizedExecutor {

  public static void main(String[] args) throws InterruptedException {


    ConcurrentMap<String, Integer> map = new ConcurrentHashMap<>();

    SharedResource shared = new SharedResource();

    ExecutorService producerPool = Executors.newFixedThreadPool(2);
    ExecutorService consumerPool = Executors.newFixedThreadPool(2);

    System.out.println("== Pools created ==");
    System.out.println("producerPool isShutdown=" + producerPool.isShutdown() + ", isTerminated=" + producerPool.isTerminated());
    System.out.println("consumerPool isShutdown=" + consumerPool.isShutdown() + ", isTerminated=" + consumerPool.isTerminated());

    int producers = 2;
    int consumers = 2;

    // Submit consumers (they run until they receive STOP)
    for (int i = 0; i < consumers; i++) {
      int id = i;
      consumerPool.submit(() -> consumer(shared, id));
    }

    // Submit producers
    for (int i = 0; i < producers; i++) {
      int id = i;
      producerPool.submit(() -> producer(shared, id, 8));
    }

    // 1) Graceful shutdown for producer pool
    System.out.println("\n== shutdown() producers ==");
    producerPool.shutdown();

    boolean producersDone = producerPool.awaitTermination(5, TimeUnit.SECONDS);
    System.out.println("producersDone=" + producersDone);

    // Send STOP signals so consumers can exit (one STOP per consumer)
    for (int i = 0; i < consumers; i++) {
      shared.produce(SharedResource.STOP);
    }

    // 2) Graceful shutdown for consumer pool
    System.out.println("\n== shutdown() consumers ==");
    consumerPool.shutdown();

    boolean consumersDone = consumerPool.awaitTermination(5, TimeUnit.SECONDS);
    System.out.println("consumersDone=" + consumersDone);

    // 3) Force shutdown if needed
    if (!consumersDone) {
      System.out.println("\n== shutdownNow() consumers (FORCE) ==");
      List<Runnable> dropped = consumerPool.shutdownNow();
      System.out.println("Dropped queued tasks count=" + dropped.size());
    }

    System.out.println("\n== Final state ==");
    System.out.println("producerPool isShutdown=" + producerPool.isShutdown() + ", isTerminated=" + producerPool.isTerminated());
    System.out.println("consumerPool isShutdown=" + consumerPool.isShutdown() + ", isTerminated=" + consumerPool.isTerminated());
  }

  // Producer task
  static void producer(SharedResource shared, int producerId, int items) {
    try {
      for (int i = 1; i <= items; i++) {
        String item = "P" + producerId + "-item" + i;
        shared.produce(item);
        System.out.printf("[%s] produced %s%n", Thread.currentThread().getName(), item);
        Thread.sleep(80);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      System.out.printf("[%s] producer interrupted%n", Thread.currentThread().getName());
    }
  }

  // Consumer task
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

  // SharedResource using synchronized + wait/notifyAll
  static class SharedResource {

    static final String STOP = "__STOP__";

    private String data;
    private boolean available = false;

    public void produce(String value) throws InterruptedException {
      synchronized (this) {
        while (available) {
          this.wait(); // wait until consumer consumes
        }
        data = value;
        available = true;
        this.notifyAll(); // wake up consumers
      }
    }

    public String consume() throws InterruptedException {
      synchronized (this) {
        while (!available) {
          this.wait(); // wait until producer produces
        }
        String value = data;
        available = false;
        this.notifyAll(); // wake up producers
        return value;
      }
    }
  }
}

