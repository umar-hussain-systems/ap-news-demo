package com.systems.demo.apnewsdemo.streams;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class StreamReduceExamples {


  public static void streamReduceExamples() {
    List<Integer> numbers = List.of(1, 2, 3, 4, 5);

// Sum with identity value
    int sumOfNumbers = numbers.stream().reduce(0, Integer::sum);
    System.out.println("Sum: " + sumOfNumbers);
// Calculation: 0 + 1 = 1, 1 + 2 = 3, 3 + 3 = 6, 6 + 4 = 10, 10 + 5 = 15
// Result: 15

// Product
    int product = numbers.stream().reduce(1, (a, b) -> a * b);
    System.out.println("Product = " + product);
// Result: 120

// Without identity (returns Optional)
    Optional<Integer> max = numbers.stream().reduce(Integer::max);
    System.out.println("Max: " + max.orElse(-1));
// Result: Optional[5]

// String concatenation
    List<String> words = List.of("a", "b", "c");
    String joined = words.stream().reduce("", String::concat);
    System.out.println("Joined: " + joined);
// Result: "abc"

  //complex examples

    // Find longest string
    List<String> fruits = List.of("apple", "pie", "banana", "kiwi");
    Optional<String> longest = fruits.stream()
        .reduce((a, b) -> a.length() > b.length() ? a : b);
    System.out.println("Longest: " + longest.orElse(""));
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
    System.out.println("Total Salary: " + totalSalary);
// Result: 180000.0

  }

  public static void main(String[] args) {

  }







}
