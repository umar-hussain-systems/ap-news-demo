# Java Stream API & Functional Programming — Complete Reference Guide

## 📌 Quick Reference

This document serves as a comprehensive API reference for Java Streams, with detailed explanations of when and why to use each operation.

---

## 1. Lambdas & Functional Interfaces

### Understanding Functional Interfaces

A **functional interface** is an interface with exactly one abstract method. Java 8 introduced `@FunctionalInterface` annotation to enforce this.

```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);  // Single abstract method
    
    // These are allowed:
    default int add(int a, int b) { return a + b; }  // default method
    static int multiply(int a, int b) { return a * b; }  // static method
}
```

### Core Functional Interfaces with Real-World Examples

```java
// Predicate<T> - Testing conditions
// Use case: Filtering, validation, business rules
Predicate<Integer> isEven = x -> x % 2 == 0;
Predicate<String> isValidEmail = email -> email.matches("^[\\w.-]+@[\\w.-]+\\.[a-z]{2,}$");
Predicate<Order> isEligibleForDiscount = order -> 
    order.total() > 100 && order.customer().isPremium();

// Combining predicates
Predicate<Integer> isPositiveEven = ((Predicate<Integer>) x -> x > 0).and(isEven);
Predicate<String> isBlank = String::isBlank;
Predicate<String> isNotBlank = isBlank.negate();

// Function<T, R> - Transformation
// Use case: Data mapping, conversions, DTOs
Function<String, Integer> parseAge = Integer::parseInt;
Function<Employee, EmployeeDTO> toDTO = emp -> new EmployeeDTO(emp.name(), emp.email());
Function<LocalDate, String> formatDate = date -> date.format(DateTimeFormatter.ISO_LOCAL_DATE);

// Function composition
Function<String, String> trim = String::trim;
Function<String, String> lower = String::toLowerCase;
Function<String, String> normalize = trim.andThen(lower);  // First trim, then lowercase

// Consumer<T> - Side effects
// Use case: Logging, sending notifications, saving to DB
Consumer<String> log = msg -> logger.info(msg);
Consumer<Order> sendConfirmation = order -> emailService.send(order.customerEmail(), "Confirmed!");
Consumer<Exception> handleError = ex -> {
    logger.error("Error: " + ex.getMessage());
    metricsService.incrementErrorCount();
};

// Chaining consumers
Consumer<User> audit = user -> auditLog.record(user.id(), "accessed");
Consumer<User> notify = user -> pushService.notify(user.id(), "Welcome!");
Consumer<User> onLogin = audit.andThen(notify);

// Supplier<T> - Lazy value generation
// Use case: Default values, factory methods, deferred computation
Supplier<Long> currentTime = System::currentTimeMillis;
Supplier<Connection> connectionFactory = () -> dataSource.getConnection();
Supplier<List<String>> emptyListFactory = ArrayList::new;

// UnaryOperator<T> - Same type transformation (extends Function<T, T>)
UnaryOperator<String> shout = s -> s.toUpperCase() + "!";
UnaryOperator<BigDecimal> addTax = amount -> amount.multiply(new BigDecimal("1.08"));

// BinaryOperator<T> - Combine two values of same type
BinaryOperator<Integer> sum = Integer::sum;
BinaryOperator<String> concat = String::concat;
BinaryOperator<BigDecimal> max = (a, b) -> a.compareTo(b) > 0 ? a : b;
```

### Method References Explained

Method references are shorthand for lambdas that just call an existing method.

```java
// Type 1: Static method reference
// Lambda: x -> Math.abs(x)
Function<Integer, Integer> abs = Math::abs;

// Type 2: Instance method of a parameter
// Lambda: s -> s.toUpperCase()
Function<String, String> upper = String::toUpperCase;

// Type 3: Instance method of a specific object
// Lambda: s -> System.out.println(s)
PrintStream out = System.out;
Consumer<String> print = out::println;

// Type 4: Constructor reference
// Lambda: () -> new ArrayList<>()
Supplier<List<String>> listFactory = ArrayList::new;
// Lambda: s -> new StringBuilder(s)
Function<String, StringBuilder> sbFactory = StringBuilder::new;
```

