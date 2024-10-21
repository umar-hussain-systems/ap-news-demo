
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