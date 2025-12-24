# Phase 1 — Stream + Functional Programming + Lambdas (Java)

## 📌 Overview

This document covers the foundational concepts of Java's Stream API and functional programming paradigm. Streams were introduced in Java 8 and represent a modern approach to processing collections of data.

---

## 1) What "Functional Style" Means in Java

### The Paradigm Shift

Traditional **imperative programming** focuses on *how* to do something — you write explicit loops, maintain state, and specify each step of the algorithm.

**Functional programming** focuses on *what* you want to achieve — you describe transformations and let the framework handle the iteration.

### Key Principles

| Principle | Description | Example |
|-----------|-------------|---------|
| **Declarative** | Describe the desired result, not the steps | `filter(x -> x > 5)` instead of `if (x > 5)` |
| **Immutability** | Don't modify original data; create new data | Streams don't change the source collection |
| **Functions as Values** | Pass behavior (lambdas) as method arguments | `list.stream().map(String::toUpperCase)` |
| **No Side Effects** | Operations should not affect external state | Avoid modifying variables outside the stream |

### Stream Pipeline Architecture

```
┌─────────┐    ┌─────────────────────┐    ┌──────────────┐
│ SOURCE  │ → │ INTERMEDIATE OPS    │ → │ TERMINAL OP  │
│         │    │ (lazy, chainable)   │    │ (triggers    │
│ List,   │    │ map, filter, sorted │    │  execution)  │
│ Set,    │    │ flatMap, distinct   │    │ collect,     │
│ Array   │    │ limit, skip, peek   │    │ forEach,     │
└─────────┘    └─────────────────────┘    │ reduce       │
                                          └──────────────┘
```

### Practical Example: Imperative vs Functional

**Imperative Approach:**
```java
List<String> names = Arrays.asList("alice", "bob", "charlie", "david");
List<String> result = new ArrayList<>();

for (String name : names) {
    if (name.length() > 3) {
        result.add(name.toUpperCase());
    }
}
// result: [ALICE, CHARLIE, DAVID]
```

**Functional Approach:**
```java
List<String> result = names.stream()
    .filter(name -> name.length() > 3)
    .map(String::toUpperCase)
    .toList();
// result: [ALICE, CHARLIE, DAVID]
```

**Why functional is better here:**
- ✅ Less boilerplate code
- ✅ Easier to read and understand intent
- ✅ No mutable state (result is built at the end)
- ✅ Easy to parallelize with `.parallelStream()`

---

## 2) Lambdas in Depth

### What is a Lambda?

A **lambda expression** is a concise way to implement a **functional interface** (an interface with exactly one abstract method). It's essentially an anonymous function.

### Syntax Variations

```java
// Full syntax
(Type param1, Type param2) -> { statements; return result; }

// Type inference (compiler figures out types)
(param1, param2) -> { statements; return result; }

// Single expression (no braces, implicit return)
(param1, param2) -> expression

// Single parameter (no parentheses needed)
param -> expression

// No parameters
() -> expression
```

### Core Functional Interfaces

#### Predicate<T> — Tests a condition (returns boolean)
```java
Predicate<Integer> isEven = x -> x % 2 == 0;
Predicate<String> isEmpty = String::isEmpty;
Predicate<Employee> isHighEarner = e -> e.salary() > 50000;

// Usage
boolean result = isEven.test(4);  // true
list.stream().filter(isEven).toList();

// Combining predicates
Predicate<Integer> isPositiveEven = x -> x > 0 && x % 2 == 0;
// Or using composition:
Predicate<Integer> isPositive = x -> x > 0;
Predicate<Integer> combined = isPositive.and(isEven);
```

