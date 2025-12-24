# Java Streams — Quick Reference Cheatsheet 🚀

> **Purpose:** Fast review before interviews or quick lookup during coding

---

## 📌 Core Concept (One Line)

```
Source → Intermediate (lazy) → Terminal (triggers execution)
```

---

## 🔷 Functional Interfaces

| Interface | Method | Use |
|-----------|--------|-----|
| `Predicate<T>` | `test(T) → boolean` | Filtering |
| `Function<T,R>` | `apply(T) → R` | Transformation |
| `Consumer<T>` | `accept(T) → void` | Side effects |
| `Supplier<T>` | `get() → T` | Lazy values |

```java
Predicate<Integer> isEven = n -> n % 2 == 0;
Function<String, Integer> len = String::length;
Consumer<String> print = System.out::println;
Supplier<Long> now = System::currentTimeMillis;
```

---

## 🔷 Creating Streams

```java
list.stream()                          // From collection
list.parallelStream()                  // Parallel
Arrays.stream(arr)                     // From array
Stream.of("a", "b", "c")               // From values
IntStream.range(1, 10)                 // 1 to 9
IntStream.rangeClosed(1, 10)           // 1 to 10
Stream.iterate(0, n -> n + 2)          // Infinite: 0,2,4,6...
Stream.generate(Math::random)          // Infinite randoms
"hello".chars()                        // IntStream of chars
```

---

## 🔷 Intermediate Operations (Lazy)

### filter — Keep matching elements
```java
// Keep salaries > 50000
employees.stream().filter(e -> e.salary() > 50000)
```

### map — Transform 1:1
```java
// Get all names
employees.stream().map(Employee::name)
```

### flatMap — Flatten nested structures
```java
// Get all skills from all employees
employees.stream().flatMap(e -> e.skills().stream())

// Split sentences into words
sentences.stream().flatMap(s -> Arrays.stream(s.split(" ")))
```

### sorted — Order elements
```java
.sorted()                                         // Natural order
.sorted(Comparator.reverseOrder())                // Descending
.sorted(Comparator.comparing(Employee::salary))   // By field
.sorted(Comparator.comparing(E::age).thenComparing(E::name))  // Multi-field
```

### distinct — Remove duplicates
```java
numbers.stream().distinct()  // Uses equals()
```

### limit / skip — Pagination
```java
.limit(10)           // First 10
.skip(5)             // Skip first 5
.skip(page * size).limit(size)  // Pagination
```

### peek — Debug (don't use for logic!)
```java
.peek(e -> System.out.println("Processing: " + e))
```

---

## 🔷 Terminal Operations (Execute Pipeline)

### collect — Build collections/maps
```java
.collect(Collectors.toList())
.collect(Collectors.toSet())
.toList()  // Java 16+ (immutable)
```

### forEach — Iterate with side effect
```java
.forEach(System.out::println)
```

### reduce — Fold to single value
```java
.reduce(0, Integer::sum)                // Sum with identity
.reduce(Integer::max)                   // Max (returns Optional)
.reduce("", (a,b) -> a + b)             // Concat strings
```

### count / min / max
```java
.count()                                 // Total elements
.min(Comparator.comparing(E::salary))    // Returns Optional
.max(Comparator.comparing(E::salary))
```

### anyMatch / allMatch / noneMatch
```java
.anyMatch(e -> e.salary() > 100000)      // Any high earner?
.allMatch(e -> e.age() >= 18)            // All adults?
.noneMatch(e -> e.salary() < 0)          // No negatives?
```

### findFirst / findAny
```java
.filter(e -> e.dept().equals("IT")).findFirst()  // First IT employee
.findAny()  // Any match (faster in parallel)
```

---

## 🔷 Collectors — The Power Tools

### groupingBy — GROUP BY equivalent
```java
// Group by department
.collect(Collectors.groupingBy(Employee::dept))
// Result: Map<String, List<Employee>>

// Group + Count
.collect(Collectors.groupingBy(Employee::dept, Collectors.counting()))
// Result: Map<String, Long>

// Group + Average
.collect(Collectors.groupingBy(Employee::dept, 
    Collectors.averagingDouble(Employee::salary)))
// Result: Map<String, Double>

// Group + Sum
.collect(Collectors.groupingBy(Employee::dept,
    Collectors.summingDouble(Employee::salary)))
```

### partitioningBy — Split into true/false
```java
.collect(Collectors.partitioningBy(e -> e.salary() >= 50000))
// Result: Map<Boolean, List<Employee>>
```

### toMap — Build lookup map
```java
// ID → Employee
.collect(Collectors.toMap(Employee::id, Function.identity()))

// Handle duplicate keys
.collect(Collectors.toMap(E::name, E::salary, (old, new) -> old))
```

### joining — Concatenate strings
```java
.map(E::name).collect(Collectors.joining(", "))
// Result: "Alice, Bob, Carol"

.collect(Collectors.joining(", ", "[", "]"))
// Result: "[Alice, Bob, Carol]"
```

### Statistics
```java
.collect(Collectors.summarizingDouble(E::salary))
// Returns: count, sum, min, max, average
```

---

## 🔷 Common Patterns (Copy-Paste Ready)