---

## 2. Intermediate Operations (Lazy)

**Key Point:** Intermediate operations are **lazy** — they don't process data until a terminal operation is called. They return a new Stream, allowing method chaining.

### map — Transform Each Element

**What it does:** Applies a function to each element, producing a new stream of transformed elements.

**When to use:** Converting between types, extracting fields, applying transformations.

```java
// Basic transformation
List<String> names = List.of("alice", "bob", "carol");
List<String> upperNames = names.stream()
    .map(String::toUpperCase)
    .toList();
// Result: ["ALICE", "BOB", "CAROL"]

// Object field extraction
record Person(String name, int age, String email) {}
List<Person> people = List.of(
    new Person("Alice", 30, "alice@example.com"),
    new Person("Bob", 25, "bob@example.com")
);

List<String> emails = people.stream()
    .map(Person::email)
    .toList();
// Result: ["alice@example.com", "bob@example.com"]

// Complex transformation
List<PersonDTO> dtos = people.stream()
    .map(p -> new PersonDTO(p.name(), p.email()))
    .toList();

// Chained maps
List<Integer> nameLengths = people.stream()
    .map(Person::name)
    .map(String::length)
    .toList();
// Result: [5, 3]
```

### filter — Select Elements by Condition

**What it does:** Keeps only elements that match the predicate.

**When to use:** Removing unwanted elements, implementing search/filter functionality.

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Simple filter
List<Integer> evens = numbers.stream()
    .filter(n -> n % 2 == 0)
    .toList();
// Result: [2, 4, 6, 8, 10]

// Filter with predicate variable (reusable)
Predicate<Integer> isGreaterThan5 = n -> n > 5;
List<Integer> large = numbers.stream()
    .filter(isGreaterThan5)
    .toList();
// Result: [6, 7, 8, 9, 10]

// Multiple conditions
record Product(String name, double price, boolean inStock) {}
List<Product> products = List.of(
    new Product("Laptop", 999.99, true),
    new Product("Phone", 599.99, false),
    new Product("Tablet", 399.99, true),
    new Product("Watch", 199.99, true)
);

List<Product> affordableInStock = products.stream()
    .filter(p -> p.inStock())
    .filter(p -> p.price() < 500)
    .toList();
// Result: [Tablet, Watch]

// Combining predicates
Predicate<Product> available = Product::inStock;
Predicate<Product> affordable = p -> p.price() < 500;
List<Product> result = products.stream()
    .filter(available.and(affordable))
    .toList();
```

### flatMap — Flatten Nested Structures

**What it does:** Maps each element to a stream, then flattens all streams into one.

**When to use:** Working with nested collections, Optional chains, one-to-many relationships.

```java
// Flattening nested lists
List<List<Integer>> nested = List.of(
    List.of(1, 2, 3),
    List.of(4, 5),
    List.of(6, 7, 8, 9)
);

List<Integer> flat = nested.stream()
    .flatMap(List::stream)
    .toList();
// Result: [1, 2, 3, 4, 5, 6, 7, 8, 9]

// Splitting strings into words
List<String> sentences = List.of(
    "Hello World",
    "Java Streams are powerful",
    "Functional programming rocks"
);

List<String> words = sentences.stream()
    .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
    .toList();
// Result: ["Hello", "World", "Java", "Streams", "are", "powerful", ...]

// Real-world: Orders with multiple items
record Order(String id, List<LineItem> items) {}
record LineItem(String product, int quantity, double price) {}

List<Order> orders = List.of(
    new Order("O1", List.of(
        new LineItem("Widget", 2, 10.00),
        new LineItem("Gadget", 1, 25.00)
    )),
    new Order("O2", List.of(
        new LineItem("Widget", 5, 10.00)
    ))
);

// Get all line items across all orders
List<LineItem> allItems = orders.stream()
    .flatMap(order -> order.items().stream())
    .toList();

// Calculate total revenue
double totalRevenue = orders.stream()
    .flatMap(order -> order.items().stream())
    .mapToDouble(item -> item.quantity() * item.price())
    .sum();
