# Java Streams — Interview Prep & Daily Coding Patterns

## 📌 Purpose

This document is designed for **quick interview prep** and **daily coding reference**. It focuses on patterns you'll actually use and questions interviewers frequently ask.

---

## 🧠 Core Mental Model

```
┌─────────────────────────────────────────────────────────────────┐
│                     STREAM PIPELINE                             │
├─────────────────────────────────────────────────────────────────┤
│  SOURCE          →    INTERMEDIATE    →    TERMINAL            │
│  (Collection,         (Lazy, chainable)     (Triggers           │
│   Array, Generator)    filter, map, sorted   execution)         │
│                                              collect, reduce    │
└─────────────────────────────────────────────────────────────────┘
```

**Key Interview Points:**
1. **Lazy evaluation** — Nothing executes until terminal operation
2. **Single-use** — Stream can only be consumed once
3. **Non-interfering** — Don't modify source during processing
4. **Stateless operations** — Preferred for predictable behavior

---

## 🔥 Daily Coding Patterns (The Ones You'll Use 80% of the Time)

### Pattern 1: Filter + Map (Data Extraction)

**Use case:** Extract specific fields from filtered data

```java
// Get emails of active users
List<String> activeEmails = users.stream()
    .filter(User::isActive)
    .map(User::getEmail)
    .toList();

// Get names of high earners (salary > 80000)
List<String> highEarnerNames = employees.stream()
    .filter(e -> e.salary() > 80000)
    .map(Employee::name)
    .toList();

// Multiple filters (readable style)
List<Product> result = products.stream()
    .filter(Product::inStock)
    .filter(p -> p.price() < 100)
    .filter(p -> p.category().equals("Electronics"))
    .toList();
```

### Pattern 2: Group & Count (Analytics/Reporting)

**Use case:** Counting occurrences, frequency analysis

```java
// Count employees by department
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.counting()
    ));
// Result: {Engineering=5, Marketing=3, HR=2}

// Word frequency
Map<String, Long> wordFreq = Arrays.stream(text.split("\\s+"))
    .collect(Collectors.groupingBy(
        String::toLowerCase,
        Collectors.counting()
    ));
```

### Pattern 3: Group & Aggregate (Sum, Average, Max)

**Use case:** Business reporting, analytics

```java
// Average salary by department
Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.averagingDouble(Employee::salary)
    ));

// Total revenue by product category
Map<String, Double> revenueByCategory = orders.stream()
    .collect(Collectors.groupingBy(
        Order::category,
        Collectors.summingDouble(Order::total)
    ));

// Highest paid employee per department
Map<String, Optional<Employee>> topEarnerByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.maxBy(Comparator.comparing(Employee::salary))
    ));
```

### Pattern 4: Find Max/Min

**Use case:** Finding best/worst, highest/lowest

```java
// Highest paid employee
Optional<Employee> highest = employees.stream()
    .max(Comparator.comparing(Employee::salary));

// Alternative: using sorted + findFirst
Employee highest = employees.stream()
    .sorted(Comparator.comparing(Employee::salary).reversed())
    .findFirst()
    .orElseThrow();

// Longest string
Optional<String> longest = strings.stream()
    .max(Comparator.comparing(String::length));
```

### Pattern 5: Top N

**Use case:** Leaderboards, top performers, recent items

```java
// Top 3 highest salaries
List<Employee> top3 = employees.stream()
    .sorted(Comparator.comparing(Employee::salary).reversed())
    .limit(3)
    .toList();

// Top 5 most expensive products
List<Product> top5Expensive = products.stream()
    .sorted(Comparator.comparing(Product::price).reversed())
    .limit(5)
    .toList();
```

### Pattern 6: Lookup Map

**Use case:** Quick access by key, avoiding repeated searches

```java
// Employee lookup by ID
Map<Long, Employee> employeeById = employees.stream()
    .collect(Collectors.toMap(
        Employee::id,
        Function.identity()
    ));

// Usage: employeeById.get(123L)

// Price lookup by product name
Map<String, Double> priceByProduct = products.stream()
    .collect(Collectors.toMap(
        Product::name,
        Product::price
    ));
```

### Pattern 7: Partition (Boolean Split)

**Use case:** Dividing into two groups

