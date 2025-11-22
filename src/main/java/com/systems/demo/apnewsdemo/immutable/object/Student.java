package com.systems.demo.apnewsdemo.immutable.object;

public final class Student {
  private final String name;
  private final int age;

  public Student(String name, int age) {
    this.name = name;
    this.age = age;
  }

  public String getName() { return name; }
  public int getAge() { return age; }

  // No setters
}
