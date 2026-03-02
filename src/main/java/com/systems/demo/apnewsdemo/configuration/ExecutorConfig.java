package com.systems.demo.apnewsdemo.configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExecutorConfig {
  private static final Logger log = LoggerFactory.getLogger(ExecutorConfig.class);
  @Bean(name = "paymentExecutor", destroyMethod = "shutdown")
  public ExecutorService paymentExecutor() {


    int cores = Runtime.getRuntime().availableProcessors();

    RejectedExecutionHandler handler = (r, executor) -> {

      ThreadPoolExecutor tpe = (ThreadPoolExecutor) executor;

      log.warn(
          "PaymentExecutor REJECTED task | active={} pool={}/{} queueSize={} remainingCapacity={} completed={}",
          tpe.getActiveCount(),
          tpe.getPoolSize(),
          tpe.getMaximumPoolSize(),
          tpe.getQueue().size(),
          tpe.getQueue().remainingCapacity(),
          tpe.getCompletedTaskCount()
      );

      // CallerRuns behavior (backpressure)
      if (!executor.isShutdown()) {
        r.run();
      }
    };

    return new ThreadPoolExecutor(
        cores,
        cores * 2,
        30, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(500),
        new ThreadFactory() {
          private final AtomicInteger seq = new AtomicInteger();
          public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setName("payment-worker-" + seq.incrementAndGet());
            return t;
          }
        },
        handler
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
