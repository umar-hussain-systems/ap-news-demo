# Java Streams — Advanced Topics & Deep Dive

## 📌 Overview

This document covers **advanced stream concepts** for senior developers and those preparing for senior-level interviews. Topics include custom collectors, parallel stream internals, performance optimization, and advanced patterns.

---

## 1. Custom Collectors — Building Your Own

### Understanding Collector Interface

A `Collector` has four components:

```java
public interface Collector<T, A, R> {
    Supplier<A> supplier();           // Creates accumulator container
    BiConsumer<A, T> accumulator();   // Adds element to container
    BinaryOperator<A> combiner();     // Merges two containers (parallel)
    Function<A, R> finisher();        // Transforms container to result
    Set<Characteristics> characteristics();  // Optimization hints
}
```

### Building Collectors with Collector.of()

```java
// Custom collector: Count elements (reimplementing counting())
Collector<Object, long[], Long> myCounter = Collector.of(
    () -> new long[1],           // Supplier: create container
    (acc, e) -> acc[0]++,        // Accumulator: add element
    (a, b) -> {                  // Combiner: merge containers
        a[0] += b[0]; 
        return a; 
    },
    acc -> acc[0]                // Finisher: extract result
);

long count = employees.stream().collect(myCounter);

// Custom collector: Find all distinct departments
Collector<Employee, Set<String>, Set<String>> distinctDepts = Collector.of(
    HashSet::new,
    (set, emp) -> set.add(emp.department()),
    (s1, s2) -> { s1.addAll(s2); return s1; },
    Function.identity(),
    Collector.Characteristics.UNORDERED,
    Collector.Characteristics.IDENTITY_FINISH
);

Set<String> depts = employees.stream().collect(distinctDepts);
```

### Advanced Custom Collector Examples

#### Collector: Running Average

```java
// Calculate running average without loading all values into memory
Collector<Double, double[], Double> runningAverage = Collector.of(
    () -> new double[2],  // [sum, count]
    (acc, val) -> {
        acc[0] += val;    // sum
        acc[1]++;          // count
    },
    (a, b) -> {
        a[0] += b[0];
        a[1] += b[1];
        return a;
    },
    acc -> acc[1] == 0 ? 0.0 : acc[0] / acc[1]
);

double avgSalary = employees.stream()
    .map(Employee::salary)
    .collect(runningAverage);
```

#### Collector: Top N Elements

```java
// Collect top N elements by comparator
public static <T> Collector<T, ?, List<T>> topN(int n, Comparator<T> comparator) {
    return Collector.of(
        () -> new PriorityQueue<>(n + 1, comparator.reversed()),
        (queue, element) -> {
            queue.add(element);
            if (queue.size() > n) {
                queue.poll();  // Remove smallest
            }
        },
        (q1, q2) -> {
            q1.addAll(q2);
            while (q1.size() > n) q1.poll();
            return q1;
        },
        queue -> {
            List<T> result = new ArrayList<>(queue);
            result.sort(comparator.reversed());
            return result;
        }
    );
}

// Usage
List<Employee> top3 = employees.stream()
    .collect(topN(3, Comparator.comparing(Employee::salary)));
```

#### Collector: Group by with Value Transformation

```java
// Custom: Group and transform values in one step
public static <T, K, V> Collector<T, ?, Map<K, List<V>>> 
        groupByMapping(Function<T, K> keyMapper, Function<T, V> valueMapper) {
    return Collector.of(
        HashMap::new,
        (map, element) -> {
            K key = keyMapper.apply(element);
            V value = valueMapper.apply(element);
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        },
        (m1, m2) -> {
            m2.forEach((k, v) -> m1.merge(k, v, (l1, l2) -> {
                l1.addAll(l2);
                return l1;
            }));
            return m1;
        }
    );
}

// Usage: Group department -> list of names
Map<String, List<String>> namesByDept = employees.stream()
    .collect(groupByMapping(Employee::department, Employee::name));
```

#### Collector: Immutable Result

