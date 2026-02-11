package com.systems.demo.apnewsdemo.configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {

  @Bean(name = "paymentExecutor", destroyMethod = "shutdown")
  public ExecutorService paymentExecutor() {
    int cores = Runtime.getRuntime().availableProcessors();

    return new ThreadPoolExecutor(
        cores,
        cores * 2,
        30, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(500), // bounded queue

        //creating thread factory directly here
        new ThreadFactory() {
          private final AtomicInteger seq = new AtomicInteger();
          public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("payment-worker-" + seq.incrementAndGet());
            return t;
          }
        },
        new ThreadPoolExecutor.CallerRunsPolicy() // backpressure
    );
  }

  @Bean(name = "visitExecutor")
  public ExecutorService visitExecutor() {
    return new ThreadPoolExecutor(
        4, 4,
        0L, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<>(200),
        new NamedThreadFactory("visit-worker-"),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
  }
  @Bean(name = "sessionExecutor", destroyMethod = "shutdown")
  public ExecutorService sessionExecutor() {
    return new ThreadPoolExecutor(
        5, 5,
        0L, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<>(300),
       new NamedThreadFactory("session-worker-"),
        new ThreadPoolExecutor.CallerRunsPolicy()
    );
  }
















}
