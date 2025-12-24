# AP News Demo - Project Analysis

## Executive Summary

This is a **Spring Boot 3.2.0** application built with **Java 21** that serves as a comprehensive demo project combining:
- A RESTful API for managing Players and Sports (many-to-many relationship)
- Algorithm implementations (sorting, searching)
- Data structure implementations (LRU Cache, Queue, Stack, Priority Queue)
- Problem-solving code samples
- Multi-threading examples
- Design patterns

## Project Structure

### Technology Stack
- **Framework**: Spring Boot 3.2.0
- **Java Version**: 21
- **Build Tool**: Maven
- **Database**: H2 (in-memory) and MySQL support
- **ORM**: Spring Data JPA / Hibernate
- **Additional Libraries**:
  - Lombok (code generation)
  - Guava 11.0
  - Apache Commons Lang3
  - Spring WebFlux (reactive programming)
  - Kotlin support (though primarily Java)

### Core Application Domain: Sports Management System

#### Entity Model
1. **Player** (`com.systems.demo.apnewsdemo.model.Player`)
   - Attributes: email, level, age, gender (enum)
   - One-to-Many relationship with SportPlayer (join table)

2. **Sport** (`com.systems.demo.apnewsdemo.model.Sport`)
   - Attributes: name
   - One-to-Many relationship with SportPlayer

3. **SportPlayer** (`com.systems.demo.apnewsdemo.model.SportPlayer`)
   - Join entity for many-to-many relationship
   - Links Player and Sport entities
   - Custom equals/hashCode implementation

4. **BaseEntity** (`com.systems.demo.apnewsdemo.model.BaseEntity`)
   - Base class with id (auto-generated) and createdAt timestamp
   - All entities extend this

#### API Endpoints

**Player Controller** (`/api/player`)
- `GET /api/player/no-enlistment` - Get players with no sports
- `POST /api/player` - Create a new player
- `PUT /api/player/{id}/update-sports` - Update player's sports
- `GET /api/player/players-by-category?category={name}&page={n}&size={m}` - Get paginated players by sport category

**Sports Controller** (`/api/sports`)
- `GET /api/sports/multiple-players/{playerCount}` - Get sports with >= N players
- `GET /api/sports/no-player-enlisted` - Get sports with no players
- `GET /api/sports/order-by-names?sportNames={names}` - Get sports by names (preserves order)
- `POST /api/sports` - Create a new sport
- `GET /api/sports/{Id}` - Get sport by ID
- `DELETE /api/sports/{id}` - Delete sport
- `POST /api/sports/upload/` - File upload endpoint (multipart/form-data)

#### Service Layer

**PlayerService** (`PlayerServiceImpl`)
- Business logic for player operations
- Transactional methods for data consistency
- Custom error handling with ServiceException
- Pagination support

**SportsService** (`SportsServiceImpl`)
- Business logic for sports operations
- Complex queries for filtering sports by player count
- Order preservation using LinkedHashSet

#### Repository Layer

**PlayerRepository**
- Custom JPQL queries:
  - `getPlayerHavingNoSports()` - Left join to find players without sports
  - `findAllBySportsName()` - Paginated query for players by sport name

**SportRepository**
- Custom JPQL queries:
  - `getSportsHavingPlayerGreaterThan()` - Group by with having clause
  - `getSportsHavingNoPlayers()` - Left join to find sports without players
  - `findSportsByNameIn()` - IN clause query

### Additional Components

#### 1. Algorithms (`com.systems.demo.apnewsdemo.algorithms`)
- **Sorting**:
  - Bubble Sort
  - Merge Sort
  - Quick Sort (standard and median-of-three pivot)
- **Searching**:
  - Binary Search

#### 2. Data Structures (`com.systems.demo.apnewsdemo.datastructures`)
- **Array-based**:
  - Stack
  - Priority Queue
- **Node-based**:
  - Queue (with `QueueNode`)
  - LRU Cache (node-based, now implemented as a key->value LRU)
- **Cache**:
  - CacheVal (for cache value storage)

Notes on LRU Cache (updated)
- The LRU cache was updated to a key->value implementation that uses the existing `Queue`/`QueueNode` without changing those classes' public shapes.
- New helper class: `KeyValue<K,V>` (stored inside `QueueNode.Data.value`).
- Map now indexes keys to `QueueNode<KeyValue<K,V>>` for O(1) lookup and eviction.
- Main files involved:
  - `src/main/java/com/systems/demo/apnewsdemo/datastructures/nodebased/KeyValue.java` (new)
  - `src/main/java/com/systems/demo/apnewsdemo/datastructures/nodebased/LRUCache.java` (converted to `LRUCache<K,V>`)
  - `src/main/java/com/systems/demo/apnewsdemo/datastructures/nodebased/Queue.java` and `QueueNode.java` (unchanged API, used as-is)
- Unit tests updated/added: `src/test/java/com/systems/demo/apnewsdemo/datastructures/nodebased/LRUCacheTest.java` (tests for put/get/eviction/remove)

#### 3. Problem Solving (`com.systems.demo.apnewsdemo.problem.solving`)
Multiple coding problem solutions (varied complexity) are present and useful for learning/assessment.

#### 4. Design Patterns (`com.systems.demo.apnewsdemo.design.patterns`)
- Singleton pattern implementation

#### 5. Multi-threading (`com.systems.demo.apnewsdemo.multi.threading`)
- Executor service management
- Worker threads
- Thread manager

