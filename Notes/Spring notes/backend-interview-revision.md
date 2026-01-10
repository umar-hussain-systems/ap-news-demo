# Backend Interview Revision Notes

## 1. What is Spring Boot and how does auto-configuration work?

Spring Boot builds on top of the Spring Framework by providing opinionated defaults and auto-configuration so that applications can be up and running with minimal configuration.

When you add a starter dependency, Spring Boot brings in required libraries and activates auto-configuration classes that create common beans like DataSource, EntityManager, and repositories.

Auto-configuration is conditional — it checks:
- What is on the classpath
- What beans already exist
- What configuration properties are defined

If a custom configuration is provided, Spring Boot backs off and uses that instead. This follows a convention-over-configuration approach.

---

## 2. Explain the Spring Bean lifecycle and scopes.

### Bean Lifecycle

1. Bean is instantiated using the constructor
2. Dependencies are injected
3. BeanPostProcessor before initialization runs
4. Initialization callbacks like `@PostConstruct` run
5. BeanPostProcessor after initialization runs (proxies like `@Transactional` are applied)
6. Bean is ready for use
7. On shutdown, destruction callbacks like `@PreDestroy` are called

### Bean Scopes

| Scope      | Creation | Destruction | Usage |
|-----------|----------|-------------|--------|
| Singleton | Once per context | On shutdown | Stateless services |
| Prototype | Every request | Not managed by Spring | Stateful per-use objects |
| Request   | Per HTTP request | End of request | Request-specific state |

Prototype beans are created by Spring but must be destroyed by the application if they hold resources.

---

## 3. How does @Transactional work and what are common pitfalls?

`@Transactional` uses Spring AOP and proxy pattern. The proxy wraps method calls with transaction logic:

- Start transaction before method
- Commit if successful
- Rollback if runtime exception occurs

### Common Pitfalls
- Self-invocation bypasses the proxy
- Private methods cannot be proxied
- Checked exceptions do not trigger rollback by default
- Wrong propagation or isolation settings

---

## 4. Difference between persist(), merge(), and save()

- `persist()` — makes a new transient entity managed; insert happens on flush/commit.
- `merge()` — copies state of a detached entity into a managed instance and returns it.
- `save()` — Hibernate-specific method similar to persist for new entities.

---

## 5. What is the N+1 problem?

The N+1 problem occurs when fetching parent entities lazily loads children per entity, resulting in one query for parents and N additional queries for children.

Detection: SQL logs or Hibernate statistics.  
Fix: `JOIN FETCH`, entity graphs, or batch fetching.

---

## 6. How do you safely update shared resources?

- In-memory concurrency: use Atomic types or locks.
- Database concurrency: use optimistic locking, atomic SQL updates, or pessimistic locking.
- Distributed systems: use idempotency, versioning, and transactional boundaries.

---

## 7. Exception handling in Spring Boot

- Business exceptions are thrown from service layer and mapped using `@RestControllerAdvice`.
- System exceptions are handled with generic responses and logged internally.
- A fallback handler catches unexpected exceptions and returns a generic 500 response.

---

## 8. Hibernate caching

- First-level cache: mandatory, per persistence context/session.
- Second-level cache: optional, shared across sessions, backed by Ehcache/Infinispan.

Caching helps in read-heavy scenarios and hurts when data changes frequently due to staleness risk.

---

## 9. Data consistency between microservices

Use eventual consistency with Saga pattern:
- Each service uses local ACID transactions.
- Outbox pattern ensures reliable event publishing.
- Downstream services react to events and apply updates or compensations.
- Idempotency and versioning prevent duplicates.

---

## 10. Example problem solved — Inventory cart locking

Prevented overselling by implementing cart-level inventory locking with TTL.

- Reserved inventory when added to cart.
- Checkout consumed locked inventory first.
- Scheduled job released expired locks.
- Improved checkout reliability and eliminated overselling.

---

## Key One-liners

- "Singleton = service, Prototype = task."
- "Authentication proves identity, authorization controls access."
- "Strong consistency within a service, eventual consistency across services."
- "Never use distributed transactions; use Saga + Outbox instead."

