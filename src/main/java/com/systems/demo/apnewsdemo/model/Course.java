package com.systems.demo.apnewsdemo.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "course")
@Setter
@Getter
public class Course extends BaseEntity {

  private String name;
  private String description;

  @ManyToOne
  @JoinColumn(name = "instructor_id", nullable = true)
  private Instructor instructor;

  @OneToMany(mappedBy = "course", orphanRemoval = true, cascade = CascadeType.ALL)
  private Set<StudentCourse> students;
}
