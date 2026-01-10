# Java Concurrency — Complete Revision Notes (JMM, Locks, CHM)

---

## 1. Java Memory Model (JMM)

### What is JMM?
Defines rules for:
- Visibility
- Ordering
- Atomicity

### Happens-Before
If A happens-before B, all writes in A are visible to B.

Examples:
- Unlock -> subsequent lock on same monitor
- Volatile write -> volatile read
- Thread.start() -> thread actions
- Thread.join() returns -> actions visible

---

## 2. Volatile

Guarantees:
- Visibility
- Ordering

Does NOT guarantee:
- Atomicity

Use cases:
- Flags (shutdown, initialized)
- Double-checked locking

---

## 3. Locks & synchronized

### Intrinsic Lock
Every object has a monitor.

### synchronized
- Mutual exclusion
- Establishes happens-before

Method vs Block:
- instance method -> locks this
- static method -> locks Class
- block -> locks specified object

Prefer blocks for fine-grained locking.

---

## 4. wait / notify

- Must be called inside synchronized
- Releases lock on wait
- Use while, not if

```java
synchronized(lock){
  while(!cond) lock.wait();
  lock.notifyAll();
}
```

---

## 5. Deadlocks

Conditions:
- Mutual exclusion
- Hold and wait
- No preemption
- Circular wait

Prevention:
- Lock ordering
- Timeouts
- Avoid nested locks

---

## 6. ConcurrentHashMap

### Why thread-safe?
- CAS + fine-grained bin locking
- Lock-free reads mostly

### vs synchronizedMap
| CHM | synchronizedMap |
|---|---|
Scales | Single lock |
Fine-grained | Coarse |

---

## 7. CAS

- Atomic CPU instruction
- Optimistic
- Can spin under contention

---

## 8. computeIfAbsent

- Atomic insert
- Lambda may run more than once
- Must be side-effect free

---

## 9. Singleton

Double-checked locking:

```java
class Singleton {
  private static volatile Singleton instance;
  public static Singleton get() {
    if(instance==null){
      synchronized(Singleton.class){
        if(instance==null) instance=new Singleton();
      }
    }
    return instance;
  }
}
```

Best: enum Singleton

---

## 10. Thread Lifecycle

NEW -> RUNNABLE -> BLOCKED/WAITING/TIMED_WAITING -> TERMINATED

---

## 11. Interview Traps

- volatile != atomic
- computeIfAbsent lambda may run twice
- CHM does not lock whole map
- wait must be in while
- never block inside synchronized

---

## 12. Cheat Sheet

| Tool | Purpose |
|---|---|
volatile | visibility |
synchronized | mutual exclusion |
Lock | advanced control |
CHM | concurrent map |
CAS | lock-free updates |

---

End of file.