```java
// Collect to ImmutableList (if using Guava)
Collector<String, ?, List<String>> toImmutableList = Collector.of(
    ArrayList::new,
    List::add,
    (l1, l2) -> { l1.addAll(l2); return l1; },
    Collections::unmodifiableList,
    Collector.Characteristics.CONCURRENT
);

// Built-in Java 10+ alternative
List<String> immutable = stream.collect(Collectors.toUnmodifiableList());
```

---

## 2. Parallel Streams — Deep Dive

### How Parallel Streams Work

```
┌─────────────────────────────────────────────────────────────────┐
│                    PARALLEL STREAM EXECUTION                    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│    Source Collection: [1, 2, 3, 4, 5, 6, 7, 8]                 │
│                           │                                     │
│                           ▼                                     │
│    ┌──────────────────────────────────────────┐                │
│    │     ForkJoinPool.commonPool()            │                │
│    │     (CPU cores - 1 worker threads)       │                │
│    └──────────────────────────────────────────┘                │
│                           │                                     │
│         ┌─────────────────┼─────────────────┐                  │
│         ▼                 ▼                 ▼                  │
│    ┌─────────┐      ┌─────────┐      ┌─────────┐              │
│    │ [1,2,3] │      │ [4,5]   │      │ [6,7,8] │              │
│    │ Thread1 │      │ Thread2 │      │ Thread3 │              │
│    └────┬────┘      └────┬────┘      └────┬────┘              │
│         │                │                │                    │
│         ▼                ▼                ▼                    │
│    [Process]        [Process]        [Process]                 │
│         │                │                │                    │
│         └────────────────┼────────────────┘                    │
│                          ▼                                      │
│                    [Combine Results]                            │
│                          │                                      │
│                          ▼                                      │
│                    Final Result                                 │
└─────────────────────────────────────────────────────────────────┘
```

### ForkJoinPool Internals

```java
// Parallel streams use ForkJoinPool.commonPool() by default
// Pool size = Runtime.getRuntime().availableProcessors() - 1

// Check common pool size
int poolSize = ForkJoinPool.commonPool().getParallelism();
System.out.println("Common pool parallelism: " + poolSize);

// Configure via system property (before JVM starts)
// -Djava.util.concurrent.ForkJoinPool.common.parallelism=4

// Or programmatically (must be set before first use)
System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
```

### Custom ForkJoinPool for Streams

```java
// Problem: All parallel streams share common pool
// Solution: Use custom pool for isolation

ForkJoinPool customPool = new ForkJoinPool(4);

try {
    List<Integer> result = customPool.submit(() ->
        hugeList.parallelStream()
            .map(this::expensiveOperation)
            .toList()
    ).get();
} finally {
    customPool.shutdown();
}

// Why use custom pool?
// 1. Isolation from other parallel streams
// 2. Control parallelism level
// 3. Prevent I/O-bound tasks from blocking CPU-bound tasks
```

### Spliterator — The Engine Behind Parallel Streams

```java
// Spliterator splits data for parallel processing
public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action);  // Process one element
    Spliterator<T> trySplit();                       // Split for parallelism
    long estimateSize();                             // Remaining elements
    int characteristics();                           // Properties (ORDERED, SIZED, etc.)
}

// Example: Custom Spliterator for chunks
class ChunkSpliterator implements Spliterator<List<String>> {
    private final List<String> data;
    private final int chunkSize;
    private int current = 0;
    
    @Override
    public Spliterator<List<String>> trySplit() {
        int remaining = data.size() - current;
        if (remaining < chunkSize * 2) return null;  // Too small to split
        
        int mid = current + remaining / 2;
        ChunkSpliterator split = new ChunkSpliterator(
            data.subList(current, mid), chunkSize);
        current = mid;
        return split;
    }
    // ... other methods
}

// Usage
Stream<List<String>> chunks = StreamSupport.stream(
    new ChunkSpliterator(data, 100), true);  // true = parallel
```

### Parallel Stream Characteristics