// Result: 95.00

// Find all unique products ordered
Set<String> uniqueProducts = orders.stream()
    .flatMap(order -> order.items().stream())
    .map(LineItem::product)
    .collect(Collectors.toSet());
// Result: {"Widget", "Gadget"}
```

### sorted — Order Elements

**What it does:** Sorts elements using natural order or a custom comparator.

**When to use:** Displaying data in order, finding top/bottom N, ordered processing.

```java
List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);

// Natural order (Comparable)
List<Integer> ascending = numbers.stream()
    .sorted()
    .toList();
// Result: [1, 1, 2, 3, 4, 5, 6, 9]

// Reverse order
List<Integer> descending = numbers.stream()
    .sorted(Comparator.reverseOrder())
    .toList();
// Result: [9, 6, 5, 4, 3, 2, 1, 1]

// Custom comparator
record Employee(String name, int age, double salary) {}
List<Employee> employees = List.of(
    new Employee("Alice", 30, 75000),
    new Employee("Bob", 25, 85000),
    new Employee("Carol", 35, 65000)
);

// Sort by single field
List<Employee> bySalary = employees.stream()
    .sorted(Comparator.comparing(Employee::salary))
    .toList();

// Sort descending
List<Employee> bySalaryDesc = employees.stream()
    .sorted(Comparator.comparing(Employee::salary).reversed())
    .toList();

// Sort by multiple fields
List<Employee> byAgeThenSalary = employees.stream()
    .sorted(Comparator
        .comparing(Employee::age)
        .thenComparing(Employee::salary).reversed())
    .toList();

// Null-safe sorting
List<String> withNulls = Arrays.asList("banana", null, "apple", null, "cherry");
List<String> sorted = withNulls.stream()
    .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
    .toList();
// Result: ["apple", "banana", "cherry", null, null]
```

### distinct — Remove Duplicates

**What it does:** Returns a stream with duplicate elements removed (uses `.equals()`).

**When to use:** Deduplication, unique values extraction.

```java
List<Integer> numbers = List.of(1, 2, 2, 3, 3, 3, 4, 4, 4, 4);

List<Integer> unique = numbers.stream()
    .distinct()
    .toList();
// Result: [1, 2, 3, 4]

// With objects (requires proper equals/hashCode)
record Tag(String name) {}
List<Tag> tags = List.of(
    new Tag("java"),
    new Tag("streams"),
    new Tag("java"),  // duplicate
    new Tag("functional")
);

List<Tag> uniqueTags = tags.stream()
    .distinct()
    .toList();
// Result: [Tag[name=java], Tag[name=streams], Tag[name=functional]]

// Distinct by field (custom approach)
List<Employee> employees = List.of(
    new Employee("Alice", 30, 75000),
    new Employee("Bob", 30, 85000),    // Same age as Alice
    new Employee("Carol", 35, 65000)
);

// Get employees with unique ages
List<Employee> uniqueByAge = employees.stream()
    .collect(Collectors.toMap(
        Employee::age,
        Function.identity(),
        (existing, replacement) -> existing  // Keep first
    ))
    .values()
    .stream()
    .toList();
```

### limit — Take First N Elements

**What it does:** Truncates the stream to at most N elements.

**When to use:** Pagination, top-N queries, limiting results.

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Take first 5
List<Integer> firstFive = numbers.stream()
    .limit(5)
    .toList();
// Result: [1, 2, 3, 4, 5]

// Pagination pattern
int pageSize = 3;
int pageNumber = 2;  // 0-indexed

List<Integer> page = numbers.stream()
    .skip(pageNumber * pageSize)  // Skip previous pages
    .limit(pageSize)              // Take page size
    .toList();
// Result: [7, 8, 9]

// Top N with sorting
record Product(String name, double rating) {}
List<Product> products = List.of(
    new Product("A", 4.5),
    new Product("B", 4.8),
    new Product("C", 4.2),
    new Product("D", 4.9),
    new Product("E", 4.1)
);

List<Product> top3 = products.stream()
    .sorted(Comparator.comparing(Product::rating).reversed())
    .limit(3)
    .toList();
// Result: [D(4.9), B(4.8), A(4.5)]
```