```java
// Active vs inactive users
Map<Boolean, List<User>> partitioned = users.stream()
    .collect(Collectors.partitioningBy(User::isActive));

List<User> active = partitioned.get(true);
List<User> inactive = partitioned.get(false);

// Pass/fail students
Map<Boolean, List<Student>> passedFailed = students.stream()
    .collect(Collectors.partitioningBy(s -> s.score() >= 60));
```

### Pattern 8: Join Strings

**Use case:** Building CSV, SQL clauses, display strings

```java
// Comma-separated names
String names = employees.stream()
    .map(Employee::name)
    .collect(Collectors.joining(", "));
// Result: "Alice, Bob, Carol"

// SQL IN clause
String inClause = ids.stream()
    .map(String::valueOf)
    .collect(Collectors.joining(", ", "IN (", ")"));
// Result: "IN (1, 2, 3, 4)"
```

### Pattern 9: FlatMap (Nested Structures)

**Use case:** Processing nested collections

```java
// All items across all orders
List<Item> allItems = orders.stream()
    .flatMap(order -> order.items().stream())
    .toList();

// All words from sentences
List<String> words = sentences.stream()
    .flatMap(s -> Arrays.stream(s.split(" ")))
    .toList();

// Unique tags from all posts
Set<String> allTags = posts.stream()
    .flatMap(post -> post.tags().stream())
    .collect(Collectors.toSet());
```

### Pattern 10: Existence Check

**Use case:** Validation, preconditions

```java
// Check if any admin exists
boolean hasAdmin = users.stream()
    .anyMatch(User::isAdmin);

// Check if all orders are shipped
boolean allShipped = orders.stream()
    .allMatch(o -> o.status() == Status.SHIPPED);

// Check if no errors
boolean noErrors = results.stream()
    .noneMatch(Result::hasError);
```

---

## 🎯 Interview Must-Know Concepts

### 1. map vs flatMap — The Classic Question

**Question:** "What's the difference between map and flatMap?"

**Answer:**
- `map`: 1-to-1 transformation. Each input produces exactly one output.
- `flatMap`: 1-to-many with flattening. Each input produces a stream, and all streams are flattened into one.

```java
// map: Stream<List<String>> stays nested
List<List<String>> nested = List.of(
    List.of("a", "b"),
    List.of("c", "d")
);
Stream<List<String>> mapped = nested.stream().map(list -> list);
// Result: Stream containing [["a","b"], ["c","d"]]

// flatMap: Flattens to Stream<String>
Stream<String> flattened = nested.stream().flatMap(List::stream);
// Result: Stream containing ["a", "b", "c", "d"]
```

**When to use flatMap:**
- Processing nested collections (List of Lists)
- Splitting strings into words
- Chaining Optionals
- One-to-many relationships (Order → LineItems)

### 2. reduce vs collect

**Question:** "When do you use reduce vs collect?"

**Answer:**
- `reduce`: Fold values into a **single result** (sum, max, concatenation)
- `collect`: Build **complex mutable structures** (List, Map, Set)

```java
// reduce: Folding to single value
int sum = numbers.stream().reduce(0, Integer::sum);
String concat = strings.stream().reduce("", String::concat);

// collect: Building structures
List<String> list = stream.collect(Collectors.toList());
Map<String, Long> counts = stream.collect(Collectors.groupingBy(x -> x, Collectors.counting()));
```

**Key difference:** reduce creates new objects; collect mutates accumulators (more efficient for mutable containers).

### 3. groupingBy vs partitioningBy

**Question:** "Difference between groupingBy and partitioningBy?"

**Answer:**
- `groupingBy`: Groups by any classifier, produces Map with many keys
- `partitioningBy`: Groups by boolean predicate, produces Map with exactly 2 keys (true/false)

```java
// groupingBy: Many buckets
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::department));
// Keys: "Engineering", "Marketing", "HR", etc.

// partitioningBy: Two buckets
Map<Boolean, List<Employee>> eligible = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.salary() >= 60000));
// Keys: true, false (always both present)
```

### 4. Why Streams are Lazy

**Question:** "Why are streams lazy? What's the benefit?"

**Answer:**
1. **Efficiency** — Only processes elements needed for the result
2. **Short-circuiting** — Operations like `findFirst()`, `limit()` can stop early
3. **Fusion** — Multiple operations can be combined into single pass

