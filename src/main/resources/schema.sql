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