### skip — Skip First N Elements

**What it does:** Discards the first N elements.

**When to use:** Pagination, skipping headers, offsetting.

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// Skip first 3
List<Integer> afterThird = numbers.stream()
    .skip(3)
    .toList();
// Result: [4, 5, 6, 7, 8, 9, 10]

// Skip headers in data processing
List<String> csvLines = List.of(
    "Name,Age,Salary",     // Header
    "Alice,30,75000",
    "Bob,25,85000"
);

List<String[]> data = csvLines.stream()
    .skip(1)  // Skip header
    .map(line -> line.split(","))
    .toList();
```

### peek — Debug/Observe Without Modifying

**What it does:** Performs an action on each element while passing it through unchanged.

**When to use:** Debugging, logging, observing intermediate values.

⚠️ **Warning:** Don't use peek for business logic! It's meant for debugging only.

```java
List<String> names = List.of("alice", "bob", "carol");

// Debugging pipeline
List<String> result = names.stream()
    .peek(n -> System.out.println("Before filter: " + n))
    .filter(n -> n.length() > 3)
    .peek(n -> System.out.println("After filter: " + n))
    .map(String::toUpperCase)
    .peek(n -> System.out.println("After map: " + n))
    .toList();

// Output:
// Before filter: alice
// After filter: alice
// After map: ALICE
// Before filter: bob
// Before filter: carol
// After filter: carol
// After map: CAROL

// Production logging
List<Order> processed = orders.stream()
    .peek(order -> logger.debug("Processing order: {}", order.id()))
    .filter(Order::isValid)
    .peek(order -> metrics.increment("orders.valid"))
    .toList();
```

### takeWhile / dropWhile (Java 9+)

**What they do:** Take or drop elements while a condition is true.

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 1, 2, 3);

// Take while less than 4
List<Integer> taken = numbers.stream()
    .takeWhile(n -> n < 4)
    .toList();
// Result: [1, 2, 3]  (stops at first element >= 4)

// Drop while less than 4
List<Integer> dropped = numbers.stream()
    .dropWhile(n -> n < 4)
    .toList();
// Result: [4, 5, 1, 2, 3]  (starts from first element >= 4)

// Processing sorted data efficiently
List<Transaction> sortedByDate = getTransactionsSortedByDate();
List<Transaction> recentOnly = sortedByDate.stream()
    .dropWhile(t -> t.date().isBefore(cutoffDate))
    .toList();
```

---

## 3. Terminal Operations

**Key Point:** Terminal operations **trigger** the stream pipeline execution and produce a result (or side effect). After a terminal operation, the stream is consumed and cannot be reused.

### collect — Build Result Structures

The most versatile terminal operation. Covered in detail in Section 4.

```java
// To List
List<String> list = stream.collect(Collectors.toList());
List<String> immutableList = stream.toList();  // Java 16+

// To Set
Set<String> set = stream.collect(Collectors.toSet());

// To Map
Map<String, Integer> map = stream.collect(Collectors.toMap(
    Employee::name,
    Employee::age
));
```

### forEach — Perform Action on Each Element

**What it does:** Executes an action for each element (side effect operation).

**When to use:** Sending notifications, saving to database, printing.

```java
List<String> names = List.of("Alice", "Bob", "Carol");

// Simple iteration
names.stream().forEach(System.out::println);

// Or use List's forEach directly (more readable for simple cases)
names.forEach(System.out::println);

// Real-world usage
orders.stream()
    .filter(Order::isPending)
    .forEach(order -> emailService.sendReminder(order.customerEmail()));

// forEachOrdered - maintains order in parallel streams
names.parallelStream()
    .forEachOrdered(System.out::println);  // Guaranteed order: Alice, Bob, Carol
```

### reduce — Fold Into Single Value

**What it does:** Combines all elements into a single result using an accumulator function.

**When to use:** Summing, finding max/min, string concatenation, complex aggregations.

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

// With identity (never returns empty)
int sum = numbers.stream()
    .reduce(0, Integer::sum);