```java
// Check stream characteristics
Spliterator<Employee> spliterator = employees.spliterator();

boolean ordered = spliterator.hasCharacteristics(Spliterator.ORDERED);
boolean sized = spliterator.hasCharacteristics(Spliterator.SIZED);
boolean subsized = spliterator.hasCharacteristics(Spliterator.SUBSIZED);

// Characteristics affect parallel performance:
// SIZED + SUBSIZED: Efficient splitting (ArrayList, arrays)
// !SUBSIZED: Less efficient (LinkedList, tree structures)

// ArrayList vs LinkedList in parallel
List<Integer> arrayList = new ArrayList<>(data);   // O(1) split
List<Integer> linkedList = new LinkedList<>(data); // O(n) split

// arrayList.parallelStream() is much faster than linkedList.parallelStream()
```

### When Parallel Actually Helps — Benchmarking

```java
// Rule of thumb formula:
// Speedup possible if: N * Q > 10,000
// where N = number of elements, Q = cost per element

// Low Q (simple ops) - need huge N
list.parallelStream()
    .filter(x -> x > 0)  // Q ≈ 1, need N > 10,000
    .toList();

// High Q (expensive ops) - smaller N works
list.parallelStream()
    .map(this::callExternalAPI)  // Q ≈ 100ms = 100,000,000, N > 1 is enough
    .toList();

// Benchmark example
public void benchmark() {
    List<Integer> data = IntStream.range(0, 1_000_000)
        .boxed()
        .toList();
    
    // Sequential
    long start = System.nanoTime();
    data.stream().map(x -> x * x).toList();
    long seqTime = System.nanoTime() - start;
    
    // Parallel
    start = System.nanoTime();
    data.parallelStream().map(x -> x * x).toList();
    long parTime = System.nanoTime() - start;
    
    // For simple x*x, sequential is often faster due to overhead!
}
```

---

## 3. Performance Tips & Optimization

### Avoid Boxing Overhead

```java
// ❌ BAD: Boxed stream (Integer objects)
int sum = list.stream()
    .map(x -> x * 2)
    .reduce(0, Integer::sum);

// ✅ GOOD: Primitive stream (no boxing)
int sum = list.stream()
    .mapToInt(x -> x * 2)  // IntStream - primitives
    .sum();

// Benchmark difference can be 2-5x for large datasets!

// Primitive stream variants:
IntStream intStream = IntStream.range(0, 1000);
LongStream longStream = LongStream.range(0, 1000);
DoubleStream doubleStream = DoubleStream.of(1.0, 2.0, 3.0);

// Converting between boxed and primitive
IntStream primitive = Stream.of(1, 2, 3).mapToInt(Integer::intValue);
Stream<Integer> boxed = IntStream.of(1, 2, 3).boxed();
```

### Avoid Unnecessary Operations

```java
// ❌ BAD: Sorted is expensive (O(n log n))
long count = employees.stream()
    .sorted(Comparator.comparing(Employee::salary))  // Unnecessary!
    .filter(e -> e.salary() > 5000)
    .count();

// ✅ GOOD: Remove unnecessary sorting
long count = employees.stream()
    .filter(e -> e.salary() > 5000)
    .count();

// ❌ BAD: Multiple terminal operations (creates new streams)
Stream<Employee> stream = employees.stream().filter(e -> e.age() > 30);
long count = stream.count();  // OK
List<Employee> list = stream.toList();  // ERROR: Stream already consumed!

// ✅ GOOD: Collect once, derive from collection
List<Employee> filtered = employees.stream()
    .filter(e -> e.age() > 30)
    .toList();
long count = filtered.size();
```

### Prefer Stateless Operations

```java
// ❌ BAD: Stateful operation (maintains order, expensive in parallel)
employees.parallelStream()
    .sorted()  // Forces ordering - limits parallelism
    .limit(10)
    .toList();

// ✅ BETTER: Collect then sort (if result is small)
List<Employee> top10 = employees.parallelStream()
    .toList();  // Parallel collection
Collections.sort(top10.subList(0, Math.min(10, top10.size())));
```

### Short-Circuit When Possible

