package com.systems.demo.apnewsdemo.concurrent.hashmap;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashMapDemo {


  public static void putIfAbsentDemo() throws InterruptedException {
    ConcurrentHashMap<String, String> processed = new ConcurrentHashMap<>();

    Runnable task = () -> {
      String prev = processed.putIfAbsent("idemKey-1", "DONE");
      System.out.println(Thread.currentThread().getName()
          + " inserted? " + (prev == null));
    };

    Thread t1 = new Thread(task, "T1");
    Thread t2 = new Thread(task, "T2");
    t1.start(); t2.start();
    t1.join(); t2.join();

    System.out.println("Final value = " + processed.get("idemKey-1"));
  }

  public static void main(String[] args) {

  }
}