// Calculation: ((((0+1)+2)+3)+4)+5 = 15

int product = numbers.stream()
    .reduce(1, (a, b) -> a * b);
// Result: 120

// Without identity (returns Optional)
Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);
// Result: Optional[5]

// String operations
List<String> words = List.of("Hello", "World", "Java");
String sentence = words.stream()
    .reduce("", (a, b) -> a.isEmpty() ? b : a + " " + b);
// Result: "Hello World Java"

// Complex object reduction
record Transaction(double amount) {}
List<Transaction> transactions = List.of(
    new Transaction(100.0),
    new Transaction(-50.0),
    new Transaction(200.0)
);

double balance = transactions.stream()
    .map(Transaction::amount)
    .reduce(0.0, Double::sum);
// Result: 250.0

// Three-argument reduce (for parallel streams)
int totalLength = words.parallelStream()
    .reduce(
        0,                            // Identity
        (sum, word) -> sum + word.length(),  // Accumulator
        Integer::sum                   // Combiner
    );
```

### count — Count Elements

```java
List<String> names = List.of("Alice", "Bob", "Carol", "David");

long total = names.stream().count();
// Result: 4

long longNames = names.stream()
    .filter(n -> n.length() > 4)
    .count();
// Result: 2 (Alice, Carol)
```

### anyMatch / allMatch / noneMatch — Boolean Checks

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

// anyMatch - true if ANY element matches
boolean hasEven = numbers.stream()
    .anyMatch(n -> n % 2 == 0);
// Result: true

// allMatch - true if ALL elements match
boolean allPositive = numbers.stream()
    .allMatch(n -> n > 0);
// Result: true

// noneMatch - true if NO elements match
boolean noneNegative = numbers.stream()
    .noneMatch(n -> n < 0);
// Result: true

// Real-world examples
boolean hasAdminUser = users.stream()
    .anyMatch(User::isAdmin);

boolean allOrdersShipped = orders.stream()
    .allMatch(o -> o.status() == Status.SHIPPED);

boolean noOverduePayments = payments.stream()
    .noneMatch(Payment::isOverdue);
```

### findFirst / findAny — Get Single Element

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5);

// findFirst - first element in encounter order
Optional<Integer> first = numbers.stream()
    .filter(n -> n > 2)
    .findFirst();
// Result: Optional[3]

// findAny - any matching element (better for parallel)
Optional<Integer> any = numbers.parallelStream()
    .filter(n -> n > 2)
    .findAny();
// Result: Optional[3] or Optional[4] or Optional[5] (non-deterministic)

// Practical usage
Optional<User> admin = users.stream()
    .filter(User::isAdmin)
    .findFirst();

admin.ifPresent(u -> System.out.println("Found admin: " + u.name()));

// With default
User adminOrDefault = users.stream()
    .filter(User::isAdmin)
    .findFirst()
    .orElse(User.SYSTEM_ADMIN);
```

### min / max — Find Extremes

```java
List<Integer> numbers = List.of(3, 1, 4, 1, 5, 9, 2, 6);

Optional<Integer> min = numbers.stream()
    .min(Integer::compareTo);
// Result: Optional[1]

Optional<Integer> max = numbers.stream()
    .max(Integer::compareTo);
// Result: Optional[9]

// With objects
record Employee(String name, double salary) {}
List<Employee> employees = List.of(
    new Employee("Alice", 75000),
    new Employee("Bob", 85000),
    new Employee("Carol", 65000)
);

Optional<Employee> highestPaid = employees.stream()
    .max(Comparator.comparing(Employee::salary));
// Result: Optional[Employee[name=Bob, salary=85000.0]]

Optional<Employee> lowestPaid = employees.stream()
    .min(Comparator.comparing(Employee::salary));
// Result: Optional[Employee[name=Carol, salary=65000.0]]

// Get just the value
double maxSalary = employees.stream()
    .mapToDouble(Employee::salary)
    .max()
    .orElse(0);
```

### toArray — Convert to Array

```java
List<String> names = List.of("Alice", "Bob", "Carol");

