# Java Concurrency — Revision Notes

## 1. Java Memory Model (JMM)

### What is JMM?
Defines how threads interact through memory:
- Visibility: when writes become visible to other threads
- Ordering: what reordering is allowed
- Atomicity: what operations are indivisible

### Happens-Before
A happens-before relationship guarantees:
- Writes before A are visible after B
- Ordering between actions

Examples:
- Unlock happens-before subsequent lock on same monitor
- Volatile write happens-before volatile read
- Thread.start() happens-before actions in started thread
- Thread.join() establishes happens-before after completion

---

## 2. Volatile

### Guarantees
- Visibility
- Ordering (prevents reordering around volatile access)

### Does NOT guarantee
- Atomicity

### Use Cases
- State flags (shutdown, initialized)
- Double-checked locking
- Spin loops

---

## 3. Synchronized

### Intrinsic Lock
Every object has a monitor (intrinsic lock).

### Guarantees
- Mutual exclusion
- Visibility (via happens-before)

### Method vs Block

| Type | Locks |
|------|--------|
Instance method | this |
Static method | ClassName.class |
Block | given object |

Prefer block for fine-grained locking.

---

## 4. Thread Lifecycle

States:
- NEW
- RUNNABLE
- BLOCKED (waiting for monitor)
- WAITING (wait/join)
- TIMED_WAITING (sleep, wait(timeout))
- TERMINATED

---

## 5. wait / notify

- Must be called inside synchronized block
- Releases monitor when waiting
- Always use in while loop (not if) to handle spurious wakeups

Example:
```java
synchronized(lock) {
  while (!condition) lock.wait();
  // work
  lock.notifyAll();
}
