
INSERT INTO `sports_database`.`player` (`id`,`age`,`level`, `email`, `gender`,`created_at`) VALUES (1,'15','10', 'ravi@test.com', 'MALE',now());
INSERT INTO `sports_database`.`player` (`id`,`age`,`level`, `email`, `gender`,`created_at`) VALUES (2,'25','6', 'umar@test.com', 'MALE',now());
INSERT INTO `sports_database`.`player` (`id`,`age`,`level`, `email`, `gender`,`created_at`) VALUES (3,'30','5', 'john@test.com', 'MALE',now());
INSERT INTO `sports_database`.`player` (`id`,`age`,`level`, `email`, `gender`,`created_at`) VALUES (4,'20','5', 'fatima@test.com', 'FEMALE',now());
INSERT INTO `sports_database`.`player` (`id`,`age`,`level`, `email`, `gender`,`created_at`) VALUES (5,'15','9', 'alex@test.com', 'FEMALE',now());
INSERT INTO `sports_database`.`player` (`id`,`age`,`level`, `email`, `gender`,`created_at`) VALUES (6,'17','8', 'jane@test.com', 'FEMALE',now());


INSERT INTO `sports_database`.`sport` (`id`,`name`,`created_at`) VALUES (1,'Cricket',now());
INSERT INTO `sports_database`.`sport` (`id`,`name`,`created_at`) VALUES (2,'Hockey',now());
INSERT INTO `sports_database`.`sport` (`id`,`name`,`created_at`) VALUES (3,'Football',now());
INSERT INTO `sports_database`.`sport` (`id`,`name`,`created_at`) VALUES (4,'Badminton',now());

INSERT INTO `sports_database`.`player_sport` (`player_id`,`sport_id`,`created_at`) VALUES (1,1,now());
INSERT INTO `sports_database`.`player_sport` (`player_id`,`sport_id`,`created_at`) VALUES (2,1,now());
INSERT INTO `sports_database`.`player_sport` (`player_id`,`sport_id`,`created_at`) VALUES (1,2,now());
INSERT INTO `sports_database`.`player_sport` (`player_id`,`sport_id`,`created_at`) VALUES (3,3,now());
INSERT INTO `sports_database`.`player_sport` (`player_id`,`sport_id`,`created_at`) VALUES (4,1,now());

-- Insert data for instructor table
INSERT INTO `sports_database`.`instructor` (`id`,`name`,`email`,`department`,`specialization`,`created_at`) VALUES (1,'Dr. Sarah Johnson','sarah.johnson@university.edu','Computer Science','Software Engineering',now());
INSERT INTO `sports_database`.`instructor` (`id`,`name`,`email`,`department`,`specialization`,`created_at`) VALUES (2,'Prof. Michael Chen','michael.chen@university.edu','Mathematics','Applied Mathematics',now());
INSERT INTO `sports_database`.`instructor` (`id`,`name`,`email`,`department`,`specialization`,`created_at`) VALUES (3,'Dr. Emily Rodriguez','emily.rodriguez@university.edu','Physics','Quantum Physics',now());
INSERT INTO `sports_database`.`instructor` (`id`,`name`,`email`,`department`,`specialization`,`created_at`) VALUES (4,'Prof. David Kim','david.kim@university.edu','Computer Science','Data Science',now());

-- Insert data for student table
INSERT INTO `sports_database`.`student` (`id`,`name`,`gender`,`age`,`email`,`created_at`) VALUES (1,'Alice Williams','FEMALE',20,'alice.williams@student.edu',now());
INSERT INTO `sports_database`.`student` (`id`,`name`,`gender`,`age`,`email`,`created_at`) VALUES (2,'Bob Anderson','MALE',22,'bob.anderson@student.edu',now());
INSERT INTO `sports_database`.`student` (`id`,`name`,`gender`,`age`,`email`,`created_at`) VALUES (3,'Carol Martinez','FEMALE',21,'carol.martinez@student.edu',now());
INSERT INTO `sports_database`.`student` (`id`,`name`,`gender`,`age`,`email`,`created_at`) VALUES (4,'Daniel Brown','MALE',23,'daniel.brown@student.edu',now());
INSERT INTO `sports_database`.`student` (`id`,`name`,`gender`,`age`,`email`,`created_at`) VALUES (5,'Eva Garcia','FEMALE',19,'eva.garcia@student.edu',now());
INSERT INTO `sports_database`.`student` (`id`,`name`,`gender`,`age`,`email`,`created_at`) VALUES (6,'Frank Taylor','MALE',24,'frank.taylor@student.edu',now());

-- Insert data for course table
INSERT INTO `sports_database`.`course` (`id`,`name`,`description`,`instructor_id`,`created_at`) VALUES (1,'Introduction to Java Programming','Learn the fundamentals of Java programming language and object-oriented programming concepts',1,now());
INSERT INTO `sports_database`.`course` (`id`,`name`,`description`,`instructor_id`,`created_at`) VALUES (2,'Advanced Algorithms','Study of complex algorithms, data structures, and algorithm analysis techniques',1,now());
INSERT INTO `sports_database`.`course` (`id`,`name`,`description`,`instructor_id`,`created_at`) VALUES (3,'Linear Algebra','Mathematical foundations of linear algebra including vectors, matrices, and transformations',2,now());
INSERT INTO `sports_database`.`course` (`id`,`name`,`description`,`instructor_id`,`created_at`) VALUES (4,'Quantum Mechanics','Introduction to quantum physics principles and applications',3,now());
INSERT INTO `sports_database`.`course` (`id`,`name`,`description`,`instructor_id`,`created_at`) VALUES (5,'Machine Learning Fundamentals','Introduction to machine learning algorithms and data science techniques',4,now());
INSERT INTO `sports_database`.`course` (`id`,`name`,`description`,`instructor_id`,`created_at`) VALUES (6,'Database Systems','Design and implementation of database systems and SQL',1,now());

-- Insert data for student_course table (enrollments)
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (1,1,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (1,2,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (2,1,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (2,3,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (2,5,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (3,1,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (3,4,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (3,5,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (4,2,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (4,6,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (5,1,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (5,3,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (6,2,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (6,5,now());
INSERT INTO `sports_database`.`student_course` (`student_id`,`course_id`,`created_at`) VALUES (6,6,now());