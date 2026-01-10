package com.systems.demo.apnewsdemo.streams;

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
