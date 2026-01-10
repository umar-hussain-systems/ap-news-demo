# ConcurrentHashMap (CHM) + ExecutorService — Detailed Revision Sheet (with runnable examples)

> **Goal:** Interview-ready notes + **copy/paste runnable** Java examples for:
> `putIfAbsent`, `computeIfAbsent`, `computeIfPresent`, `compute`, `merge`  
> plus a **CHM + ExecutorService** pattern used in production.

---

## 0) What ConcurrentHashMap really guarantees (and what it doesn’t)

### ✅ Guarantees
- Thread-safe access to the **map structure** (buckets, resizing, node insertion/removal).
- Atomic compound operations provided by CHM methods:  
  `putIfAbsent`, `computeIfAbsent`, `computeIfPresent`, `compute`, `merge`, `replace`, `remove(key, value)`, etc.
- Reads are highly concurrent; updates use fine-grained coordination (CAS / bucket-level locking internally).

### ❌ Not guaranteed
- If the **value object is mutable**, CHM does **not** make the *inside of that object* thread-safe.

Example:
```java
class Account { BigDecimal balance; } // mutable field
map.get("A1").balance = map.get("A1").balance.add(...); // still a race
```

✅ Fix: make values immutable, or use atomic fields, or synchronize inside the value, or update via `compute(...)` and replace with a new immutable value.

---

## 1) The #1 rule: Avoid manual “read → modify → write”

❌ Wrong (race condition; loses updates):
```java
map.put(k, map.get(k) + 1);
```

✅ Correct (atomic):
```java
map.merge(k, 1, Integer::sum);
```
or
```java
map.compute(k, (key, val) -> val == null ? 1 : val + 1);
```

---

## 2) `putIfAbsent` — insert only if missing

### Definition
```java
V putIfAbsent(K key, V value)
```

### Behavior
- If `key` is missing → inserts and returns `null`
- If present → returns existing value, does **not** overwrite

### Use case
- **Idempotency marker**, session token, “create once”.

### Example (threads)
```java
import java.util.concurrent.*;

public class PutIfAbsentDemo {
  public static void main(String[] args) throws Exception {
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
}
```

**What you should observe**
- Only one thread prints `inserted? true`
- Both end with the same final value.

---

## 3) `computeIfAbsent` — lazy init (mapping runs only when missing)

### Definition
```java
V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)
```

### Behavior
- If key missing → **compute value** and insert it
- If key present → return existing value (lambda not run)

### Use case
- Cache-aside / lazy loading (load from DB only once per key)

### Example (threads)
```java
import java.util.concurrent.*;

public class ComputeIfAbsentDemo {
  public static void main(String[] args) throws Exception {
    ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<>();

    Runnable task = () -> {
      String v = cache.computeIfAbsent("user:1", k -> {
        System.out.println(Thread.currentThread().getName() + " loading...");
        // simulate expensive work
        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
        return "PROFILE_DATA";
      });
      System.out.println(Thread.currentThread().getName() + " got=" + v);
    };

    Thread t1 = new Thread(task, "T1");
    Thread t2 = new Thread(task, "T2");
    t1.start(); t2.start();
    t1.join(); t2.join();
  }
}
```

### Important interview notes
- The mapping function **should be side-effect free**.
- Don’t mutate external shared state inside the lambda (can cause bugs).

---

## 4) `computeIfPresent` — update only if key exists

### Definition
```java
V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
```

### Behavior
- If key present → compute and replace
- If missing → do nothing (returns null)

### Use case
- Update existing user/session/account **only if already created**

### Example (threads)
```java
import java.util.concurrent.*;

public class ComputeIfPresentDemo {
  public static void main(String[] args) throws Exception {
    ConcurrentHashMap<String, Integer> balances = new ConcurrentHashMap<>();
    balances.put("A1", 1000);

    Runnable add100 = () -> balances.computeIfPresent("A1", (k, v) -> v + 100);

    Thread t1 = new Thread(add100, "T1");
    Thread t2 = new Thread(add100, "T2");
    t1.start(); t2.start();
    t1.join(); t2.join();

    System.out.println("A1 balance=" + balances.get("A1")); // 1200
  }
}
```

---

## 5) `compute` — full control (present or absent)

### Definition
```java
V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction)
```

### Behavior
- Runs for both cases:
  - `val == null` (absent)
  - `val != null` (present)
- If function returns `null` → mapping is removed (delete)

### Use cases
- Atomic read-modify-write
- “Create if missing else update”
- Conditional delete

### Example (threads) — counter with full control
```java
import java.util.concurrent.*;

public class ComputeDemo {
  public static void main(String[] args) throws Exception {
    ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

    Runnable inc = () -> {
      for (int i = 0; i < 10_000; i++) {
        map.compute("counter", (k, v) -> (v == null) ? 1 : v + 1);
      }
    };

    Thread t1 = new Thread(inc, "T1");
    Thread t2 = new Thread(inc, "T2");
    t1.start(); t2.start();
    t1.join(); t2.join();

    System.out.println("counter=" + map.get("counter")); // 20000
  }
}
```

### Example — “remove if becomes zero”
```java
map.compute("stock", (k, v) -> {
  int next = (v == null ? 0 : v) - 1;
  return next <= 0 ? null : next; // null => remove key
});
```

---

## 6) `merge` — combine old + new (best for counters/aggregation)

