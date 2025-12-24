package com.systems.demo.apnewsdemo.interview.security.ai.cas.retry.loop;

import java.util.concurrent.atomic.AtomicLong;

public class CASWithdrawExample {

  private final AtomicLong balance;

  CASWithdrawExample(long balanceAmount) {
    balance = new AtomicLong(balanceAmount);
  }

  public boolean withdraw(long amount,boolean delay) {

    while (true) {

      long current = balance.get();

      // 1. Check condition
      if (current < amount) {
        System.out.println("Not enough balance!");
        return false;
      }

      // 2. Compute new value
      long updated = current - amount;

      // Add a short delay so another thread can interleave and cause CAS to fail
      if(delay){
        try {
          Thread.sleep(100); // adjust duration as needed
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          return false;
        }
      }
      // 3. Try CAS
      boolean success = balance.compareAndSet(current, updated);




      if (success) {
        System.out.println("Withdrawal success! Old=" + current + " New=" + updated);
        return true;
      }

      // 4. CAS failed → someone changed balance → retry
      System.out.println("CAS failed. Retrying...");
    }
  }

  public static void main(String[] args) throws InterruptedException {
    CASWithdrawExample ex = new CASWithdrawExample(200);

    // Simulate two threads withdrawing
    Runnable task = () ->{
      ex.withdraw(30,true);

    };

    Runnable task2 = () ->{
      ex.withdraw(30,false);

    };

    Thread t1 = new Thread(task);
    Thread t2 = new Thread(task2);
    Thread t3 = new Thread(task);

    Thread t4 = new Thread(task2);

    t1.start();
    t2.start();
    t3.start();
    t4.start();
  }
}
