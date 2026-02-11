# Object Oriented Programming (OOP) — Mock Interview Revision

Author: Muhammad Umar Hussain  
Level: Senior / Principal Java Backend Engineer

---

## 1. What are the four pillars of OOP?

### Answer:
1. **Encapsulation** — Bundling data and behavior in a class and restricting direct access to the internal state.
2. **Abstraction** — Exposing only necessary behavior while hiding implementation details.
3. **Inheritance** — Reusing and extending behavior from a base class.
4. **Polymorphism** — One interface, many implementations — behavior resolved at runtime.

---

## 2. Encapsulation vs Abstraction

| Aspect | Encapsulation | Abstraction |
|--------|--------------|-------------|
| What it hides | Object state (fields) | Implementation logic |
| Purpose | Protect data integrity | Reduce complexity |
| Java tools | private fields, getters/setters | interfaces, abstract classes |

---

## 3. What is Encapsulation?

Encapsulation means keeping object state private and providing controlled access via public methods. This prevents invalid state changes and enforces business rules.

---

## 4. What is Abstraction?

Abstraction hides internal logic and exposes only behavior. Clients depend on contracts, not implementations.

---

## 5. What is Inheritance?

Inheritance allows a class to reuse and extend behavior from another class using `extends`. It models an "is-a" relationship.

---

## 6. What is Polymorphism?

Polymorphism allows a base type reference to point to different concrete implementations, with method execution resolved at runtime.

---

## 7. Compile-time vs Runtime Polymorphism

| Type | Example | Binding |
|------|----------|----------|
| Compile-time | Method overloading | Compile time |
| Runtime | Method overriding | Runtime |

---

## 8. Why prefer composition over inheritance?

Composition avoids tight coupling and fragile hierarchies. It is more flexible and easier to maintain.

---

## 9. Interface vs Abstract Class

| Interface | Abstract Class |
|-----------|----------------|
| Multiple inheritance | Single inheritance |
| No instance fields | Can have state |
| Pure contract | Partial implementation |

---

## 10. SOLID Principles

- **S** — Single Responsibility Principle
- **O** — Open/Closed Principle
- **L** — Liskov Substitution Principle
- **I** — Interface Segregation Principle
- **D** — Dependency Inversion Principle

---

## 11. Liskov Substitution Principle

Subtypes must be substitutable for their base types without breaking correctness.

---

## 12. Coupling vs Cohesion

| Coupling | Cohesion |
|----------|----------|
| Interdependency between classes | Focus of a class |
| Should be low | Should be high |

---

## 13. Immutability

An immutable object cannot change after creation. It is thread-safe and predictable.

---

## 14. Method Overriding

Providing a new implementation for a superclass method in a subclass with the same signature.

---

## 15. Can static methods be overridden?

No — static methods are hidden, not overridden.

---

## 16. Final keyword usage

- `final class` — cannot be inherited
- `final method` — cannot be overridden
- `final variable` — cannot be reassigned

---

## 17. Marker Interfaces

Empty interfaces used to signal behavior, e.g., `Serializable`.

---

## 18. Dependency Inversion

High-level modules depend on abstractions, not concrete implementations.

---

## 19. POJO

Plain Old Java Object — no framework dependency.

---

## 20. High vs Low Cohesion

High cohesion means a class has a single responsibility. Low cohesion means it does many unrelated things.

---

## 21. Diamond Problem

Multiple inheritance ambiguity avoided in Java by allowing only interface multiple inheritance.

---

## 22. Association vs Aggregation vs Composition

| Type | Relationship |
|------|-------------|
| Association | Loose |
| Aggregation | Whole-part but independent |
| Composition | Strong lifecycle dependency |

---

## 23. Anti-patterns

- God Object
- Tight coupling
- Deep inheritance hierarchies

---

## 24. Separation of Concerns

Separating system into distinct responsibilities improves maintainability.

---

## 25. Why OOP?

OOP improves modularity, maintainability, extensibility, and testability.

---

## 26. == vs equals()

| == | equals |
|----|---------|
| Reference comparison | Logical comparison |

---

## 27. Constructors

Constructors are not inherited and cannot be overridden.

---

## 28. Access Modifiers

Control visibility and enforce encapsulation.

---

## 29. Open/Closed Principle

Open for extension, closed for modification.

---

## 30. OOP and Testing

OOP enables mocking, isolation, and dependency injection.

---