### Definition
```java
V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)
```

### Behavior
- If key absent → inserts `value`
- If present → replaces with `remappingFunction(oldValue, value)`

### Use case
- Metrics, counters, totals, aggregation (most common in interviews)

### Example (threads) — safe increment
```java
import java.util.concurrent.*;

public class MergeDemo {
  public static void main(String[] args) throws Exception {
    ConcurrentHashMap<String, Integer> metrics = new ConcurrentHashMap<>();

    Runnable inc = () -> {
      for (int i = 0; i < 10_000; i++) {
        metrics.merge("requests", 1, Integer::sum);
      }
    };

    Thread t1 = new Thread(inc, "T1");
    Thread t2 = new Thread(inc, "T2");
    t1.start(); t2.start();
    t1.join(); t2.join();

    System.out.println("requests=" + metrics.get("requests")); // 20000
  }
}
```

### Example — totals per key
```java
metrics.merge("sales:apple", 10, Integer::sum);
metrics.merge("sales:apple", 5, Integer::sum); // now 15
```

---

## 7) Updating when the key is present — “what should I use?”

### Scenario: you only want to update if exists
✅ Use:
- `computeIfPresent`
- `replace(key, oldValue, newValue)` (CAS-style conditional replace)

Example:
```java
map.computeIfPresent("k", (key, val) -> val + 1);
```

### Scenario: create if missing else update
✅ Use:
- `compute`
- `merge` (if it fits your combine logic)

Example:
```java
map.compute("k", (key, val) -> val == null ? init : update(val));
```

---

## 8) CHM + ExecutorService — production-style example (bounded queue + backpressure)

This is the pattern you’ll see in real services:
- Worker pool processes many tasks
- Each task updates shared stats / counters / caches using CHM atomically
- Pool uses **bounded queue** and a **rejection policy** (backpressure)

### Example: count events by type from multiple tasks
```java
import java.util.concurrent.*;
import java.util.*;

public class CHMWithExecutorServiceDemo {

  public static void main(String[] args) throws Exception {

    ConcurrentHashMap<String, Integer> stats = new ConcurrentHashMap<>();

    // Production-safe pool: bounded queue + CallerRunsPolicy (backpressure)
    int threads = 4;
    int queueSize = 50;

    ThreadPoolExecutor pool = new ThreadPoolExecutor(
        threads,
        threads,
        0L, TimeUnit.MILLISECONDS,
        new ArrayBlockingQueue<>(queueSize),
        new ThreadFactory() {
          private final ThreadFactory def = Executors.defaultThreadFactory();
          private int n = 1;
          @Override public Thread newThread(Runnable r) {
            Thread t = def.newThread(r);
            t.setName("worker-" + (n++));
            return t;
          }
        },
        new ThreadPoolExecutor.CallerRunsPolicy() // if queue full, caller executes task
    );

    // Simulate tasks producing events
    List<String> events = List.of("LOGIN", "LOGIN", "PAYMENT", "PAYMENT", "PAYMENT", "LOGOUT");

    Runnable submitter = () -> {
      for (int i = 0; i < 10_000; i++) {
        String ev = events.get(i % events.size());
        pool.execute(() -> {
          // Atomic aggregation:
          stats.merge(ev, 1, Integer::sum);
        });
      }
    };

    Thread producer1 = new Thread(submitter, "producer-1");
    Thread producer2 = new Thread(submitter, "producer-2");

    producer1.start();
    producer2.start();
    producer1.join();
    producer2.join();

    // ExecutorService lifecycle: shutdown -> await -> optional shutdownNow
    pool.shutdown();
    boolean finished = pool.awaitTermination(10, TimeUnit.SECONDS);
    if (!finished) {
      pool.shutdownNow();
    }

    System.out.println("Stats = " + stats);
  }
}
```

### What this demonstrates (interview points)
- Bounded queue prevents unbounded memory growth.
- `CallerRunsPolicy` adds backpressure by slowing producers.
- CHM `merge` safely aggregates counts from many concurrent tasks.
- Clean shutdown using `shutdown()` + `awaitTermination()`.

---

## 9) Common interview traps (memorize)

### Trap A — “CHM makes everything thread-safe”
❌ Wrong: CHM doesn’t protect internal mutation of values.
✅ Either:
- use immutable values + replace atomically
- or use atomic fields / synchronization inside values
- or update via `compute(...)` and return a new object

### Trap B — “computeIfAbsent runs many times”
For the **same key**, CHM ensures correctness; mapping function is not meant to run repeatedly for the same key.
But **don’t rely on side effects** (don’t print/log for correctness).

### Trap C — “get + put is fine if map is concurrent”
❌ Still wrong. That compound operation is not atomic.
✅ Use CHM atomic methods.

---

## 10) Quick decision table

| Need | Best method |
|------|-------------|
| Insert only if missing | `putIfAbsent` |
| Lazy init value only if missing | `computeIfAbsent` |
| Update only if key exists | `computeIfPresent` |
| Full atomic read-modify-write | `compute` |
| Combine old+new (counters/totals) | `merge` |

---

### Mini-checklist (before you code CHM in production)
- Are my values immutable or safely updatable?
- Am I using atomic CHM methods instead of `get+put`?
- If using ExecutorService: is the queue bounded? is there backpressure?
- Do I shut down pools properly?

---