### 1️⃣ Filter + Map + Collect
```java
List<String> names = employees.stream()
    .filter(e -> e.salary() > 50000)
    .map(Employee::name)
    .toList();
```

### 2️⃣ Top N by Field
```java
List<Employee> top3 = employees.stream()
    .sorted(Comparator.comparing(Employee::salary).reversed())
    .limit(3)
    .toList();
```

### 3️⃣ Group and Count
```java
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));
```

### 4️⃣ Find Max
```java
Optional<Employee> highest = employees.stream()
    .max(Comparator.comparing(Employee::salary));
```

### 5️⃣ Sum Values
```java
double total = employees.stream()
    .mapToDouble(Employee::salary)
    .sum();
```

### 6️⃣ Lookup Map
```java
Map<Long, Employee> byId = employees.stream()
    .collect(Collectors.toMap(Employee::id, Function.identity()));
```

### 7️⃣ Flatten Nested Lists
```java
List<String> allSkills = employees.stream()
    .flatMap(e -> e.skills().stream())
    .distinct()
    .toList();
```

### 8️⃣ Word Frequency
```java
Map<String, Long> freq = Arrays.stream(text.split("\\s+"))
    .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));
```

### 9️⃣ Partition
```java
Map<Boolean, List<Employee>> split = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.salary() >= 60000));
List<Employee> highEarners = split.get(true);
```

### 🔟 Check Condition
```java
boolean hasAdmin = users.stream().anyMatch(User::isAdmin);
```

---

## 🔷 map vs flatMap

```java
// map: 1 input → 1 output
[A, B, C] → map(toUpper) → [A, B, C]

// flatMap: 1 input → many outputs → flatten
[[1,2], [3,4]] → flatMap → [1, 2, 3, 4]
```

```java
// map keeps structure
Stream<List<String>> nested = emps.stream().map(E::skills);

// flatMap flattens
Stream<String> flat = emps.stream().flatMap(e -> e.skills().stream());
```

---

## 🔷 reduce vs collect

| reduce | collect |
|--------|---------|
| Fold to single value | Build mutable container |
| `sum, max, concat` | `List, Map, Set` |
| Creates new objects | Mutates accumulator |

```java
int sum = nums.stream().reduce(0, Integer::sum);        // reduce
List<String> list = stream.collect(Collectors.toList()); // collect
```

---

## 🔷 Optional Handling

```java
// ❌ NEVER
opt.get()  // Throws if empty!

// ✅ SAFE
opt.orElse("default")
opt.orElseGet(() -> compute())
opt.orElseThrow(() -> new NotFoundException())
opt.ifPresent(System.out::println)
```

---

## 🔷 Primitive Streams (Avoid Boxing)

```java
// ❌ Boxed (slower)
list.stream().map(x -> x * 2).reduce(0, Integer::sum);

// ✅ Primitive (faster)
list.stream().mapToInt(x -> x * 2).sum();

// Available: IntStream, LongStream, DoubleStream
IntStream.range(1, 100).sum();
```

---

## ⚠️ Common Traps

### Stream Reuse
```java
Stream<String> s = list.stream();
s.count();   // OK
s.toList();  // ❌ IllegalStateException - already consumed!
```

### Null in Stream
```java
// ❌ NPE
stream.map(String::toUpperCase)  // Crashes on null

// ✅ Safe
stream.filter(Objects::nonNull).map(String::toUpperCase)
```

### Duplicate Keys in toMap
```java
// ❌ Throws on duplicates
.collect(toMap(E::name, E::salary))

// ✅ Handle duplicates
.collect(toMap(E::name, E::salary, (a,b) -> a))
```

### Parallel Stream Dangers
```java
// ❌ NEVER
list.parallelStream().forEach(db::save);  // Connection pool!
list.parallelStream().forEach(result::add);  // Race condition!

// ✅ OK
list.parallelStream().map(cpuHeavy).toList();  // CPU-bound, no shared state
```

---

## 🎯 Interview Quick Answers

| Question | Answer |
|----------|--------|
| Lazy? | Nothing runs until terminal op |
| map vs flatMap? | 1:1 vs 1:many+flatten |
| reduce vs collect? | Single value vs container |
| Why Long in counting()? | Handles > 2 billion elements |
| When parallel? | CPU-heavy, large data, no I/O |
| Side effects bad? | Race conditions, unpredictable |

---

## 📋 Method Reference Types

```java
Math::abs           // Static method
String::length      // Instance method (on param)
System.out::println // Instance method (on object)
ArrayList::new      // Constructor
```

---

## 🔥 One-Liners

```java
// Sum
list.stream().mapToInt(i -> i).sum();

// Max
list.stream().max(Comparator.naturalOrder());

// Join
list.stream().collect(Collectors.joining(", "));

// Count matches
list.stream().filter(pred).count();

// First match
list.stream().filter(pred).findFirst();

// Any/All match
list.stream().anyMatch(pred);
list.stream().allMatch(pred);

// Distinct count
list.stream().distinct().count();

// To Set
list.stream().collect(Collectors.toSet());

// Sort descending
list.stream().sorted(Comparator.reverseOrder()).toList();
```

---

*Quick lookup, meaningful examples, interview ready!* ✨