// To Object array
Object[] objArray = names.stream().toArray();

// To specific type array
String[] stringArray = names.stream()
    .toArray(String[]::new);

// With transformation
String[] upperArray = names.stream()
    .map(String::toUpperCase)
    .toArray(String[]::new);
```

---

## 4. Collectors — Building Complex Results

The `Collectors` utility class provides common reduction operations.

### groupingBy — SQL-style GROUP BY

```java
record Employee(String name, int age, double salary, String dept) {}

List<Employee> employees = List.of(
    new Employee("Alice", 30, 75000, "Engineering"),
    new Employee("Bob", 25, 55000, "Marketing"),
    new Employee("Carol", 30, 85000, "Engineering"),
    new Employee("David", 35, 65000, "Marketing"),
    new Employee("Eve", 25, 60000, "Engineering")
);

// Basic grouping
Map<String, List<Employee>> byDept = employees.stream()
    .collect(Collectors.groupingBy(Employee::dept));
// Result: {Engineering=[Alice, Carol, Eve], Marketing=[Bob, David]}

// Group + count
Map<String, Long> countByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.counting()
    ));
// Result: {Engineering=3, Marketing=2}

// Group + average
Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.averagingDouble(Employee::salary)
    ));
// Result: {Engineering=73333.33, Marketing=60000.0}

// Group + sum
Map<String, Double> totalSalaryByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.summingDouble(Employee::salary)
    ));

// Group + collect to set
Map<String, Set<Integer>> agesByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.mapping(Employee::age, Collectors.toSet())
    ));
// Result: {Engineering={25, 30}, Marketing={25, 35}}

// Group + max
Map<String, Optional<Employee>> highestPaidByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.maxBy(Comparator.comparing(Employee::salary))
    ));

// Multi-level grouping
Map<String, Map<Integer, List<Employee>>> byDeptThenAge = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.groupingBy(Employee::age)
    ));
// Result: {Engineering={30=[Alice, Carol], 25=[Eve]}, ...}

// With specific map type
Map<String, Long> sorted = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        TreeMap::new,  // Sorted map
        Collectors.counting()
    ));
```

### partitioningBy — Boolean Split

```java
// Partition into two groups
Map<Boolean, List<Employee>> byEligibility = employees.stream()
    .collect(Collectors.partitioningBy(e -> e.salary() >= 70000));
// Result: {true=[Alice, Carol], false=[Bob, David, Eve]}

// Partition + count
Map<Boolean, Long> countByEligibility = employees.stream()
    .collect(Collectors.partitioningBy(
        e -> e.salary() >= 70000,
        Collectors.counting()
    ));
// Result: {true=2, false=3}

// Practical use
Map<Boolean, List<Order>> ordersByStatus = orders.stream()
    .collect(Collectors.partitioningBy(Order::isCompleted));

List<Order> completed = ordersByStatus.get(true);
List<Order> pending = ordersByStatus.get(false);
```

### toMap — Build Lookup Maps

```java
// Simple key-value
Map<String, Double> salaryByName = employees.stream()
    .collect(Collectors.toMap(
        Employee::name,
        Employee::salary
    ));

// With merge function (handles duplicate keys)
Map<String, Double> salaryByDept = employees.stream()
    .collect(Collectors.toMap(
        Employee::dept,
        Employee::salary,
        Double::sum  // Sum salaries for same department
    ));

// Keep whole object as value
Map<String, Employee> employeeByName = employees.stream()
    .collect(Collectors.toMap(
        Employee::name,
        Function.identity()
    ));

// With specific map type
Map<String, Double> sortedSalaryMap = employees.stream()
    .collect(Collectors.toMap(
        Employee::name,
        Employee::salary,
        (a, b) -> a,
        TreeMap::new  // Sorted map
    ));
```

### Numeric Collectors

```java
// Averaging
double avgSalary = employees.stream()
    .collect(Collectors.averagingDouble(Employee::salary));

// Summing
double totalSalary = employees.stream()
    .collect(Collectors.summingDouble(Employee::salary));

