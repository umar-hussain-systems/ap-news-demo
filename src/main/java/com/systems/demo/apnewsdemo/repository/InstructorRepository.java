package com.systems.demo.apnewsdemo.repository;

import com.systems.demo.apnewsdemo.model.Instructor;
import com.systems.demo.apnewsdemo.model.Student;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** The interface Instructor repository. */
@Repository
public interface InstructorRepository extends JpaRepository<Instructor, Integer> {

  /**
   * Find all students of an instructor.
   * Finds students who are enrolled in courses taught by the specified instructor.
   *
   * @param instructorId the instructor id
   * @return the list of students
   */
  @Query(
      "SELECT DISTINCT s FROM Student s "
          + "INNER JOIN StudentCourse sc ON s.id = sc.student.id "
          + "INNER JOIN Course c ON sc.course.id = c.id "
          + "WHERE c.instructor.id = :instructorId")
  List<Student> findAllStudentsByInstructorId(@Param("instructorId") Integer instructorId);
}


