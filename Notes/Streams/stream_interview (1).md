# Java Streams — Practical Interview Guide (Employee-Based Examples)

## 📌 Overview

This document provides **practical, real-world stream examples** using a consistent `Employee` data model. All examples are interview-ready and demonstrate common business scenarios.

---

## 🏢 Data Model & Test Data

We use this consistent data model throughout all examples:

```java
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import static java.util.stream.Collectors.*;

// Core Employee record
record Employee(
    Long id,
    String name, 
    int age, 
    double salary, 
    String department,
    LocalDate hireDate,
    List<String> skills
) {}

// Sample data for all examples
List<Employee> employees = List.of(
    new Employee(1L, "Umar", 30, 7000, "Backend", 
        LocalDate.of(2020, 3, 15), List.of("Java", "Spring", "SQL")),
    new Employee(2L, "Ali", 24, 3000, "QA", 
        LocalDate.of(2022, 6, 1), List.of("Selenium", "Python")),
    new Employee(3L, "Sara", 28, 9000, "Backend", 
        LocalDate.of(2019, 1, 10), List.of("Java", "Kubernetes", "AWS")),
    new Employee(4L, "Hina", 30, 5000, "Support", 
        LocalDate.of(2021, 8, 20), List.of("Jira", "Communication")),
    new Employee(5L, "Zain", 24, 3500, "Backend", 
        LocalDate.of(2023, 2, 1), List.of("Java", "React")),
    new Employee(6L, "Noor", 28, 6500, "QA", 
        LocalDate.of(2020, 11, 5), List.of("Selenium", "Java", "API Testing")),
    new Employee(7L, "Ahmed", 35, 12000, "Backend", 
        LocalDate.of(2017, 4, 12), List.of("Java", "Microservices", "Kafka")),
    new Employee(8L, "Fatima", 32, 8500, "Frontend", 
        LocalDate.of(2018, 7, 25), List.of("React", "TypeScript", "CSS"))
);
```

---

## 1) Stream Mental Model (Interview Answer)

**Interview Question:** "Explain how Java Streams work."

