package com.systems.demo.apnewsdemo.multi.threading.producer.consumer;

import lombok.extern.slf4j.Slf4j;

class SharedResource {
  private int data;
  private boolean available = false;

  synchronized void produce(int value) throws InterruptedException {
    while (available) {
      wait();
    }
    data = value;
    available = true;
    System.out.println("Produced: " + value);
    //notifyAll();
    notify();
  }

  synchronized int consume() throws InterruptedException {
    while (!available) {
      wait();
    }
    available = false;
    System.out.println("Consumed: " + data);
    notify();
    return data;
  }
}

@Slf4j
class ThreadLifeCycleDemo {

  public static void threadLogger(Thread thread) {
    log.info("thread: name {}, state: {}",thread.getName(), thread.getState());
  }

  public static void main(String[] args) throws Exception {
    SharedResource resource = new SharedResource();

    Thread t1 = new Thread(() -> {
      try {
        resource.produce(10);
      } catch (InterruptedException e) {}
    });
    t1.setName("T1");

    Thread t2 = new Thread(() -> {
      try {
        Thread.sleep(2000); // TIMED_WAITING
        resource.consume();
      } catch (InterruptedException e) {}
    });

    t2.setName("T2");


    Thread t3 = new Thread(() -> {
      try {
        resource.produce(5);
      } catch (InterruptedException e) {}
    });
    t3.setName("T3");

    Thread t4 = new Thread(() -> {
      try {
        resource.produce(15);
      } catch (InterruptedException e) {}
    });
    t4.setName("T4");

   threadLogger(t1);
    threadLogger(t2);
    threadLogger(t3);
    threadLogger(t4);
    t1.start();  // NEW → RUNNABLE
    t3.start();
    t2.start();  // NEW → RUNNABLE
    threadLogger(t1);
    threadLogger(t2);
    threadLogger(t3);
    threadLogger(t4);

    t1.join();   // main thread WAITING
    t2.join();
    t3.join();

    // At this point, available is true (set by T3) and no consumer remains.
    // Starting T4 will block inside produce(), and main.join(T4) will now deadlock.
    System.out.println("Starting T4 to simulate deadlock; main will wait on T4.");
    threadLogger(t4);
    t4.start();
    threadLogger(t4);

    t4.join();   // main thread WAITING forever

    System.out.println("This line is never reached because of the deadlock");
  }
}
