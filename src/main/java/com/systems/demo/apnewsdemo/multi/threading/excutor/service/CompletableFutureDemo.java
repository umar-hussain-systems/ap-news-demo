package com.systems.demo.apnewsdemo.multi.threading.excutor.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureDemo {

  public static void main(String[] args) {
    CompletableFuture<String> future =
        CompletableFuture.supplyAsync(() -> {
          try { Thread.sleep(1000); } catch (Exception e) {}
          return "Task Completed";
        });

    future.thenAccept(System.out::println);

    ExecutorService executor = Executors.newFixedThreadPool(2);

    CompletableFuture future2 =
        CompletableFuture.supplyAsync(() -> {
          try { Thread.sleep(1000); } catch (Exception e) {}
          return "Task Completed";
        }, executor);

    future2.thenAccept(System.out::println);

    executor.shutdown();


  }
}
