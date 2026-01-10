package com.systems.demo.apnewsdemo.multi.threading.excutor.service;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Executor;

@Slf4j
public class ProductionSafeExecutor {

  // COUNTER to observe how often CallerRunsPolicy gets invoked
  private static final AtomicLong callerRunsInvocations = new AtomicLong(0);

  // Custom ThreadFactory to name threads (helps debugging + monitoring)
  static ThreadFactory namedThreadFactory(String prefix) {
    AtomicInteger seq = new AtomicInteger(1);
    return r -> {
      Thread t = new Thread(r);
      t.setName(prefix + seq.getAndIncrement());
      t.setDaemon(false); // keep JVM alive until tasks complete
      return t;
    };
  }

  public static ExecutorService createSimpleThreadPoolExecutor(){
     return Executors.newFixedThreadPool(10,namedThreadFactory("simple-thread-pool-"));
  }

  public static ExecutorService newExecutor() {
    int cores = Runtime.getRuntime().availableProcessors();

    int corePoolSize = cores;          // CPU-bound default
    int maxPoolSize  = cores * 2;      // allow burst
    long keepAliveSeconds = 30;

    BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10); // ✅ bounded

    // wrap CallerRunsPolicy so we can count/log invocations, then delegate to it
    RejectedExecutionHandler baseRejection = new ThreadPoolExecutor.CallerRunsPolicy();
    RejectedExecutionHandler monitoringRejection = (r, exec) -> {
      callerRunsInvocations.incrementAndGet();
      log.warn("CallerRunsPolicy invoked for task: {}", r);
      baseRejection.rejectedExecution(r, exec);
    };



    ThreadPoolExecutor exec = new ThreadPoolExecutor(
        corePoolSize,
        maxPoolSize,
        keepAliveSeconds, TimeUnit.SECONDS,
        queue,
        namedThreadFactory("payment-worker-"),
        monitoringRejection
    );

    // Optional: allow core threads to time out (reduces idle resource usage)
    exec.allowCoreThreadTimeOut(true);

    return exec;
  }

  // Graceful shutdown helper (use on @PreDestroy in Spring, or shutdown hook)
  public static void shutdownGracefully(ExecutorService executor) {
    executor.shutdown(); // stop accepting new tasks
    try {
      if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
        executor.shutdownNow(); // interrupt running tasks
        if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
          System.err.println("Executor did not terminate");
        }
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  // --- Monitoring helpers ---

  // Print basic ThreadPoolExecutor metrics and approximate thread states for named threads
  public static void printStats(ThreadPoolExecutor exec, String threadNamePrefix) {
    if (exec == null) {
      log.warn("Executor is null");
      return;
    }
    int poolSize = exec.getPoolSize();
    int active = exec.getActiveCount();
    long completed = exec.getCompletedTaskCount();
    long taskCount = exec.getTaskCount();
    int queueSize = exec.getQueue().size();
    int queueRemaining = exec.getQueue().remainingCapacity();

    // count thread states for threads that match the prefix
    ThreadMXBean tbean = ManagementFactory.getThreadMXBean();
    ThreadInfo[] infos = tbean.getThreadInfo(tbean.getAllThreadIds());
    int waiting = 0, blocked = 0, runnable = 0;
    for (ThreadInfo ti : infos) {
      if (ti == null) continue;
      String name = ti.getThreadName();
      if (name != null && name.startsWith(threadNamePrefix)) {
        Thread.State s = ti.getThreadState();
        switch (s) {
          case WAITING:
          case TIMED_WAITING:
            waiting++; break;
          case BLOCKED:
            blocked++; break;
          case RUNNABLE:
            runnable++; break;
          default:
            break;
        }
      }
    }

    log.info("Executor stats: poolSize={}, active={}, runnableThreads={}, waitingThreads={}, blockedThreads={}, queueSize={}, queueRemaining={}, tasksSubmitted={}, tasksCompleted={}, callerRunsInvocations={}",
        poolSize, active, runnable, waiting, blocked, queueSize, queueRemaining, taskCount, completed, callerRunsInvocations.get());
  }

  public static long getCallerRunsInvocations() {
    return callerRunsInvocations.get();
  }

  // Quick demo
  public static void main(String[] args) {
    ExecutorService executorRaw = newExecutor();
    ThreadPoolExecutor executor = (ThreadPoolExecutor) executorRaw;

    for (int i = 0; i < 50; i++) {
      int id = i;
      executor.submit(() -> {
        try {
          // simulate work
            System.out.println(Thread.currentThread().getName() + " processed " + id);
            Thread.sleep(1000);


        }catch (InterruptedException e) {
          log.info(e.getMessage());
          Thread.currentThread().interrupt();
        }


      });
    }

    // Example: sample stats for a few seconds while work is running
    ScheduledExecutorService monitor = Executors.newSingleThreadScheduledExecutor();
    monitor.scheduleAtFixedRate(() -> printStats(executor, "payment-worker-"), 0, 1, TimeUnit.SECONDS);

    // let demo run for some time then shutdown monitor + executor
    try {
      Thread.sleep(10_000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    monitor.shutdownNow();
    shutdownGracefully(executor);
    log.info("Total CallerRunsPolicy invocations: {}", getCallerRunsInvocations());
  }
}