#### Function<T, R> — Transforms input to output
```java
Function<String, Integer> length = s -> s.length();
Function<String, Integer> lengthRef = String::length;  // Method reference
Function<Employee, String> getName = Employee::name;

// Usage
int len = length.apply("hello");  // 5
list.stream().map(length).toList();

// Chaining functions
Function<String, String> trim = String::trim;
Function<String, String> upper = String::toUpperCase;
Function<String, String> trimThenUpper = trim.andThen(upper);
String result = trimThenUpper.apply("  hello  ");  // "HELLO"
```

#### Consumer<T> — Performs an action (no return value)
```java
Consumer<String> print = s -> System.out.println(s);
Consumer<String> printRef = System.out::println;  // Method reference
Consumer<Employee> logEmployee = e -> logger.info("Processing: {}", e.name());

// Usage
print.accept("Hello World");
list.forEach(print);

// Chaining consumers
Consumer<String> printAndLog = print.andThen(s -> logger.debug(s));
```

#### Supplier<T> — Provides a value (no input)
```java
Supplier<Long> timestamp = () -> System.currentTimeMillis();
Supplier<UUID> idGenerator = UUID::randomUUID;
Supplier<List<String>> emptyList = ArrayList::new;

// Usage
Long time = timestamp.get();
Optional<String> opt = Optional.empty();
String value = opt.orElseGet(() -> "default");  // Supplier for lazy default
```

#### BiFunction<T, U, R> — Two inputs, one output
```java
BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
BiFunction<String, String, String> concat = String::concat;

// Usage
int sum = add.apply(5, 3);  // 8
```

#### BinaryOperator<T> — Two same-type inputs, same-type output
```java
BinaryOperator<Integer> sum = Integer::sum;
BinaryOperator<String> longer = (a, b) -> a.length() > b.length() ? a : b;

// Usage in reduce
int total = list.stream().reduce(0, Integer::sum);
```

### Method References — Cleaner Lambda Syntax

| Type | Lambda | Method Reference |
|------|--------|------------------|
| Static method | `x -> Math.abs(x)` | `Math::abs` |
| Instance method (parameter) | `s -> s.toUpperCase()` | `String::toUpperCase` |
| Instance method (external) | `s -> System.out.println(s)` | `System.out::println` |
| Constructor | `() -> new ArrayList<>()` | `ArrayList::new` |

```java
// More examples
List<String> names = Arrays.asList("alice", "bob");

// Static method reference
names.stream().map(String::valueOf);

// Instance method of arbitrary object
names.stream().map(String::toUpperCase).toList();

// Instance method of specific object
PrintStream out = System.out;
names.forEach(out::println);

// Constructor reference
names.stream().map(StringBuilder::new).toList();
```

---

## 3) Stream Fundamentals

### Creating Streams

```java
// From Collections
List<String> list = List.of("a", "b", "c");
Stream<String> stream1 = list.stream();
Stream<String> parallelStream = list.parallelStream();

// From Arrays
String[] array = {"a", "b", "c"};
Stream<String> stream2 = Arrays.stream(array);

// Using Stream.of()
Stream<String> stream3 = Stream.of("a", "b", "c");

// Infinite streams
Stream<Integer> infinite = Stream.iterate(0, n -> n + 2);  // 0, 2, 4, 6...
Stream<Double> randoms = Stream.generate(Math::random);    // Random doubles

// Primitive streams (avoid boxing overhead)
IntStream intStream = IntStream.range(1, 100);      // 1 to 99
LongStream longStream = LongStream.rangeClosed(1, 100);  // 1 to 100
DoubleStream doubleStream = DoubleStream.of(1.5, 2.5, 3.5);

// From String
IntStream chars = "hello".chars();  // Stream of char codes

// Empty stream
Stream<String> empty = Stream.empty();
```

### Stream Characteristics

| Characteristic | Description |
|---------------|-------------|
| **Lazy** | Nothing happens until a terminal operation is called |
| **Single-use** | A stream can only be consumed once |
| **Non-interfering** | Don't modify the source during processing |
| **Stateless preferred** | Avoid operations that depend on external state |

### Proving Laziness

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