// Statistics (all at once)
DoubleSummaryStatistics stats = employees.stream()
    .collect(Collectors.summarizingDouble(Employee::salary));

System.out.println("Count: " + stats.getCount());
System.out.println("Sum: " + stats.getSum());
System.out.println("Min: " + stats.getMin());
System.out.println("Max: " + stats.getMax());
System.out.println("Average: " + stats.getAverage());
```

### joining — String Concatenation

```java
List<String> names = List.of("Alice", "Bob", "Carol");

// Simple join
String joined = names.stream()
    .collect(Collectors.joining());
// Result: "AliceBobCarol"

// With delimiter
String csv = names.stream()
    .collect(Collectors.joining(", "));
// Result: "Alice, Bob, Carol"

// With delimiter, prefix, suffix
String formatted = names.stream()
    .collect(Collectors.joining(", ", "[", "]"));
// Result: "[Alice, Bob, Carol]"

// Real-world: SQL IN clause
String inClause = ids.stream()
    .map(String::valueOf)
    .collect(Collectors.joining(", ", "IN (", ")"));
// Result: "IN (1, 2, 3, 4)"
```

### mapping / flatMapping — Transform Before Collecting

```java
// Extract names while grouping
Map<String, List<String>> namesByDept = employees.stream()
    .collect(Collectors.groupingBy(
        Employee::dept,
        Collectors.mapping(Employee::name, Collectors.toList())
    ));
// Result: {Engineering=["Alice", "Carol", "Eve"], Marketing=["Bob", "David"]}

// flatMapping for nested structures
record Order(String customer, List<String> items) {}
List<Order> orders = List.of(
    new Order("Alice", List.of("Book", "Pen")),
    new Order("Alice", List.of("Notebook")),
    new Order("Bob", List.of("Laptop"))
);

Map<String, Set<String>> itemsByCustomer = orders.stream()
    .collect(Collectors.groupingBy(
        Order::customer,
        Collectors.flatMapping(
            order -> order.items().stream(),
            Collectors.toSet()
        )
    ));
// Result: {Alice={"Book", "Pen", "Notebook"}, Bob={"Laptop"}}
```

### collectingAndThen — Post-process Result

```java
// Collect to unmodifiable list
List<String> immutable = names.stream()
    .collect(Collectors.collectingAndThen(
        Collectors.toList(),
        Collections::unmodifiableList
    ));

// Find max and get value (not Optional)
String longest = names.stream()
    .collect(Collectors.collectingAndThen(
        Collectors.maxBy(Comparator.comparing(String::length)),
        opt -> opt.orElse("")
    ));

// Collect and wrap in custom type
EmployeeDirectory directory = employees.stream()
    .collect(Collectors.collectingAndThen(
        Collectors.toMap(Employee::name, Function.identity()),
        EmployeeDirectory::new
    ));
```

---

## 5. Comparators — Sorting Made Easy

```java
record Employee(String name, int age, double salary, String dept) {}

// Basic comparators
Comparator<Employee> byName = Comparator.comparing(Employee::name);
Comparator<Employee> byAge = Comparator.comparing(Employee::age);
Comparator<Employee> bySalary = Comparator.comparing(Employee::salary);

// Reversed order
Comparator<Employee> bySalaryDesc = Comparator.comparing(Employee::salary).reversed();

// Multiple fields
Comparator<Employee> byDeptThenName = Comparator
    .comparing(Employee::dept)
    .thenComparing(Employee::name);

Comparator<Employee> byAgeThenSalaryDesc = Comparator
    .comparing(Employee::age)
    .thenComparing(Employee::salary).reversed();

// Null handling
Comparator<Employee> byNameNullsFirst = Comparator.comparing(
    Employee::name,
    Comparator.nullsFirst(Comparator.naturalOrder())
);

Comparator<Employee> byNameNullsLast = Comparator.comparing(
    Employee::name,
    Comparator.nullsLast(Comparator.naturalOrder())
);

// Case-insensitive string comparison
Comparator<Employee> byNameIgnoreCase = Comparator.comparing(
    Employee::name,
    String.CASE_INSENSITIVE_ORDER
);

