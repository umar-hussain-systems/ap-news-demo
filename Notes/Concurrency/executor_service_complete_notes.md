# ExecutorService, Future & CompletableFuture — Complete Revision Notes (Java/Spring Boot)

> Use this file as a **revision sheet** for interviews and real production work.

---

## Table of Contents

1. [Why ExecutorService](#why-executorservice)
2. [Core Concepts](#core-concepts)
3. [ThreadPoolExecutor Deep Dive](#threadpoolexecutor-deep-dive)
4. [ExecutorService Lifecycle](#executorservice-lifecycle)
5. [Production-Safe Configuration](#production-safe-configuration)
6. [Backpressure & Rejection Policies](#backpressure--rejection-policies)
7. [Graceful Shutdown Patterns](#graceful-shutdown-patterns)
8. [Future](#future)
9. [CompletableFuture](#completablefuture)
10. [Spring Boot Examples](#spring-boot-examples)
11. [Common Pitfalls](#common-pitfalls)
12. [Interview Questions We Covered](#interview-questions-we-covered)
13. [Quick Cheat Sheet](#quick-cheat-sheet)

---

## Why ExecutorService

### What problem does it solve?

Creating threads manually (`new Thread(...)`) is:

- expensive (thread creation cost)
- hard to control (too many threads)
- hard to shut down correctly
- has no backpressure (requests keep coming; memory/CPU melts)
- hard to monitor (names, metrics, queue size, rejections)

**ExecutorService** provides:

- thread reuse (pool)
- task submission API (`submit`, `execute`)
- queueing
- lifecycle control (`shutdown`, `awaitTermination`)
- rejection policies (backpressure / fail-fast)
- better observability (thread factory naming)

---

## Core Concepts

### Task vs Thread

- **Task**: unit of work (`Runnable` or `Callable`)
- **Thread**: the worker that runs tasks

Executor decouples *task submission* from *thread management*.

### Runnable vs Callable

- `Runnable`: no return value, cannot throw checked exceptions directly
- `Callable<T>`: returns `T`, can throw checked exceptions

### execute() vs submit()

- `execute(Runnable)`:
  - returns `void`
  - exceptions go to thread's UncaughtExceptionHandler (often just logs)
- `submit(...)`:
  - returns a `Future`
  - exceptions are captured and re-thrown from `Future.get()`

---

## ThreadPoolExecutor Deep Dive

### Key constructor

```java
new ThreadPoolExecutor(
  corePoolSize,
  maximumPoolSize,
  keepAliveTime,
  unit,
  workQueue,
  threadFactory,
  rejectionHandler
);
```

### What each parameter means

| Parameter | Meaning |
|---|---|
| `corePoolSize` | minimum number of threads kept alive even when idle |
| `maximumPoolSize` | upper bound threads during bursts |
| `keepAliveTime` | how long extra (non-core) threads can stay idle before being removed |
| `workQueue` | where tasks wait when all core threads are busy |
| `threadFactory` | custom naming/daemon/priority/exception handler |
| `rejectionHandler` | invoked when queue is full and pool can’t accept more |

### How tasks are accepted (important!)

When you submit a task, ThreadPoolExecutor logic is roughly:

1. If **running threads < corePoolSize** → create a new thread to run task
2. Else if **queue has space** → enqueue task
3. Else if **running threads < maxPoolSize** → create extra thread to run task
4. Else → reject task using `RejectedExecutionHandler`

This is why queue choice is critical.

### Queue types & when to use

| Queue | Typical Use | Risk |
|---|---|---|
| `ArrayBlockingQueue` (bounded) | production-safe default | can reject under load (good) |
| `LinkedBlockingQueue` (often unbounded) | avoids rejections | can grow → OOM |
| `SynchronousQueue` | direct handoff, no storage | can create many threads if max is high |
| `DelayQueue` | scheduled/delayed tasks | specialized |

---

## ExecutorService Lifecycle

ExecutorService has a lifecycle you *must* handle in production.

### States (conceptual)

- **RUNNING**: accepts new tasks and runs queued tasks
- **SHUTDOWN**: stops accepting new tasks; finishes queued tasks
- **STOP**: attempts to interrupt running tasks; drains queue
- **TERMINATED**: all tasks done and threads ended

### Lifecycle methods

#### `shutdown()`
- graceful
- stops accepting new tasks
- completes queued tasks

#### `shutdownNow()`
- aggressive
- interrupts running threads
- returns list of tasks that never started (drained from queue)

#### `awaitTermination(timeout, unit)`
- blocks current thread until terminated or timeout

### Typical shutdown flow

```java
executor.shutdown();
if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
  executor.shutdownNow();
  executor.awaitTermination(30, TimeUnit.SECONDS);
}
```

Also remember to restore interrupt:

```java
catch (InterruptedException e) {
  executor.shutdownNow();
  Thread.currentThread().interrupt();
}
```

---

## Production-Safe Configuration

### Why `Executors.newFixedThreadPool()` is dangerous

- uses unbounded `LinkedBlockingQueue`
- tasks can pile up forever → OutOfMemoryError under sustained load

### Why `Executors.newCachedThreadPool()` is dangerous

- can create unbounded number of threads
- leads to CPU exhaustion, context switching, memory pressure

### Production-safe ThreadPoolExecutor template

```java
int cores = Runtime.getRuntime().availableProcessors();

ExecutorService executor = new ThreadPoolExecutor(
  cores,                 // core
  cores * 2,             // max (burst)
  30, TimeUnit.SECONDS,  // keepAlive
  new ArrayBlockingQueue<>(1000), // bounded queue
  new NamedThreadFactory("payment-worker-"),
  new ThreadPoolExecutor.CallerRunsPolicy() // backpressure
);
```

### ThreadFactory for naming threads (debugging/monitoring)

```java
public final class NamedThreadFactory implements ThreadFactory {
  private final String prefix;
  private final AtomicInteger seq = new AtomicInteger(1);

  public NamedThreadFactory(String prefix) { this.prefix = prefix; }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    t.setName(prefix + seq.getAndIncrement());
    t.setDaemon(false);
    return t;
  }
}
```

---

## Backpressure & Rejection Policies

### What happens when more work arrives than the queue size?

When:
- the pool has reached `maximumPoolSize`
- the queue is full

The executor **cannot accept more tasks**, so it calls the `RejectedExecutionHandler`.

### Common Rejection Policies

| Policy | Behavior | When to use |
|---|---|---|
| `AbortPolicy` | throws `RejectedExecutionException` | fail-fast APIs (return 429/503) |
| `CallerRunsPolicy` | caller thread runs task | backpressure at producer |
| `DiscardPolicy` | silently drops task | only for non-critical best-effort work |
| `DiscardOldestPolicy` | drops oldest queued task then retries | rarely, for “latest wins” behavior |

### Why CallerRunsPolicy is backpressure

If your API is Spring MVC (Tomcat):

- normal: request thread submits → returns quickly (async)
- overloaded: request thread **runs the task itself**
- request threads get busy → fewer threads accept new requests
- clients experience higher latency / timeouts → input rate slows

**Important nuance**: the request thread does **not** “wait for executor capacity”.  
It becomes the worker (temporary synchronous execution) to slow the producer.

---

## Graceful Shutdown Patterns

### Pattern A: `@PreDestroy` in Spring

```java
@Component
public class ExecutorShutdown {

  private final ExecutorService executor;

  public ExecutorShutdown(ExecutorService executor) {
    this.executor = executor;
  }

  @PreDestroy
  public void stop() {
    executor.shutdown();
    try {
      if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      executor.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
```

### Pattern B: Shutdown hook (non-Spring)

```java
Runtime.getRuntime().addShutdownHook(new Thread(() -> {
  executor.shutdown();
  try {
    executor.awaitTermination(30, TimeUnit.SECONDS);
  } catch (InterruptedException ignored) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
  }
}));
```

---

## Future

### What is Future?
A handle representing the result of an asynchronous computation.

```java
Future<Integer> f = executor.submit(() -> 42);
Integer v = f.get(); // blocks
```

### Key methods

- `get()` (blocks)
- `get(timeout, unit)` (blocks with timeout)
- `cancel(true|false)`
- `isDone()`, `isCancelled()`

### Exception behavior
If the task throws, `get()` throws `ExecutionException` wrapping the original cause.

### Limitations of Future

- blocking API (hard in web threads)
- no callbacks
- no composition (combine multiple futures is painful)

---

## CompletableFuture

### Why CompletableFuture?
It supports **non-blocking pipelines** and **composition**.

### Basic examples

#### Supply + transform + consume

```java
CompletableFuture
  .supplyAsync(() -> fetch(), executor)
  .thenApply(data -> transform(data))
  .thenAccept(result -> publish(result));
```

#### Error handling

```java
CompletableFuture
  .supplyAsync(this::fetch, executor)
  .exceptionally(ex -> fallbackValue());
```

#### handle() (success or failure)

```java
CompletableFuture
  .supplyAsync(this::fetch, executor)
  .handle((value, ex) -> ex == null ? value : fallbackValue());
```

### thenApply vs thenCompose

- `thenApply`: transforms T → U
- `thenCompose`: flattens T → CompletableFuture<U>

```java
CompletableFuture<User> u = findUserAsync(id);

CompletableFuture<Address> wrong =
  u.thenApply(user -> fetchAddressAsync(user)); // returns CF<CF<Address>>

CompletableFuture<Address> right =
  u.thenCompose(user -> fetchAddressAsync(user)); // CF<Address>
```

### thenCombine (combine two independent async results)

```java
CompletableFuture<A> fa = ...
CompletableFuture<B> fb = ...

CompletableFuture<C> fc =
  fa.thenCombine(fb, (a, b) -> combine(a, b));
```

### Important default: common pool
If you call `supplyAsync()` without an executor, it uses `ForkJoinPool.commonPool()`.  
In production, prefer passing a dedicated executor for isolation.

---

## Spring Boot Examples

### Example 1 — Bounded Executor + Backpressure (Tomcat)

#### Configuration

```java
@Configuration
public class ExecutorConfig {

  @Bean
  public ExecutorService paymentExecutor() {
    int cores = Runtime.getRuntime().availableProcessors();

    return new ThreadPoolExecutor(
      cores,
      cores * 2,
      30, TimeUnit.SECONDS,
      new ArrayBlockingQueue<>(500),
      new ThreadPoolExecutor.CallerRunsPolicy()
    );
  }
}
```

#### Service usage

```java
@Service
@RequiredArgsConstructor
public class PaymentService {

  private final ExecutorService paymentExecutor;

  public void processPayment(PaymentRequest request) {
    // Usually async: returns quickly if queue has space
    paymentExecutor.submit(() -> heavyProcessing(request));
  }

  private void heavyProcessing(PaymentRequest request) {
    // DB calls, remote calls, etc.
  }
}
```

#### What happens under overload?
- When queue is full and pool is maxed:
  - CallerRunsPolicy runs task in request thread
  - Tomcat threads get occupied
  - system slows incoming traffic = backpressure

---

### Example 2 — CompletableFuture with custom executor

```java
@Service
@RequiredArgsConstructor
public class ReportService {

  private final ExecutorService reportExecutor;

  public CompletableFuture<String> buildReportAsync(String userId) {
    return CompletableFuture
      .supplyAsync(() -> loadUser(userId), reportExecutor)
      .thenApply(user -> computeReport(user))
      .exceptionally(ex -> "REPORT_FAILED");
  }

  private User loadUser(String userId) { ... }
  private String computeReport(User u) { ... }
}
```

---

### Example 3 — HTTP overload fail-fast (AbortPolicy)

If you prefer returning 429/503 instead of slowing Tomcat threads:

- use `AbortPolicy`
- catch `RejectedExecutionException`
- return 429/503 from controller advice

```java
@Bean
public ExecutorService executor() {
  return new ThreadPoolExecutor(
    cores, cores * 2,
    30, TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(500),
    new ThreadPoolExecutor.AbortPolicy()
  );
}
```

---

## Common Pitfalls

1. **Unbounded queue** → OOM under sustained load
2. **Blocking `future.get()` on request thread** → kills throughput
3. **Using common pool unintentionally** in CompletableFuture → noisy neighbor issues
4. **Not shutting down** executors → app doesn’t stop / thread leak
5. **Heavy work in rejection handler** (CallerRuns) without understanding impact → request timeouts
6. **Too many pools** (one per bean) → thread explosion; prefer shared, well-named pools per workload
7. **Long blocking tasks** + CallerRunsPolicy → Tomcat thread starvation

---

## Interview Questions We Covered

### Executor / Thread pools
- Why are `Executors.newFixedThreadPool()` and `newCachedThreadPool()` dangerous?
- How does ThreadPoolExecutor decide: create thread vs queue vs reject?
- What is backpressure? Why is CallerRunsPolicy backpressure?
- What happens when more work arrives than the queue capacity?
- How do you shut down an executor gracefully?

### Future / CompletableFuture
- What is Future? Why is it limited?
- How does `submit()` differ from `execute()`?
- Why is CompletableFuture better? How do you compose async steps?
- thenApply vs thenCompose vs thenCombine
- Where do exceptions go in Future vs CompletableFuture?

---

## Quick Cheat Sheet

### Safe pool defaults (starting point)
- CPU-bound: core ≈ cores, max ≈ cores or cores*2
- IO-bound: can raise max (measure!), but keep bounded queue

### Policies
- want graceful slow-down → `CallerRunsPolicy`
- want fail-fast APIs → `AbortPolicy` + return 429/503

### Shutdown
- always call `shutdown()` in `@PreDestroy`
- use `awaitTermination`
- fallback to `shutdownNow()`
- restore interrupt flag

---

_End of notes._