Stream<Integer> stream = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);
        return n % 2 == 0;
    })
    .map(n -> {
        System.out.println("Mapping: " + n);
        return n * 10;
    });

System.out.println("Stream created, but nothing printed yet!");

// Only when we call a terminal operation:
List<Integer> result = stream.toList();
// Now it prints:
// Filtering: 1
// Filtering: 2
// Mapping: 2
// Filtering: 3
// Filtering: 4
// Mapping: 4
// Filtering: 5
```

---

## 4) map vs flatMap — Understanding the Difference

### map: One-to-One Transformation

`map` transforms each element to exactly one output element.

```java
List<String> words = List.of("hello", "world");

// map: String -> Integer (length)
List<Integer> lengths = words.stream()
    .map(String::length)
    .toList();
// Result: [5, 5]

// map: String -> String (uppercase)
List<String> upper = words.stream()
    .map(String::toUpperCase)
    .toList();
// Result: ["HELLO", "WORLD"]
```

### flatMap: One-to-Many with Flattening

`flatMap` transforms each element to a stream of elements, then flattens all streams into one.

```java
List<String> sentences = List.of("Hello World", "Java Streams");

// Without flatMap (nested structure)
List<String[]> nested = sentences.stream()
    .map(s -> s.split(" "))
    .toList();
// Result: [["Hello", "World"], ["Java", "Streams"]]

// With flatMap (flattened)
List<String> words = sentences.stream()
    .flatMap(s -> Arrays.stream(s.split(" ")))
    .toList();
// Result: ["Hello", "World", "Java", "Streams"]
```

### Real-World flatMap Scenarios

#### Scenario 1: Processing nested collections
```java
record Order(String id, List<Item> items) {}
record Item(String name, double price) {}

List<Order> orders = List.of(
    new Order("O1", List.of(new Item("Book", 29.99), new Item("Pen", 2.99))),
    new Order("O2", List.of(new Item("Laptop", 999.99)))
);

// Get all item names across all orders
List<String> allItemNames = orders.stream()
    .flatMap(order -> order.items().stream())
    .map(Item::name)
    .toList();
// Result: ["Book", "Pen", "Laptop"]

// Calculate total value of all orders
double totalValue = orders.stream()
    .flatMap(order -> order.items().stream())
    .mapToDouble(Item::price)
    .sum();
// Result: 1032.97
```

#### Scenario 2: Optional chaining
```java
record User(String name, Optional<Address> address) {}
record Address(Optional<String> city) {}

Optional<User> user = findUser("123");

// Without flatMap - awkward nested optionals
Optional<Optional<Optional<String>>> nested = user.map(User::address)
    .map(addr -> addr.map(Address::city));

// With flatMap - clean chain
Optional<String> city = user
    .flatMap(User::address)
    .flatMap(Address::city);
```

---

## 5) filter + map + collect — The Classic Pattern

This combination is the most common stream pattern you'll use daily.

```java
record Employee(String name, int age, double salary, String department) {}

List<Employee> employees = List.of(
    new Employee("Alice", 30, 75000, "Engineering"),
    new Employee("Bob", 25, 55000, "Marketing"),
    new Employee("Carol", 35, 95000, "Engineering"),
    new Employee("David", 28, 60000, "Marketing"),
    new Employee("Eve", 32, 82000, "Engineering")
);

// Pattern: Filter → Map → Collect
List<String> seniorEngineers = employees.stream()
    .filter(e -> e.department().equals("Engineering"))  // Keep engineers
    .filter(e -> e.age() >= 30)                         // Keep seniors
    .map(Employee::name)                                // Extract names
    .toList();                                          // Collect to list
// Result: ["Alice", "Carol", "Eve"]

// Chaining multiple operations
Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::department,
        Collectors.averagingDouble(Employee::salary)
    ));
