package com.systems.demo.apnewsdemo.multi.threading;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BlockLevelSyncronizeExample {

  public static void threadLogger(Thread thread) {
    log.info("thread: name {}, state: {}",thread.getName(), thread.getState());
  }

  public static void main(String[] args) throws InterruptedException {
    SharedValue sharedValue = new SharedValue();
    Runnable consumeSharedValue = ()-> {
      try {
       int value  = sharedValue.consume();
       log.info("Consumer got {}", value);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    };

    Runnable produceSharedValue = ()-> {
      try {
        sharedValue.produce(10);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    };

    //Thread t1 = new Thread(consumeSharedValue);
    //Thread t2 = new Thread(produceSharedValue);

    //t1.start();
    //t2.start();

    ExecutorService executor = Executors.newFixedThreadPool(2);
    executor.submit(consumeSharedValue);
    executor.submit(produceSharedValue);

    executor.shutdown();
    if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
      executor.shutdownNow();
    }

    System.out.println("Main finished");




  }
}

@Slf4j
class SharedValue {
  private int value;
  private boolean available = false;

  private final Object lock = new Object();

  public void produce(int value) throws InterruptedException {
    synchronized (lock) {
      while (available) {
        lock.wait();
      }
      this.value = value;
      available = true;
      lock.notifyAll();
      log.info("Produced value: " + value);
    }

  }

  public int consume() throws InterruptedException {
    synchronized (lock) {
      while (!available) {
        lock.wait();
      }

      int v = this.value;
      this.available = false;
      lock.notifyAll();
      log.info("Consumed value: {}", v);
      return v;
    }

  }

}