```java
// ✅ findFirst/findAny stop early
Optional<Employee> highEarner = employees.stream()
    .filter(e -> e.salary() > 100000)
    .findFirst();  // Stops at first match

// ✅ anyMatch/allMatch/noneMatch stop early
boolean hasAdmin = users.stream()
    .anyMatch(User::isAdmin);  // Stops at first admin found

// ✅ limit stops after N elements
List<Employee> sample = employees.stream()
    .filter(e -> e.department().equals("Engineering"))
    .limit(5)  // Stops after 5 matches
    .toList();
```

### Choose Right Data Structure

```java
// Data structure affects parallel stream performance

// ✅ Best for parallel: ArrayList, arrays
// - O(1) random access
// - Efficient splitting (SIZED + SUBSIZED)
ArrayList<Integer> arrayList = new ArrayList<>(data);
int[] array = data.stream().mapToInt(i -> i).toArray();

// ⚠️ Moderate: HashSet, TreeSet
// - No index, but can split
HashSet<Integer> hashSet = new HashSet<>(data);

// ❌ Poor for parallel: LinkedList, Stack
// - O(n) access, poor splitting
LinkedList<Integer> linkedList = new LinkedList<>(data);

// ❌ Poor: Streams from I/O (Files.lines)
// - Cannot estimate size, sequential by nature
Files.lines(path)  // Better to read to list first if parallelizing
```

### Collector Performance

```java
// ✅ toList() (Java 16+) vs Collectors.toList()
// toList() returns unmodifiable list - slightly more efficient
List<String> list1 = stream.toList();  // Preferred in Java 16+
List<String> list2 = stream.collect(Collectors.toList());  // Modifiable

// ✅ Use toSet() for uniqueness (eliminates distinct())
Set<String> unique = stream.collect(Collectors.toSet());
// Instead of: stream.distinct().collect(Collectors.toList())

// ✅ Pre-size collections when size is known
List<Employee> result = stream.collect(
    Collectors.toCollection(() -> new ArrayList<>(expectedSize))
);
```

---

## 4. Advanced Patterns

### Sliding Window

```java
// Process elements in sliding windows of size N
public static <T> Stream<List<T>> sliding(List<T> list, int size) {
    if (size > list.size()) return Stream.empty();
    
    return IntStream.rangeClosed(0, list.size() - size)
        .mapToObj(i -> list.subList(i, i + size));
}

// Usage: Moving average
List<Double> prices = List.of(10.0, 12.0, 11.0, 13.0, 14.0, 12.0);
List<Double> movingAvg = sliding(prices, 3)
    .map(window -> window.stream()
        .mapToDouble(d -> d)
        .average()
        .orElse(0))
    .toList();
// Result: [11.0, 12.0, 12.67, 13.0]
```

### Pairwise Processing

```java
// Process consecutive pairs
public static <T> Stream<Map.Entry<T, T>> pairs(List<T> list) {
    return IntStream.range(0, list.size() - 1)
        .mapToObj(i -> Map.entry(list.get(i), list.get(i + 1)));
}

// Usage: Find price changes
pairs(prices).forEach(pair -> {
    double change = pair.getValue() - pair.getKey();
    System.out.printf("%s -> %s: %+.2f%n", pair.getKey(), pair.getValue(), change);
});
```

### Zip Streams

```java
// Combine two streams element-wise (not built-in, custom implementation)
public static <A, B, C> Stream<C> zip(
        Stream<A> a, Stream<B> b, BiFunction<A, B, C> zipper) {
    Iterator<A> iterA = a.iterator();
    Iterator<B> iterB = b.iterator();
    
    Spliterator<C> spliterator = new AbstractSpliterator<>(
            Long.MAX_VALUE, Spliterator.ORDERED) {
        @Override
        public boolean tryAdvance(Consumer<? super C> action) {
            if (iterA.hasNext() && iterB.hasNext()) {
                action.accept(zipper.apply(iterA.next(), iterB.next()));
                return true;
            }
            return false;
        }
    };
    
    return StreamSupport.stream(spliterator, false);
}

// Usage
List<String> names = List.of("Alice", "Bob", "Carol");
List<Integer> ages = List.of(30, 25, 35);

List<String> combined = zip(names.stream(), ages.stream(), 
    (name, age) -> name + " is " + age)
    .toList();
// Result: ["Alice is 30", "Bob is 25", "Carol is 35"]
```

