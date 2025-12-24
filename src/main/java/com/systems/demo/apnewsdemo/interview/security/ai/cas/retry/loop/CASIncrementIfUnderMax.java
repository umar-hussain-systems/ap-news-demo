package com.systems.demo.apnewsdemo.interview.security.ai.cas.retry.loop;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class CASIncrementIfUnderMax {

  public AtomicInteger atomicInteger = new AtomicInteger(0);

  public final Integer maxValue;

  public CASIncrementIfUnderMax(Integer maxValue) {
    this.maxValue = maxValue;
  }

  public boolean increment() {

    while (true){

      int count = atomicInteger.get();

      if (count >= maxValue) {
        return false;
      }


      if(atomicInteger.compareAndSet(count, count + 1)) {
        System.out.println("Successfull increment: value now"+atomicInteger.get());
          return true;
       }

      System.out.println("Fail increment: value now"+atomicInteger.get());

    }

  }

  public static void main(String[] args) throws InterruptedException {
    CASIncrementIfUnderMax ex = new CASIncrementIfUnderMax(5);
    int n = 6;

    CountDownLatch ready = new CountDownLatch(n);
    CountDownLatch start = new CountDownLatch(1);


    Runnable task = () -> {
      ready.countDown();
      try {
        start.await();
      } catch (Exception ignored) {

      }
      ex.increment();
    };


    for (int i = 0; i < n; i++) {
      Thread t = new Thread(task);
      t.start();
    }

    ready.await(); // all threads created and waiting
    start.countDown(); // start simultaneously
  }

}

