package com.systems.demo.apnewsdemo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/** The type Instructor. */
@Entity
@Table(name = "instructor")
@Getter
@Setter
public class Instructor extends BaseEntity {
  private String name;
  private String email;
  private String department;
  private String specialization;

  @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Course> courses;
}

