package com.systems.demo.apnewsdemo.immutable.object;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public final class Employee {
  private final String name;
  private final Long salary;
  private final List<String> skills;

  Employee(String name, Long salary, List<String> skills) {
    this.name = name;
    this.salary = salary;
    this.skills = skills;
  }
}

class Driver{
  public static void main(String[] args) {
    List<Employee> employees = new ArrayList<>();
    employees.add(new Employee("umar",15000L,List.of("Java")));
    employees.add(new Employee("ali",12000L,List.of("Dotnet")));
    employees.add(new Employee("abubakr",20000L,List.of("Food Science")));

    List<Employee> employeeList = employees.stream().filter(employee -> employee.getSalary() > 12000L).toList();

 System.out.println(employeeList); }
}