// Result: {Engineering=84000.0, Marketing=57500.0}
```

---

## 6) reduce — Folding Values

`reduce` combines all elements into a single result by repeatedly applying a binary operator.

### Basic Reduce Operations

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

// Sum with identity value
int sum = numbers.stream().reduce(0, Integer::sum);
// Calculation: 0 + 1 = 1, 1 + 2 = 3, 3 + 3 = 6, 6 + 4 = 10, 10 + 5 = 15
// Result: 15

// Product
int product = numbers.stream().reduce(1, (a, b) -> a * b);
// Result: 120

// Without identity (returns Optional)
Optional<Integer> max = numbers.stream().reduce(Integer::max);
// Result: Optional[5]

// String concatenation
List<String> words = List.of("a", "b", "c");
String joined = words.stream().reduce("", String::concat);
// Result: "abc"
```

### How Reduce Works (Visual)

```
reduce(identity, accumulator)

         Stream: [1, 2, 3, 4, 5]
         Identity: 0
         Accumulator: Integer::sum

         Step 1: 0 + 1 = 1
         Step 2: 1 + 2 = 3
         Step 3: 3 + 3 = 6
         Step 4: 6 + 4 = 10
         Step 5: 10 + 5 = 15
         
         Result: 15
```

### Complex Reduce Examples

```java
// Find longest string
List<String> words = List.of("apple", "pie", "banana", "kiwi");
Optional<String> longest = words.stream()
    .reduce((a, b) -> a.length() > b.length() ? a : b);
// Result: Optional["banana"]

// Reduce with different types (using identity, accumulator, combiner)
record Employee(String name, double salary) {}
List<Employee> employees = List.of(
    new Employee("Alice", 50000),
    new Employee("Bob", 60000),
    new Employee("Carol", 70000)
);

double totalSalary = employees.stream()
    .reduce(
        0.0,                           // Identity
        (sum, e) -> sum + e.salary(),  // Accumulator
        Double::sum                    // Combiner (for parallel)
    );
// Result: 180000.0
```

---

## 7) collect — Building Complex Structures

While `reduce` folds into a single value, `collect` builds complex structures like Lists, Maps, and Sets.

### Basic Collectors

```java
List<String> names = List.of("Alice", "Bob", "Carol");

// To different collection types
List<String> list = names.stream().collect(Collectors.toList());
Set<String> set = names.stream().collect(Collectors.toSet());
LinkedList<String> linked = names.stream()
    .collect(Collectors.toCollection(LinkedList::new));

// Joining strings
String joined = names.stream().collect(Collectors.joining(", "));
// Result: "Alice, Bob, Carol"

String withPrefixSuffix = names.stream()
    .collect(Collectors.joining(", ", "[", "]"));
// Result: "[Alice, Bob, Carol]"
```

### groupingBy — SQL's GROUP BY

```java
record Employee(String name, int age, double salary, String dept) {}

List<Employee> employees = List.of(
    new Employee("Alice", 30, 75000, "Engineering"),
    new Employee("Bob", 30, 55000, "Marketing"),
    new Employee("Carol", 25, 65000, "Engineering"),
    new Employee("David", 25, 45000, "Marketing")
);

// Group by department
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::dept));
// Result: {Engineering=[Alice, Carol], Marketing=[Bob, David]}

// Group by department, count employees
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));
// Result: {Engineering=2, Marketing=2}

// Group by department, average salary
Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.averagingDouble(Employee::salary)
    ));
// Result: {Engineering=70000.0, Marketing=50000.0}

// Multi-level grouping
Map<String, Map<Integer, List<Employee>>> byDeptThenAge = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.groupingBy(Employee::age)
    ));
```

### partitioningBy — Boolean Split

```java
// Partition into two groups based on boolean condition
Map<Boolean, List<Employee>> partitioned = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.salary() >= 60000));

List<Employee> highEarners = partitioned.get(true);   // [Alice, Carol]
List<Employee> others = partitioned.get(false);       // [Bob, David]
```

### toMap — Building Lookup Maps