// Custom extraction with comparator
Comparator<String> byLength = Comparator.comparingInt(String::length);
Comparator<String> byLengthThenAlpha = Comparator
    .comparingInt(String::length)
    .thenComparing(Comparator.naturalOrder());
```

---

## 6. Imperative vs Stream Comparison

### groupingBy + counting Explained

**Stream version:**
```java
Map<Integer, Long> countByAge = employees.stream()
    .collect(Collectors.groupingBy(Employee::age, Collectors.counting()));
```

**Equivalent imperative code:**
```java
Map<Integer, Long> countByAge = new HashMap<>();
for (Employee e : employees) {
    Integer age = e.age();
    Long currentCount = countByAge.getOrDefault(age, 0L);
    countByAge.put(age, currentCount + 1);
}
```

**Even more verbose (what the code actually does):**
```java
Map<Integer, Long> countByAge = new HashMap<>();
for (Employee e : employees) {
    Integer age = e.age();
    if (countByAge.containsKey(age)) {
        Long currentCount = countByAge.get(age);
        countByAge.put(age, currentCount + 1);
    } else {
        countByAge.put(age, 1L);
    }
}
```

---

## 7. Additional Concepts

### Laziness — Why It Matters

```java
List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

// With laziness - processes only what's needed
Optional<Integer> result = numbers.stream()
    .filter(n -> {
        System.out.println("Filtering: " + n);
        return n > 5;
    })
    .findFirst();
// Output: Filtering: 1, 2, 3, 4, 5, 6
// Stops after finding first match (6)

// Without laziness (hypothetically) - would process all 10 elements
```

### Stream Reuse Warning

```java
Stream<String> stream = names.stream();

// First use - OK
long count = stream.count();

// Second use - EXCEPTION!
List<String> list = stream.toList();  // IllegalStateException: stream has already been operated upon
```

### Parallel Streams

```java
// Create parallel stream
List<Integer> result = numbers.parallelStream()
    .map(this::expensiveComputation)
    .toList();

// Convert sequential to parallel
List<Integer> result2 = numbers.stream()
    .parallel()
    .map(this::expensiveComputation)
    .toList();

// Maintain order in parallel
List<Integer> ordered = numbers.parallelStream()
    .map(this::compute)
    .forEachOrdered(System.out::println);  // Preserves order
```

### Optional Safety

```java
// ❌ AVOID
Optional<String> opt = findName();
String name = opt.get();  // NoSuchElementException if empty!

// ✅ PREFERRED
String name = opt.orElse("Unknown");
String name = opt.orElseGet(() -> computeDefault());
String name = opt.orElseThrow(() -> new NotFoundException());

opt.ifPresent(System.out::println);
opt.ifPresentOrElse(
    System.out::println,
    () -> System.out.println("Not found")
);
```

---

## 8. Quick Reference Card

| Operation | Type | Returns | Use Case |
|-----------|------|---------|----------|
| `map` | Intermediate | `Stream<R>` | Transform elements |
| `filter` | Intermediate | `Stream<T>` | Select elements |
| `flatMap` | Intermediate | `Stream<R>` | Flatten nested |
| `sorted` | Intermediate | `Stream<T>` | Order elements |
| `distinct` | Intermediate | `Stream<T>` | Remove duplicates |
| `limit` | Intermediate | `Stream<T>` | Take first N |
| `skip` | Intermediate | `Stream<T>` | Skip first N |
| `peek` | Intermediate | `Stream<T>` | Debug/observe |
| `collect` | Terminal | `R` | Build result |
| `forEach` | Terminal | `void` | Side effects |
| `reduce` | Terminal | `Optional<T>` / `T` | Fold to single value |
| `count` | Terminal | `long` | Count elements |
| `min`/`max` | Terminal | `Optional<T>` | Find extremes |
| `findFirst` | Terminal | `Optional<T>` | First match |
| `anyMatch` | Terminal | `boolean` | Any matches? |
| `allMatch` | Terminal | `boolean` | All match? |
| `noneMatch` | Terminal | `boolean` | None match? |

---

*Last updated: December 2024*