### Batch Processing

```java
// Process stream in batches
public static <T> Stream<List<T>> batch(Stream<T> stream, int batchSize) {
    List<T> batch = new ArrayList<>(batchSize);
    return Stream.concat(
        stream.map(item -> {
            batch.add(item);
            if (batch.size() >= batchSize) {
                List<T> result = new ArrayList<>(batch);
                batch.clear();
                return result;
            }
            return null;
        }).filter(Objects::nonNull),
        Stream.of(batch).filter(b -> !b.isEmpty())
    );
}

// Better approach with Collectors
public static <T> Collector<T, ?, Stream<List<T>>> batchCollector(int size) {
    return Collector.of(
        ArrayList::new,
        List::add,
        (l1, l2) -> { l1.addAll(l2); return l1; },
        list -> {
            List<List<T>> batches = new ArrayList<>();
            for (int i = 0; i < list.size(); i += size) {
                batches.add(list.subList(i, Math.min(i + size, list.size())));
            }
            return batches.stream();
        }
    );
}

// Usage: Process in batches of 100
employees.stream()
    .collect(batchCollector(100))
    .forEach(batch -> {
        database.bulkInsert(batch);  // More efficient than individual inserts
    });
```

### Memoization with Streams

```java
// Cache expensive computations
Map<Long, ExpensiveResult> cache = new ConcurrentHashMap<>();

List<ExpensiveResult> results = ids.stream()
    .map(id -> cache.computeIfAbsent(id, this::expensiveComputation))
    .toList();

// Note: Be careful with parallel streams and shared caches
// ConcurrentHashMap is safe but can have contention
```

---

## 5. Immutable Collecting

### Java 10+ Immutable Collectors

```java
// Unmodifiable collections (Java 10+)
List<String> immutableList = stream.collect(Collectors.toUnmodifiableList());
Set<String> immutableSet = stream.collect(Collectors.toUnmodifiableSet());
Map<K, V> immutableMap = stream.collect(
    Collectors.toUnmodifiableMap(keyFn, valueFn));

// Java 16+ simplified
List<String> list = stream.toList();  // Already unmodifiable

// With transformation
List<String> names = employees.stream()
    .map(Employee::name)
    .collect(Collectors.toUnmodifiableList());
```

### Guava ImmutableCollections

```java
// If using Guava library
ImmutableList<String> immutable = stream.collect(ImmutableList.toImmutableList());
ImmutableSet<String> immutableSet = stream.collect(ImmutableSet.toImmutableSet());
ImmutableMap<K, V> immutableMap = stream.collect(
    ImmutableMap.toImmutableMap(keyFn, valueFn));
```

---

## 6. Stream vs Loop — When to Choose What

### Streams Shine

```java
// ✅ Complex transformations
List<String> result = data.stream()
    .filter(predicate)
    .map(transformation)
    .flatMap(expansion)
    .sorted()
    .distinct()
    .limit(10)
    .toList();

// ✅ Aggregations
Map<String, DoubleSummaryStatistics> stats = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.summarizingDouble(Employee::salary)
    ));

// ✅ Parallel processing
List<Result> results = data.parallelStream()
    .map(this::cpuIntensiveOperation)
    .toList();
```

### Loops Are Better

```java
// ✅ Simple iteration with side effects
for (Employee e : employees) {
    emailService.send(e.email(), notification);
    logger.info("Notified: {}", e.name());
}

// ✅ Early exit with complex conditions
Employee found = null;
for (Employee e : employees) {
    if (complexCondition(e, externalState)) {
        found = e;
        break;
    }
}

// ✅ Index-based operations
for (int i = 0; i < list.size(); i++) {
    if (shouldSwap(i)) {
        Collections.swap(list, i, i + 1);
    }
}

// ✅ Modifying while iterating
Iterator<Employee> iter = employees.iterator();
while (iter.hasNext()) {
    if (iter.next().isInactive()) {
        iter.remove();  // Safe removal
    }
}

// ✅ Debugging (easier to step through)
for (Employee e : employees) {
    double bonus = calculateBonus(e);      // Breakpoint here
    e.setBonus(bonus);                      // Step through
}
```