```java
// Simple key-value map
Map<String, Double> salaryByName = employees.stream()
    .collect(Collectors.toMap(
        Employee::name,      // Key
        Employee::salary     // Value
    ));
// Result: {Alice=75000.0, Bob=55000.0, Carol=65000.0, David=45000.0}

// Handling duplicate keys
List<Employee> withDuplicates = List.of(
    new Employee("Alice", 30, 75000, "Eng"),
    new Employee("Alice", 25, 80000, "Eng")  // Duplicate name!
);

// Merge function for duplicates (keep higher salary)
Map<String, Double> salaryMap = withDuplicates.stream()
    .collect(Collectors.toMap(
        Employee::name,
        Employee::salary,
        (existing, replacement) -> Math.max(existing, replacement)
    ));
// Result: {Alice=80000.0}
```

---

## 8) Optional — Safe Null Handling

### Why Optional Matters

```java
// The problem with nulls
String name = employee.getManager().getName();  // NPE if manager is null!

// With Optional
Optional<String> name = Optional.ofNullable(employee.getManager())
    .map(Manager::getName);
```

### Creating Optionals

```java
Optional<String> present = Optional.of("Hello");        // Must be non-null
Optional<String> nullable = Optional.ofNullable(null);  // Can be null
Optional<String> empty = Optional.empty();              // Explicitly empty
```

### Using Optionals Safely

```java
Optional<String> opt = findUserName(userId);

// ❌ BAD - defeats the purpose
if (opt.isPresent()) {
    String name = opt.get();
}

// ❌ WORSE - throws if empty
String name = opt.get();

// ✅ GOOD - provide default
String name = opt.orElse("Unknown");

// ✅ GOOD - lazy default (computed only if empty)
String name = opt.orElseGet(() -> computeDefaultName());

// ✅ GOOD - throw custom exception
String name = opt.orElseThrow(() -> new UserNotFoundException(userId));

// ✅ GOOD - transform if present
opt.map(String::toUpperCase).ifPresent(System.out::println);

// ✅ GOOD - chain optionals
Optional<City> city = findUser(id)
    .flatMap(User::getAddress)
    .flatMap(Address::getCity);
```

### Common Patterns

```java
// Find first matching element
Optional<Employee> first = employees.stream()
    .filter(e -> e.salary() > 100000)
    .findFirst();

// Execute action if present
first.ifPresent(e -> System.out.println("Found: " + e.name()));

// Execute different actions based on presence
first.ifPresentOrElse(
    e -> System.out.println("Found: " + e.name()),
    () -> System.out.println("No match found")
);

// Filter on Optional
Optional<String> filtered = Optional.of("hello")
    .filter(s -> s.length() > 3);  // Optional["hello"]

Optional<String> empty = Optional.of("hi")
    .filter(s -> s.length() > 3);  // Optional.empty
```

---

## 9) Side-Effects — The Hidden Danger

### What are Side Effects?

A side effect occurs when a function modifies state outside its scope.

```java
// ❌ BAD: Modifying external list inside stream
List<String> results = new ArrayList<>();
employees.stream()
    .filter(e -> e.salary() > 50000)
    .map(Employee::name)
    .forEach(results::add);  // Side effect!

// ✅ GOOD: Collect properly
List<String> results = employees.stream()
    .filter(e -> e.salary() > 50000)
    .map(Employee::name)
    .toList();
```

### Why Side Effects are Dangerous

1. **Non-deterministic with parallel streams**
```java
List<Integer> results = new ArrayList<>();
IntStream.range(0, 1000)
    .parallel()
    .forEach(results::add);  // Race condition! List corrupted!
```

2. **Breaks laziness assumptions**
```java
List<String> seen = new ArrayList<>();
Stream<String> stream = names.stream()
    .filter(n -> {
        seen.add(n);  // Side effect in intermediate op!
        return n.length() > 3;
    });
// seen might be partially filled or empty depending on when/if terminal op runs
```