#### 6. REST Client (`com.systems.demo.apnewsdemo.rest.client`)
- WebClient configuration for reactive HTTP calls
- Base URL: `http://localhost:3000`
- Default headers and cookies configured

#### 7. Exception Handling
- **ServiceException**: Custom exception with error codes and HTTP status
- **ExceptionResponseHandler**: Global exception handler using `@ControllerAdvice`
  - Handles `MethodArgumentNotValidException` (validation errors)
  - Handles `ServiceException` (business logic errors)

#### 8. DTOs (Data Transfer Objects)
- **Request DTOs**: CreatePlayerDto, CreateSportsDto, UpdatePlayerSportsDto, DocumentDto, CertificateDto, RecordDto
- **Response DTOs**: PlayerDto, SportsDto, ErrorDto
- **Web DTOs**: WebClientResponse

### Database Configuration

**H2 Database** (default - `application-h2.yml`)
- In-memory database
- URL: `jdbc:h2:mem:ap-news`
- DDL mode: `create` (recreates schema on startup)

**MySQL Database** (`application-mysql.yml`)
- Production database option
- URL: `jdbc:mysql://localhost:3306/ap-news`
- DDL mode: `update` (updates schema)

**Schema** (`schema.sql`)
- Creates `sports_database` schema
- Tables: `player`, `sport`, `player_sport` (join table)
- Foreign key constraints defined

**Seed Data** (`data/data.sql`)
- 6 sample players (various ages, levels, genders)
- 4 sports (Cricket, Hockey, Football, Badminton)
- Sample player-sport relationships

### Testing

**Test Files**:
- `PlayerServiceTest` - Unit tests for PlayerService
  - Tests update player
  - Tests players with no sports
  - Tests update player with sports
  - Tests pagination by sports category
- `SportServiceTest` - Unit tests for SportsService
- `LRUCacheTest` - Tests for the node-based key->value LRU cache (put/get/evict/remove)

**Test Dependencies**:
- JUnit 5
- Mockito
- OkHttp MockWebServer (for testing HTTP clients)

### Code Quality Observations

#### Strengths
1. ✅ Clean separation of concerns (Controller → Service → Repository)
2. ✅ Proper use of DTOs for API boundaries
3. ✅ Transaction management with `@Transactional`
4. ✅ Custom exception handling
5. ✅ Validation using Jakarta Validation
6. ✅ Pagination support
7. ✅ Good unit test coverage for service layer
8. ✅ Multiple database profile support
9. ✅ Lombok for reducing boilerplate

#### Areas for Improvement

1. Query / Repository issues
   - `PlayerRepository.getPlayersByAgeAndLevelAndGender()` appears to have incorrect parameter mapping in one query (double-check parameter names and types).

2. Service Implementation issues
   - `PlayerServiceImpl.createPlayer()` should be reviewed to ensure all DTO fields (like email) are being set correctly.
   - `updatePlayerWithSport()` should save the player after modifying relationships; validate transactional behavior.

3. File upload handling
   - The `POST /api/sports/upload/` endpoint currently logs the upload but lacks robust processing, validation, and storage logic.

4. Documentation
   - Missing README.md with setup and usage instructions.
   - No Swagger/OpenAPI documentation—consider adding `springdoc-openapi` for auto-generated API docs.

5. Security
   - No authentication/authorization present. Add Spring Security for production readiness.
   - Input validation/sanitization for file uploads and external inputs should be hardened.

6. Best Practices & Cleanliness
   - Replace magic numbers/strings (error codes) with constants or an enum.
   - Add consistent logging at important error/flow points.
   - Add JavaDoc to public-facing classes and methods.

7. Algorithms & Implementations
   - QuickSort iteration counting logic should be checked for correctness (observed double-counting in the current implementation).

### Project Purpose

This is a **demonstration/learning project** that showcases:
- Spring Boot REST API development
- JPA/Hibernate ORM usage
- Common algorithms and data structures
- Problem-solving approaches
- Multi-threading concepts
- Design patterns

The project name "ap-news-demo" suggests a news-related intent, but the current implementation is focused on sports management and algorithm/data-structure demos.

### Recommendations

1. Keep the recently implemented key->value LRU (done). Add more unit tests around edge cases (nulls, duplicate puts, zero/one capacity).
2. Fix the query parameter mapping issues in repositories and add integration tests for repository queries.
3. Improve file upload handling: validate file types/sizes, store files safely, and return meaningful responses.
4. Add a `README.md` with quickstart and dev notes.
5. Add Swagger/OpenAPI (springdoc) to document endpoints.
6. Add Spring Security and basic auth/role examples for endpoints.
7. Add integration tests (Spring Boot test slicing or full context) for controllers and repositories.
8. Add logging and move magic strings to constants or enums.
9. Review and fix QuickSort counting logic.

### Build, Run and Test (local)

Windows (using included wrapper):

```powershell
# Build the project
./mvnw.cmd clean install

# Run using embedded H2 (default profile)
./mvnw.cmd spring-boot:run

# Run only the LRU cache tests
mvn -Dtest=LRUCacheTest test
```

Notes:
- The project uses Maven wrapper files (`mvnw`, `mvnw.cmd`). If wrapper isn't available on your PATH, use system `mvn` instead.

---

**Analysis Date**: Updated automatically
**Project Version**: 0.0.1-SNAPSHOT

