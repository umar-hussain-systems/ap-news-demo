package com.systems.demo.apnewsdemo.repository;

import com.systems.demo.apnewsdemo.model.Student;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/** The interface Student repository. */
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

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

  /**
   * Find top 5 students by name using pagination.
   * Orders students by name in ascending order.
   *
   * @param pageable the pageable object with page size of 5
   * @return the page of students
   */
  @Query("SELECT s FROM Student s ORDER BY s.name ASC")
  Page<Student> findTop5StudentsByName(Pageable pageable);

  /**
   * Fetch student details with courses using JOIN FETCH.
   * This method eagerly loads the courses relationship to avoid N+1 query problem.
   *
   * @param studentId the student id
   * @return the student with courses loaded
   */
  @Query(
      "SELECT DISTINCT s FROM Student s "
          + "LEFT JOIN FETCH s.courses sc "
          + "LEFT JOIN FETCH sc.course "
          + "WHERE s.id = :studentId")
  Student findStudentWithCoursesById(@Param("studentId") Integer studentId);

  /**
   * Fetch all students with their courses using JOIN FETCH.
   * This method eagerly loads the courses relationship for all students.
   *
   * @return the list of students with courses loaded
   */
  @Query(
      "SELECT DISTINCT s FROM Student s "
          + "LEFT JOIN FETCH s.courses sc "
          + "LEFT JOIN FETCH sc.course")
  List<Student> findAllStudentsWithCourses();
}