**Perfect Answer:**
> "A stream pipeline has three parts: a **Source** (collection, array, or generator), **Intermediate operations** (lazy, chainable like map/filter), and a **Terminal operation** (triggers execution like collect/reduce).
> 
> Key characteristics: streams are **lazy** (nothing executes until terminal operation), **single-use** (can't reuse after consumption), and should be **stateless** (avoid side effects)."

### Demonstrating Laziness

```java
// Nothing prints until count() is called
long c = employees.stream()
    .filter(e -> { 
        System.out.println("Filtering: " + e.name()); 
        return e.salary() >= 6000; 
    })
    .peek(e -> System.out.println("Passed filter: " + e.name()))
    .count();

// Output (only when count() executes):
// Filtering: Umar
// Passed filter: Umar
// Filtering: Ali
// Filtering: Sara
// Passed filter: Sara
// ... etc
```

### Short-Circuit Demonstration

```java
// Processes only until first match is found
Optional<Employee> first = employees.stream()
    .filter(e -> {
        System.out.println("Checking: " + e.name());
        return e.salary() > 10000;
    })
    .findFirst();

// Output:
// Checking: Umar
// Checking: Ali
// Checking: Sara
// Checking: Hina
// Checking: Zain
// Checking: Noor
// Checking: Ahmed  <-- Stops here! Found Ahmed with 12000 salary
```

---

## 2) Daily Coding Patterns (Real Service Scenarios)

### Pattern A: Filter + Map (DTO Field Extraction)

**Use case:** Building API responses, extracting specific data

```java
// Get names of high earners for dashboard
List<String> highEarnerNames = employees.stream()
    .filter(e -> e.salary() >= 6000)
    .map(Employee::name)
    .toList();
// Result: ["Umar", "Sara", "Noor", "Ahmed", "Fatima"]

// Get emails for marketing (assuming email pattern)
List<String> marketingEmails = employees.stream()
    .filter(e -> e.age() >= 25 && e.age() <= 35)
    .map(e -> e.name().toLowerCase() + "@company.com")
    .toList();
// Result: ["umar@company.com", "sara@company.com", ...]

// Convert to DTOs
record EmployeeDTO(String name, String department) {}

List<EmployeeDTO> dtos = employees.stream()
    .filter(e -> e.department().equals("Backend"))
    .map(e -> new EmployeeDTO(e.name(), e.department()))
    .toList();
```

### Pattern B: Sort + Limit (Top N / Leaderboards)

**Use case:** Displaying rankings, top performers

```java
// Top 3 highest paid employees
List<Employee> top3Salaries = employees.stream()
    .sorted(Comparator.comparing(Employee::salary).reversed())
    .limit(3)
    .toList();
// Result: [Ahmed(12000), Sara(9000), Fatima(8500)]

// Most experienced (earliest hire date) 
List<Employee> mostExperienced = employees.stream()
    .sorted(Comparator.comparing(Employee::hireDate))
    .limit(3)
    .toList();
// Result: [Ahmed(2017), Fatima(2018), Sara(2019)]

// Top earners per department (more complex)
Map<String, Optional<Employee>> topByDept = employees.stream()
    .collect(groupingBy(
        Employee::department,
        maxBy(Comparator.comparing(Employee::salary))
    ));
// Result: {Backend=Ahmed(12000), QA=Noor(6500), Support=Hina(5000), Frontend=Fatima(8500)}
```

### Pattern C: Group & Count (Analytics/Reporting)

**Use case:** Dashboard metrics, business intelligence

```java
// Count employees per department (like SQL GROUP BY + COUNT)
Map<String, Long> countByDept = employees.stream()
    .collect(groupingBy(Employee::department, counting()));
// Result: {Backend=4, QA=2, Support=1, Frontend=1}

// Count by age group
Map<String, Long> countByAgeGroup = employees.stream()
    .collect(groupingBy(
        e -> {
            if (e.age() < 25) return "Junior (< 25)";
            else if (e.age() < 30) return "Mid (25-29)";
            else return "Senior (30+)";
        },
        counting()
    ));
// Result: {Junior=2, Mid=2, Senior=4}

// Imperative equivalent (for understanding)
Map<String, Long> imperativeCount = new HashMap<>();
for (Employee e : employees) {
    String dept = e.department();
    imperativeCount.put(dept, imperativeCount.getOrDefault(dept, 0L) + 1);
}
```

### Pattern D: Group & Aggregate (Sums, Averages)

**Use case:** Financial reports, salary analysis

```java
// Average salary by department
Map<String, Double> avgSalaryByDept = employees.stream()
    .collect(groupingBy(
        Employee::department,
        averagingDouble(Employee::salary)
    ));
// Result: {Backend=7875.0, QA=4750.0, Support=5000.0, Frontend=8500.0}

// Total payroll by department
Map<String, Double> totalByDept = employees.stream()
    .collect(groupingBy(
        Employee::department,
        summingDouble(Employee::salary)
    ));
// Result: {Backend=31500.0, QA=9500.0, Support=5000.0, Frontend=8500.0}

// Complete statistics by department
Map<String, DoubleSummaryStatistics> statsByDept = employees.stream()
    .collect(groupingBy(
        Employee::department,
        summarizingDouble(Employee::salary)
    ));
// Usage: statsByDept.get("Backend").getAverage(), .getMax(), .getMin(), .getSum(), .getCount()
```

### Pattern E: Group by Department (Full List per Group)

```java
// Group employees by department
Map<String, List<Employee>> byDept = employees.stream()
    .collect(groupingBy(Employee::department));
// Result: {Backend=[Umar, Sara, Zain, Ahmed], QA=[Ali, Noor], ...}

// Group names (not full objects) by department
Map<String, List<String>> namesByDept = employees.stream()
    .collect(groupingBy(
        Employee::department,
        mapping(Employee::name, toList())
    ));
// Result: {Backend=["Umar", "Sara", "Zain", "Ahmed"], QA=["Ali", "Noor"], ...}

// Sorted within groups
Map<String, List<Employee>> byDeptSorted = employees.stream()
    .sorted(Comparator.comparing(Employee::salary).reversed())
    .collect(groupingBy(Employee::department, toList()));
```

### Pattern F: Partition (Boolean Split)

**Use case:** Eligible vs not eligible, pass vs fail

```java
// Partition by salary threshold
Map<Boolean, List<Employee>> eligibleForBonus = employees.stream()
    .collect(partitioningBy(e -> e.salary() >= 6000));

List<Employee> eligible = eligibleForBonus.get(true);    // [Umar, Sara, Noor, Ahmed, Fatima]
List<Employee> notEligible = eligibleForBonus.get(false); // [Ali, Hina, Zain]

// Partition with counting
Map<Boolean, Long> eligibilityCount = employees.stream()
    .collect(partitioningBy(
        e -> e.salary() >= 6000,
        counting()
    ));
// Result: {true=5, false=3}

// Partition with average salary
Map<Boolean, Double> avgByEligibility = employees.stream()
    .collect(partitioningBy(
        e -> e.salary() >= 6000,
        averagingDouble(Employee::salary)
    ));
// Result: {true=8600.0, false=3833.33}
```

### Pattern G: Sum / Average (Numeric Aggregations)

```java
// Total payroll
double totalPayroll = employees.stream()
    .mapToDouble(Employee::salary)
    .sum();
// Result: 54500.0

// Average age
double avgAge = employees.stream()
    .mapToInt(Employee::age)
    .average()
    .orElse(0);
// Result: 28.875

// Using Collectors
double avgSalary = employees.stream()
    .collect(averagingDouble(Employee::salary));
// Result: 6812.5

// Summary statistics (all at once)
DoubleSummaryStatistics stats = employees.stream()
    .mapToDouble(Employee::salary)
    .summaryStatistics();
System.out.println("Count: " + stats.getCount());    // 8
System.out.println("Sum: " + stats.getSum());        // 54500.0
System.out.println("Min: " + stats.getMin());        // 3000.0
System.out.println("Max: " + stats.getMax());        // 12000.0
System.out.println("Avg: " + stats.getAverage());    // 6812.5
```

### Pattern H: Lookup Map (Quick Access by Key)

**Use case:** Avoiding O(n) searches, building indexes

```java
// Employee by ID (for quick lookup)
Map<Long, Employee> employeeById = employees.stream()
    .collect(toMap(Employee::id, Function.identity()));
// Usage: Employee e = employeeById.get(3L);  // Sara

// Salary by name
Map<String, Double> salaryByName = employees.stream()
    .collect(toMap(Employee::name, Employee::salary));
// Result: {Umar=7000.0, Ali=3000.0, Sara=9000.0, ...}

// Handle potential duplicate keys (keep higher salary)
Map<String, Double> salaryMap = employees.stream()
    .collect(toMap(
        Employee::name,
        Employee::salary,
        (existing, replacement) -> Math.max(existing, replacement)
    ));

// Employee by department (single employee, keep first)
Map<String, Employee> firstByDept = employees.stream()
    .collect(toMap(
        Employee::department,
        Function.identity(),
        (first, second) -> first  // Keep first occurrence
    ));
```

---

## 3) Interview Must-Knows (With Employee Examples)

### A) map vs flatMap — The Favorite Question

**Problem:** Get all unique skills across all employees

```java
// Each employee has List<String> skills
// flatMap flattens List<String> from each employee into one stream

Set<String> allSkills = employees.stream()
    .flatMap(e -> e.skills().stream())  // Stream<String> from each employee
    .collect(toSet());
// Result: {"Java", "Spring", "SQL", "Selenium", "Python", "Kubernetes", "AWS", ...}

// WITHOUT flatMap - you get Stream<List<String>> (nested!)
Stream<List<String>> nested = employees.stream()
    .map(Employee::skills);  // Still nested lists!

// Count skill frequency
Map<String, Long> skillFrequency = employees.stream()
    .flatMap(e -> e.skills().stream())
    .collect(groupingBy(Function.identity(), counting()));
// Result: {Java=5, Selenium=2, React=2, ...}
```

**Another flatMap example:** Departments with employee names

```java
Map<String, List<Employee>> byDept = employees.stream()
    .collect(groupingBy(Employee::department));

// Get all names from all departments (flattening)
List<String> allNames = byDept.values().stream()  // Stream<List<Employee>>
    .flatMap(List::stream)                         // Stream<Employee>
    .map(Employee::name)                           // Stream<String>
    .toList();
// Result: ["Umar", "Sara", "Zain", "Ahmed", "Ali", "Noor", "Hina", "Fatima"]
```

### B) reduce vs collect

**reduce** — Fold values into single result:
```java
// Total salary using reduce
double totalSalary = employees.stream()
    .map(Employee::salary)
    .reduce(0.0, Double::sum);
// Calculation: 0 + 7000 + 3000 + 9000 + 5000 + 3500 + 6500 + 12000 + 8500

// Find employee with highest salary using reduce
Optional<Employee> highestPaid = employees.stream()
    .reduce((e1, e2) -> e1.salary() > e2.salary() ? e1 : e2);
// Result: Optional[Ahmed]

// Concatenate all names
String allNames = employees.stream()
    .map(Employee::name)
    .reduce("", (a, b) -> a.isEmpty() ? b : a + ", " + b);
// Result: "Umar, Ali, Sara, Hina, Zain, Noor, Ahmed, Fatima"
```

**collect** — Build complex structures:
```java
// Collect to Map
Map<Long, Employee> byId = employees.stream()
    .collect(toMap(Employee::id, Function.identity()));

// Collect with grouping
Map<String, List<Employee>> byDept = employees.stream()
    .collect(groupingBy(Employee::department));
```

### C) groupingBy vs partitioningBy

```java
// groupingBy: Many keys based on classifier
Map<String, List<Employee>> byDept = employees.stream()
    .collect(groupingBy(Employee::department));
// Keys: "Backend", "QA", "Support", "Frontend"

// partitioningBy: Exactly 2 keys (true/false)
Map<Boolean, List<Employee>> byEligibility = employees.stream()
    .collect(partitioningBy(e -> e.salary() >= 6000));
// Keys: true, false (always both present, even if empty)
```

### D) Why counting() Returns Long?

```java
// Long can hold values up to 9,223,372,036,854,775,807
// Integer max is only 2,147,483,647
// For big data scenarios, Long prevents overflow

Map<String, Long> countByDept = employees.stream()
    .collect(groupingBy(Employee::department, counting()));

// In production, you might have millions of records
// Long ensures you don't overflow during counting
```

### E) Side Effects Trap

```java
// ❌ BAD: Mutating external state
List<String> collected = new ArrayList<>();
employees.stream()
    .map(Employee::name)
    .forEach(collected::add);  // Side effect! Modifying external list

// ✅ GOOD: Proper collection
List<String> collected = employees.stream()
    .map(Employee::name)
    .toList();  // No side effects

// ❌ DANGEROUS: Parallel with side effects
List<String> corrupted = Collections.synchronizedList(new ArrayList<>());
employees.parallelStream()
    .map(Employee::name)
    .forEach(corrupted::add);  // Race condition even with synchronized!

// ✅ SAFE: Collect properly
List<String> safe = employees.parallelStream()
    .map(Employee::name)
    .toList();  // Thread-safe collection
```

### F) Parallel Stream Traps

```java
// ❌ AVOID: Database calls in parallel
employees.parallelStream()
    .forEach(e -> database.updateSalary(e.id(), e.salary() * 1.1));
// Problems: Connection pool exhaustion, transaction issues

// ❌ AVOID: Sequential dependencies
employees.parallelStream()
    .limit(5)  // Expensive in parallel - needs coordination
    .toList();

// ✅ OK: CPU-intensive, independent, pure operations
List<Employee> processed = employees.parallelStream()
    .map(e -> new Employee(e.id(), e.name().toUpperCase(), 
                          e.age(), e.salary() * 1.1, 
                          e.department(), e.hireDate(), e.skills()))
    .toList();

// Rule of thumb: Use parallel for:
// - Large datasets (1000+ elements)
// - CPU-intensive operations
// - No shared mutable state
// - No I/O operations
```

---

## 4) Advanced Patterns

### Multi-level Grouping

```java
// Group by department, then by age
Map<String, Map<Integer, List<Employee>>> byDeptThenAge = employees.stream()
    .collect(groupingBy(
        Employee::department,
        groupingBy(Employee::age)
    ));
// Result: {Backend={30=[Umar], 28=[Sara], 24=[Zain], 35=[Ahmed]}, ...}

// Group by department, then count by age
Map<String, Map<Integer, Long>> countByDeptAndAge = employees.stream()
    .collect(groupingBy(
        Employee::department,
        groupingBy(Employee::age, counting())
    ));
```

### Chained Comparators

```java
// Sort by department, then by salary (descending), then by name
List<Employee> sorted = employees.stream()
    .sorted(Comparator
        .comparing(Employee::department)
        .thenComparing(Employee::salary, Comparator.reverseOrder())
        .thenComparing(Employee::name))
    .toList();
```

### Collecting with Custom Finisher

```java
// Collect to comma-separated string of names
String names = employees.stream()
    .map(Employee::name)
    .collect(joining(", ", "Employees: [", "]"));
// Result: "Employees: [Umar, Ali, Sara, Hina, Zain, Noor, Ahmed, Fatima]"

// Collect to unmodifiable list
List<String> immutableNames = employees.stream()
    .map(Employee::name)
    .collect(collectingAndThen(toList(), Collections::unmodifiableList));
```

### Finding Employees by Skill

```java
// Find employees with a specific skill
List<Employee> javaDevs = employees.stream()
    .filter(e -> e.skills().contains("Java"))
    .toList();
// Result: [Umar, Sara, Zain, Noor, Ahmed]

// Find employees with all specified skills
Set<String> requiredSkills = Set.of("Java", "AWS");
List<Employee> cloudDevs = employees.stream()
    .filter(e -> e.skills().containsAll(requiredSkills))
    .toList();
// Result: [Sara]

// Find employees with any of the specified skills
Set<String> anySkills = Set.of("React", "TypeScript");
List<Employee> frontendCapable = employees.stream()
    .filter(e -> e.skills().stream().anyMatch(anySkills::contains))
    .toList();
// Result: [Zain, Fatima]
```

---

## 5) Rapid-Fire Interview Questions

### Q1: "Difference between intermediate and terminal operations?"
> **A:** Intermediate ops are lazy, return Stream, and can be chained (map, filter, sorted). Terminal ops trigger execution and produce result or side effect (collect, forEach, reduce).

### Q2: "Why are streams lazy?"
> **A:** Efficiency. Allows short-circuiting (findFirst stops early), fusion (multiple ops in single pass), and processes only elements needed for result.

### Q3: "When would you use flatMap?"
> **A:** When each element maps to multiple elements that need flattening: nested collections, splitting strings, Optional chaining.

### Q4: "reduce vs collect?"
> **A:** reduce folds into single immutable value; collect builds mutable structures (List, Map). Collect is more efficient for containers due to mutable accumulation.

### Q5: "When would you avoid parallelStream?"
> **A:** I/O operations, small datasets, ordered operations (limit), shared mutable state, sequential dependencies.

### Q6: "How do you handle duplicate keys in toMap?"
> **A:** Provide merge function as third parameter: `toMap(keyFn, valueFn, (old, new) -> old)`

### Q7: "What's the difference between findFirst and findAny?"
> **A:** findFirst returns first in encounter order (deterministic). findAny returns any element (faster in parallel, non-deterministic).

---

## 📊 Quick Reference Table

| Pattern | Code |
|---------|------|
| Filter + Map | `stream.filter(pred).map(fn).toList()` |
| Top N | `stream.sorted(cmp.reversed()).limit(n)` |
| Group + Count | `groupingBy(fn, counting())` |
| Group + Avg | `groupingBy(fn, averagingDouble(fn))` |
| Partition | `partitioningBy(pred)` |
| Lookup Map | `toMap(keyFn, valueFn)` |
| Flatten | `flatMap(x -> x.stream())` |
| Join | `joining(", ")` |
| Stats | `summarizingDouble(fn)` |
| Max by field | `max(comparing(fn))` |

---

*Last updated: December 2024*
