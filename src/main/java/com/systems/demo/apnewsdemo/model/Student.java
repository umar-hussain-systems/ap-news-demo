package com.systems.demo.apnewsdemo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student")
@Getter
@Setter
public class Student extends BaseEntity{
  private String name;
  @Enumerated(EnumType.STRING)
  private Gender gender;
  private int age;
  private String email;

  @OneToMany(mappedBy = "student", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<StudentCourse> courses;
}
