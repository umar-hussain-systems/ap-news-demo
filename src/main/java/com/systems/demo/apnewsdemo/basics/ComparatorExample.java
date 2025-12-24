package com.systems.demo.apnewsdemo.basics;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
class Person{

  private Long id;
  private String name;
  private Double salary;
  private Long age;

}

public class ComparatorExample {

  Comparator<Person> personComparator = new Comparator<Person>() {
    @Override
    public int compare(Person p1, Person p2) {
      int cmp = p1.getAge().compareTo(p2.getAge());
      if (cmp != 0) return cmp;

      cmp = p1.getSalary().compareTo(p2.getSalary());
      if (cmp != 0) return cmp;

      return p1.getName().compareTo(p2.getName());
    }
  };


  Comparator<Person> comparator = Comparator
      .comparingLong(Person::getId)
      .thenComparing(Person::getName)
      .thenComparing(Person::getSalary,Comparator.reverseOrder());


}