```java
// Without laziness: Would filter ALL elements, then find first
// With laziness: Stops as soon as first match is found
Optional<Employee> first = employees.stream()
    .filter(e -> e.salary() > 100000)  // Stops when first match found
    .findFirst();

// Processes only 5 elements, not all
List<Integer> firstFive = hugeList.stream()
    .filter(n -> n > 0)
    .limit(5)  // Short-circuits after 5 matches
    .toList();
```

### 5. Why counting() Returns Long

**Question:** "Why does Collectors.counting() return Long instead of Integer?"

**Answer:** Streams can process very large datasets (potentially billions of elements). `Long` can represent counts up to 9 quintillion, while `Integer` maxes out at ~2 billion.

```java
// Safe for huge datasets
long count = hugeStream.collect(Collectors.counting());
// Can count up to Long.MAX_VALUE = 9,223,372,036,854,775,807
```

### 6. Side Effects Trap

**Question:** "Why should you avoid side effects in streams?"

**Answer:**
1. **Parallel streams** — Race conditions corrupt shared state
2. **Laziness** — Side effects may not execute when expected
3. **Reasoning** — Makes code harder to understand and debug

```java
// ❌ BAD: Side effect in stream
List<String> results = new ArrayList<>();
stream.map(x -> x.toUpperCase())
    .forEach(results::add);  // Mutating external state!

// ✅ GOOD: Collect properly
List<String> results = stream
    .map(String::toUpperCase)
    .toList();

// ❌ DANGEROUS with parallel
List<Integer> list = Collections.synchronizedList(new ArrayList<>());
IntStream.range(0, 1000).parallel()
    .forEach(list::add);  // Race condition even with synchronized list!
```

### 7. When NOT to Use Parallel Streams

**Question:** "When should you avoid parallelStream()?"

**Answer:**
1. **I/O operations** — Network/database calls block threads
2. **Small datasets** — Parallelization overhead exceeds benefit
3. **Ordered operations** — `limit()`, `findFirst()` need coordination
4. **Shared mutable state** — Race conditions
5. **Sequential dependencies** — Each element depends on previous

```java
// ❌ AVOID: I/O in parallel
users.parallelStream()
    .forEach(user -> database.save(user));  // Connection pool exhaustion

// ❌ AVOID: Small data
smallList.parallelStream().map(x -> x * 2).toList();  // Overhead > benefit

// ✅ OK: CPU-intensive, independent operations
largeList.parallelStream()
    .map(this::expensiveComputation)  // Pure function, no I/O
    .toList();
```

### 8. Optional Best Practices

**Question:** "How should you handle Optional from streams?"

**Answer:**

```java
// ❌ NEVER DO THIS
String name = optional.get();  // NoSuchElementException if empty!

// ❌ DEFEATS PURPOSE
if (optional.isPresent()) {
    String name = optional.get();
}

// ✅ PREFERRED PATTERNS
String name = optional.orElse("Unknown");
String name = optional.orElseGet(() -> computeDefault());  // Lazy
String name = optional.orElseThrow(() -> new NotFoundException());

optional.ifPresent(System.out::println);
optional.ifPresentOrElse(
    System.out::println,
    () -> System.out.println("Not found")
);

// ✅ Stream with Optional
Optional<String> result = employees.stream()
    .filter(e -> e.id() == targetId)
    .map(Employee::name)
    .findFirst();

result.ifPresent(name -> logger.info("Found: {}", name));
```

---

## 📝 Common Interview Coding Questions

### Q1: Find Second Highest Salary

```java
Optional<Double> secondHighest = employees.stream()
    .map(Employee::salary)
    .distinct()  // Remove duplicates
    .sorted(Comparator.reverseOrder())
    .skip(1)  // Skip the highest
    .findFirst();
```

### Q2: Find Duplicate Elements

```java
Set<Integer> seen = new HashSet<>();
List<Integer> duplicates = numbers.stream()
    .filter(n -> !seen.add(n))  // add returns false if already present
    .distinct()
    .toList();

// Alternative (functional, no side effects)
Map<Integer, Long> freq = numbers.stream()
    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
List<Integer> duplicates = freq.entrySet().stream()
    .filter(e -> e.getValue() > 1)
    .map(Map.Entry::getKey)
    .toList();
```

