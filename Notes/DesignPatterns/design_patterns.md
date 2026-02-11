# Struts 2 — Design Patterns Cheat Sheet

## 1. Singleton
Ensures only one instance exists.

**Struts Example**
- StrutsPrepareAndExecuteFilter
- Spring default beans

**Why**
Shared infrastructure, must be thread-safe.

---

## 2. Factory
Encapsulates object creation.

**Struts Example**
- ObjectFactory creates Action classes
- SpringObjectFactory integration

---

## 3. Builder
Builds complex objects step by step.

**Struts Example**
- Building search/filter criteria
- Building DTOs from request params

---

## 4. Decorator
Adds behavior dynamically by wrapping.

**Struts Example**
- Interceptor stack
- Servlet filters

---

## 5. Strategy
Encapsulates interchangeable algorithms.

**Struts Example**
- Validation strategies
- Pricing, discount, routing logic

---

## 6. Proxy
Controls access to another object.

**Struts / Spring Example**
- @Transactional proxy
- Security proxies
- Lazy loading proxies (Hibernate)

---

## 7. Template Method
Defines algorithm skeleton, subclasses override steps.

**Struts Example**
- AbstractAction classes
- Framework lifecycle hooks

---

## Decorator vs Proxy

| Aspect | Decorator | Proxy |
|--------|-----------|--------|
| Intent | Add behavior | Control access |
| Typical use | Logging, metrics | Lazy loading, security |
| Example | Interceptor | @Transactional proxy |

---

## Interview Mapping

| Pattern | Where in Struts |
|---------|-----------------|
| Singleton | Filters, Spring services |
| Factory | ObjectFactory |
| Decorator | Interceptors |
| Strategy | Business rules |
| Proxy | Spring AOP |
| Template | Abstract actions |

---

## Key Interview Lines

- Actions are per request, not singleton.
- Interceptors form a decorator chain.
- Spring uses proxies for transactions/security.
- Factories decouple creation from usage.
- Builders simplify construction of complex objects.
- Strategy removes if-else logic.
- Template Method defines fixed workflows with flexible steps.