### Performance Comparison

```java
// Simple operations: Loop slightly faster (no lambda overhead)
// Complex pipelines: Stream comparable or faster (optimizations)
// Parallel: Stream much easier (but profile first!)

// General guidance:
// - Default to streams for readability
// - Use loops for simple mutations
// - Profile before optimizing
```

---

## 7. Testing Streams

### Unit Testing Stream Operations

```java
@Test
void shouldFilterHighEarners() {
    List<Employee> employees = List.of(
        new Employee(1L, "Alice", 30, 80000, "Eng"),
        new Employee(2L, "Bob", 25, 50000, "Eng")
    );
    
    List<Employee> highEarners = employees.stream()
        .filter(e -> e.salary() > 60000)
        .toList();
    
    assertThat(highEarners)
        .hasSize(1)
        .extracting(Employee::name)
        .containsExactly("Alice");
}

@Test
void shouldGroupByDepartment() {
    Map<String, Long> countByDept = employees.stream()
        .collect(Collectors.groupingBy(Employee::department, Collectors.counting()));
    
    assertThat(countByDept)
        .containsEntry("Engineering", 5L)
        .containsEntry("Marketing", 3L);
}
```

### Testing Custom Collectors

```java
@Test
void customCollectorShouldWork() {
    Collector<Integer, ?, Double> avgCollector = createAverageCollector();
    
    double result = Stream.of(10, 20, 30).collect(avgCollector);
    
    assertThat(result).isEqualTo(20.0);
}

@Test
void customCollectorShouldHandleEmpty() {
    Collector<Integer, ?, Double> avgCollector = createAverageCollector();
    
    double result = Stream.<Integer>empty().collect(avgCollector);
    
    assertThat(result).isEqualTo(0.0);
}
```

---

## 8. Common Pitfalls & Solutions

### Pitfall 1: Infinite Streams

```java
// ❌ Infinite without limit
Stream.iterate(0, n -> n + 1)
    .forEach(System.out::println);  // Never terminates!

// ✅ Always limit infinite streams
Stream.iterate(0, n -> n + 1)
    .limit(100)
    .forEach(System.out::println);

// ✅ Java 9+: iterate with predicate
Stream.iterate(0, n -> n < 100, n -> n + 1)
    .forEach(System.out::println);
```

### Pitfall 2: Resource Leaks

```java
// ❌ Stream not closed
Stream<String> lines = Files.lines(path);
lines.forEach(System.out::println);
// File handle leaked!

// ✅ Always close I/O streams
try (Stream<String> lines = Files.lines(path)) {
    lines.forEach(System.out::println);
}
```

### Pitfall 3: Order Sensitivity

```java
// ❌ Order matters for some operations
employees.stream()
    .limit(5)        // Takes first 5
    .sorted()        // Then sorts them
    .toList();

// vs

employees.stream()
    .sorted()        // Sorts ALL first
    .limit(5)        // Then takes first 5
    .toList();

// Second is correct for "top 5 sorted employees"
```

### Pitfall 4: Lazy Evaluation Surprise

```java
// ❌ Side effect in map never executes
Stream<String> stream = list.stream()
    .map(s -> {
        System.out.println("Processing: " + s);  // Never prints!
        return s.toUpperCase();
    });
// No terminal operation - nothing happens

// ✅ Terminal operation triggers execution
List<String> result = list.stream()
    .map(s -> {
        System.out.println("Processing: " + s);  // Now it prints
        return s.toUpperCase();
    })
    .toList();  // Triggers execution
```

---

## 📊 Quick Reference: When to Use What

| Scenario | Recommendation |
|----------|----------------|
| Simple filter/map | Stream (readable) |
| Complex aggregation | Stream (powerful) |
| Side effects (DB, API) | Loop (explicit) |
| Index needed | Loop |
| Early exit with state | Loop |
| Parallel CPU work | parallelStream |
| Parallel I/O | CompletableFuture |
| Small collection (<100) | Either (preference) |
| Performance critical | Profile both |

---

*Last updated: December 2024*