### Q3: Flatten and Process Nested Structure

```java
record Department(String name, List<Employee> employees) {}

// Get all employee names across all departments
List<String> allNames = departments.stream()
    .flatMap(dept -> dept.employees().stream())
    .map(Employee::name)
    .distinct()
    .sorted()
    .toList();
```

### Q4: Word Frequency Count

```java
String text = "the quick brown fox jumps over the lazy dog";

Map<String, Long> frequency = Arrays.stream(text.split("\\s+"))
    .map(String::toLowerCase)
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ));
// Result: {the=2, quick=1, brown=1, fox=1, ...}
```

### Q5: Find Employees with Highest Salary per Department

```java
Map<String, Employee> highestPaidByDept = employees.stream()
    .collect(Collectors.toMap(
        Employee::department,
        Function.identity(),
        BinaryOperator.maxBy(Comparator.comparing(Employee::salary))
    ));

// Alternative with groupingBy
Map<String, Optional<Employee>> result = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.maxBy(Comparator.comparing(Employee::salary))
    ));
```

### Q6: Check if Two Lists Have Common Elements

```java
boolean hasCommon = list1.stream()
    .anyMatch(list2::contains);

// More efficient with Set
Set<Integer> set2 = new HashSet<>(list2);
boolean hasCommon = list1.stream()
    .anyMatch(set2::contains);
```

### Q7: Sum of Squares of Even Numbers

```java
int sumOfSquares = numbers.stream()
    .filter(n -> n % 2 == 0)
    .mapToInt(n -> n * n)
    .sum();
```

### Q8: Partition and Calculate Averages

```java
Map<Boolean, Double> avgByEligibility = employees.stream()
    .collect(Collectors.partitioningBy(
        e -> e.salary() >= 60000,
        Collectors.averagingDouble(Employee::salary)
    ));
// Result: {true=85000.0, false=45000.0}
```

---

## ⚠️ Common Traps & Gotchas

### Trap 1: Stream Reuse

```java
Stream<String> stream = names.stream();
long count = stream.count();  // OK
List<String> list = stream.toList();  // ❌ IllegalStateException!
```

### Trap 2: Null in Stream

```java
List<String> names = Arrays.asList("Alice", null, "Bob");

// ❌ NullPointerException in map
names.stream()
    .map(String::toUpperCase)  // Crashes on null
    .toList();

// ✅ Filter nulls first
names.stream()
    .filter(Objects::nonNull)
    .map(String::toUpperCase)
    .toList();
```

### Trap 3: Duplicate Keys in toMap

```java
// ❌ Throws IllegalStateException on duplicate keys
Map<String, Integer> map = people.stream()
    .collect(Collectors.toMap(Person::name, Person::age));

// ✅ Provide merge function
Map<String, Integer> map = people.stream()
    .collect(Collectors.toMap(
        Person::name,
        Person::age,
        (existing, replacement) -> existing  // Keep first
    ));
```

### Trap 4: Modifying Source During Stream

```java
List<String> names = new ArrayList<>(List.of("A", "B", "C"));

// ❌ ConcurrentModificationException
names.stream()
    .forEach(n -> names.add(n + "!"));  // Modifying source!

// ✅ Collect to new list
List<String> modified = names.stream()
    .map(n -> n + "!")
    .toList();
```

---

## 🚀 Quick Reference Cheat Sheet

| Pattern | Code |
|---------|------|
| Filter + Map | `stream.filter(pred).map(fn).toList()` |
| Group + Count | `Collectors.groupingBy(fn, counting())` |
| Group + Sum | `Collectors.groupingBy(fn, summingDouble(fn))` |
| Find Max | `stream.max(Comparator.comparing(fn))` |
| Top N | `stream.sorted(cmp.reversed()).limit(n)` |
| Lookup Map | `Collectors.toMap(keyFn, valueFn)` |
| Partition | `Collectors.partitioningBy(pred)` |
| Join Strings | `Collectors.joining(", ")` |
| Flatten | `stream.flatMap(x -> x.stream())` |
| Any/All Check | `stream.anyMatch(pred)` / `allMatch` |

---

*Last updated: December 2024*
