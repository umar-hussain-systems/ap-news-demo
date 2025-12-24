package com.systems.demo.apnewsdemo.multi.threading.maps;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class HashMapPractice {

}



 class FintechBalanceCHM {
  static class Account {
    final AtomicLong balance = new AtomicLong(0);
  }

  public static void main(String[] args) throws Exception {
    ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    Runnable depositTask = () -> {
      for (int i = 0; i < 10_000; i++) {
        Account acc = accounts.computeIfAbsent("user-1", k -> new Account());
        acc.balance.addAndGet(1); // atomic update
      }
    };

    Thread t1 = new Thread(depositTask);
    Thread t2 = new Thread(depositTask);

    t1.start();
    t2.start();
    t1.join();
    t2.join();

    System.out.println("Final balance = " + accounts.get("user-1").balance.get()); // 20000
  }
}
