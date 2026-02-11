package com.systems.demo.apnewsdemo.streams;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StreamFilterExamples {

  public static void filterExample() {
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


    // 1) Filter engineers aged >= 30, then sort by salary descending, then collect names
    List<String> seniorEngineersBySalaryDesc = employees.stream()
        .filter(e -> "Engineering".equals(e.department()) && e.age() >= 30)
        .sorted(Comparator.comparingDouble(Employee::salary).reversed())
        .map(Employee::name)
        .toList();

    // 2) Filter high earners, then sort by department asc, then age asc, collect Employee objects
    List<Employee> highEarnersSortedByDeptThenAge = employees.stream()
        .filter(e -> e.salary() > 60000)
        .sorted(Comparator.comparing(Employee::department)
            .thenComparingInt(Employee::age))
        .toList();

    System.out.println(seniorEngineersBySalaryDesc);         // e.g. [Carol, Eve, Alice]
    System.out.println(highEarnersSortedByDeptThenAge);      // sorted Employee list

// Chaining multiple operations
    Map<String, Double> avgSalaryByDept = employees.stream()
        .collect(Collectors.groupingBy(
            Employee::department,
            Collectors.averagingDouble(Employee::salary)
        ));
// Result: {Engineering=84000.0, Marketing=57500.0}
  }
  public static void main(String[] args) {
    filterExample();
  }
}