### Acceptable Side Effects

```java
// Logging/debugging (peek is designed for this)
List<String> result = names.stream()
    .peek(n -> logger.debug("Processing: {}", n))
    .filter(n -> n.length() > 3)
    .toList();

// Terminal operation side effects (forEach is designed for this)
employees.stream()
    .forEach(e -> emailService.sendNotification(e));
```

---

## 10) parallelStream — Use with Caution

### When Parallel Streams Help

```java
// ✅ CPU-intensive, independent operations
List<Image> thumbnails = images.parallelStream()
    .map(img -> resizeImage(img, 100, 100))  // CPU-heavy, pure function
    .toList();

// ✅ Large datasets with simple operations
long count = hugeList.parallelStream()
    .filter(x -> x > 0)
    .count();
```

### When to Avoid Parallel Streams

```java
// ❌ I/O operations (network, database)
users.parallelStream()
    .forEach(user -> saveToDatabase(user));  // Connection pool exhaustion!

// ❌ Small collections (overhead > benefit)
smallList.parallelStream().map(x -> x * 2).toList();

// ❌ Ordered operations
list.parallelStream()
    .limit(10)  // Expensive in parallel - needs coordination
    .toList();

// ❌ Shared mutable state
List<String> result = new ArrayList<>();
stream.parallelStream()
    .forEach(result::add);  // Corrupts the list!
```

### Performance Reality Check

```java
// Parallel is NOT always faster!
// Benchmark before using:

// Sequential: Often faster for < 10,000 elements
List<Integer> result1 = numbers.stream()
    .map(n -> n * 2)
    .toList();

// Parallel: Better for CPU-heavy ops on large datasets
List<Integer> result2 = numbers.parallelStream()
    .map(n -> expensiveComputation(n))
    .toList();
```

---

## 11) Mini Practice Problems

### Problem 1: Word Frequency Map
```java
String text = "the quick brown fox jumps over the lazy dog the fox";

// Solution
Map<String, Long> frequency = Arrays.stream(text.split(" "))
    .collect(Collectors.groupingBy(
        Function.identity(),
        Collectors.counting()
    ));
// Result: {the=3, quick=1, brown=1, fox=2, ...}
```

### Problem 2: Top N Salaries
```java
// Get top 3 highest paid employees
List<Employee> top3 = employees.stream()
    .sorted(Comparator.comparing(Employee::salary).reversed())
    .limit(3)
    .toList();
```

### Problem 3: Flatten and Sum Evens
```java
List<List<Integer>> nested = List.of(
    List.of(1, 2, 3),
    List.of(4, 5, 6),
    List.of(7, 8, 9)
);

int sumOfEvens = nested.stream()
    .flatMap(List::stream)
    .filter(n -> n % 2 == 0)
    .mapToInt(Integer::intValue)
    .sum();
// Result: 20 (2 + 4 + 6 + 8)
```

### Problem 4: Partition and Average
```java
Map<Boolean, Double> avgByEligibility = employees.stream()
    .collect(Collectors.partitioningBy(
        e -> e.salary() >= 60000,
        Collectors.averagingDouble(Employee::salary)
    ));
// Result: {true=70000.0, false=50000.0}
```

---

## 12) Key Concept Questions (Interview Prep)

| Question | Answer |
|----------|--------|
| Difference between intermediate and terminal ops? | Intermediate ops are lazy and return streams; terminal ops trigger execution and produce results |
| Why are streams lazy? | Efficiency — avoids processing elements that won't be needed (e.g., with limit()) |
| map vs flatMap? | map: 1→1 transformation; flatMap: 1→many with flattening |
| reduce vs collect? | reduce: fold into single value; collect: build complex structures |
| Why avoid side-effects? | Non-deterministic with parallel; breaks laziness assumptions; harder to reason about |
| When to use parallelStream? | CPU-heavy, independent operations on large datasets with no shared mutable state |

---

*Last updated: December 2024*
