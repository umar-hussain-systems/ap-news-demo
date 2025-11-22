CREATE SCHEMA IF NOT EXISTS "sports_database";

USE "sports_database";

-- Table structure for table `player`
CREATE TABLE "player" (
  "age" INT,
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "level" INT,
  "created_at" TIMESTAMP,
  "email" VARCHAR(255),
  "gender" ENUM('MALE', 'FEMALE')
);

-- Table structure for table `sport`
CREATE TABLE "sport" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "created_at" TIMESTAMP,
  "name" VARCHAR(255)
);

-- Table structure for table `player_sport`
CREATE TABLE "player_sport" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "player_id" INT,
  "sport_id" INT,
  "created_at" TIMESTAMP,
  FOREIGN KEY ("player_id") REFERENCES "player"("id"),
  FOREIGN KEY ("sport_id") REFERENCES "sport"("id")
);

-- Table structure for table `instructor`
CREATE TABLE "instructor" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "name" VARCHAR(255),
  "email" VARCHAR(255),
  "department" VARCHAR(255),
  "specialization" VARCHAR(255),
  "created_at" TIMESTAMP
);

-- Table structure for table `student`
CREATE TABLE "student" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "name" VARCHAR(255),
  "gender" ENUM('MALE', 'FEMALE'),
  "age" INT,
  "email" VARCHAR(255),
  "created_at" TIMESTAMP
);

-- Table structure for table `course`
CREATE TABLE "course" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "name" VARCHAR(255),
  "description" VARCHAR(500),
  "instructor_id" INT,
  "created_at" TIMESTAMP,
  FOREIGN KEY ("instructor_id") REFERENCES "instructor"("id")
);

-- Table structure for table `student_course`
CREATE TABLE "student_course" (
  "id" INT AUTO_INCREMENT PRIMARY KEY,
  "student_id" INT,
  "course_id" INT,
  "created_at" TIMESTAMP,
  FOREIGN KEY ("student_id") REFERENCES "student"("id"),
  FOREIGN KEY ("course_id") REFERENCES "course"("id")
);
