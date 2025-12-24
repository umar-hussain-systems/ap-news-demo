package com.systems.demo.apnewsdemo.multi.threading.lifcycle.demo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class LifeCycleDemo {
  static final Object lock = new Object();

  public static void threadLogger(Thread thread) {
    log.info("thread: name {}, state: {}",thread.getName(), thread.getState());
  }

  public static void main(String[] args) throws Exception {

    Thread t1 = new Thread(() -> {
      synchronized (lock) {
        try {
          lock.wait(); // WAITING
        } catch (InterruptedException e) {}
      }
    });

    t1.setName("thread1");

    threadLogger(t1);

    Thread t2 = new Thread(() -> {
      synchronized (lock) {
        lock.notify(); // wakes t1
      }
    });

    t2.setName("thread2");
    threadLogger(t2);

    t1.start(); //// NEW → RUNNABLE
    threadLogger(t1);

    Thread.sleep(1000); // TIMED_WAITING (main)

    t2.start();// NEW → RUNNABLE
    threadLogger(t1);
    threadLogger(t2);
    Thread.sleep(500);
    threadLogger(t2);
    threadLogger(t1);


  }
}

