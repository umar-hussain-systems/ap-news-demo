package com.systems.demo.apnewsdemo.streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StreamCollectorExamples {

  AtomicInteger atomicInteger = new AtomicInteger();
  public void increment(){
    atomicInteger.incrementAndGet();
  }

  public static void groupByExample() {




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

    Map<String, Optional<Employee>> byDeptMaxSalary = employees.stream()
        .collect(Collectors.groupingBy(Employee::dept, Collectors
            .reducing((employee, employee2) -> {
              if (employee.salary > employee2.salary) {
                return employee;
              }else{
                return employee2;
              }
            }
            )));

    Map<Boolean,Map<String,List<Employee>>> empPartionBySalaryThenGroupByDept = employees
        .stream()
        .collect(Collectors.partitioningBy(emp->emp.salary>80000, Collectors.groupingBy(emp->emp.dept)));



    //group by department but in Set
    Map<String, Set<Employee>> byDeptSet = employees.stream()
        .collect(Collectors.groupingBy(Employee::dept, Collectors.toSet()));

// Group by name into a Set (the answer to your question)
    Map<String, Set<Employee>> byName = employees.stream()
        .collect(Collectors.groupingBy(Employee::name, Collectors.toSet()));

    // Group by name, then by age, collecting employees into Sets
    Map<String, Map<Integer, Set<Employee>>> byNameAndAgeSet = employees.stream()
        .collect(Collectors.groupingBy(
            Employee::name,
            Collectors.groupingBy(Employee::age, Collectors.toSet())
        ));

    Map<String, Map<Integer, Set<Employee>>> byNameAndAgeOrderedSet =
        employees.stream()
            .collect(Collectors.groupingBy(
                Employee::name,
                Collectors.groupingBy(
                    Employee::age,
                    Collectors.toCollection(java.util.LinkedHashSet::new)
                )
            ));


// Result: {Engineering=[Alice, Carol], Marketing=[Bob, David]}
    System.out.println(byDept);
    System.out.println(byDeptSet);
    System.out.println(byName);
    System.out.println(byNameAndAgeSet);
    // print the ordered-set variant as well so it's not unused
    System.out.println(byNameAndAgeOrderedSet);
// Group by department, count employees
    Map<String, Long> countByDept = employees.stream()
        .collect(Collectors.groupingBy(Employee::dept, Collectors.counting()));
// Result: {Engineering=2, Marketing=2}
    System.out.println(countByDept);
// Group by department, average salary
    Map<String, Double> avgSalaryByDept = employees.stream()
        .collect(Collectors.groupingBy(
            Employee::dept,
            Collectors.averagingDouble(Employee::salary)
        ));

    System.out.println(avgSalaryByDept);

    Map<String,List<Employee>> simpleGroupingBy = employees.stream().collect(Collectors.groupingBy(Employee::name));
    Map<String,List<Employee>> byDeptNameListOfEmployeeOrderByNameDesc = employees
        .stream()
        .collect(Collectors.groupingBy(Employee::dept,
            Collectors
                .collectingAndThen(Collectors.toList(), list -> list
                    .stream()
                    .sorted(Comparator.comparing(Employee::name)
                        .reversed())
                    .toList())));

    Map<String,List<String>> byDeptNameListOfNameOrderByDesc = employees.stream().collect(Collectors.groupingBy(Employee::dept, Collectors.mapping(
        Employee::name,Collectors.collectingAndThen(Collectors.toList(),
            list -> {
          list.sort(Comparator.reverseOrder());
          return list;
        }
        ))));

// Result: {Engineering=70000.0, Marketing=50000.0}

// Multi-level grouping
    // produce an outer LinkedHashMap to preserve insertion order of departments

    // groupingBy has classifier,mapfactory,DownStream)

    // groupBy


    Map<String, Map<Integer, List<Employee>>> byDeptThenAge = employees.stream()
        .collect(Collectors.groupingBy(
            Employee::dept,
            LinkedHashMap::new,                          // use LinkedHashMap for the outer map
            Collectors.groupingBy(Employee::age,LinkedHashMap::new,Collectors.toList())        // inner grouping produces Map<Integer, List<Employee>>
        ));

    //Mutli-level grouping with mapping list in the end
    Map<String,Map<Integer,List<String>>> byDeptThenAgeEmpName =
        employees
            .stream()
            .collect(Collectors.groupingBy(
        Employee::dept,
                Collectors.groupingBy(Employee::age,
                    Collectors.mapping(Employee::name,Collectors.toList()))));

    // print the multi-level grouped result (outer map preserves insertion order)
    System.out.println(byDeptThenAge);

  }

  public static void main(String[] args) {
    groupByExample();
  }

}
