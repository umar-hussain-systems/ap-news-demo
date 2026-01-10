
# Java Streams — groupingBy & Collectors Mental Model

---


- **Source**: Collection, Array, I/O
- **Intermediate Ops** (lazy): `map`, `filter`, `flatMap`, `sorted`
- **Terminal Ops** (eager): `forEach`, `collect`, `reduce`

**Mental model**
> A stream is a conveyor belt.  
> Nothing moves until a terminal operation pulls elements through.

---

## 1. Mini Dataset

```java
enum Gender { male, female }

record Emp(String name, String dept, Long salary, int age, Gender gender) {}

List<Emp> emp = List.of(
    new Emp("Ali",   "IT", 5000L, 30, Gender.male),
    new Emp("Omar",  "IT", 6000L, 35, Gender.male),
    new Emp("Sara",  "HR", 4500L, 28, Gender.female),
    new Emp("Aisha", "HR", 7000L, 40, Gender.female)
);
```


## 2. collect() — Mental Model

### Signature

```java
<R, A> R collect(Collector<? super T, A, R> collector);
```
classifier → decides the bucket (map key)

downstream → reduces elements inside each bucket

default downstream = toList()

Mental model

First bucket elements using the classifier,
then apply a reduction inside each bucket.

Collectors — Mental Model

A Collector is a recipe with four parts:

```
Supplier – creates empty container

Accumulator – adds one element

Combiner – merges containers (parallel)

Finisher – optional final transformation
```
example

```java
List<String> names =
  emp.stream().collect(
    ArrayList::new,
    (list, e) -> list.add(e.name()),
    List::addAll
  );

```
this is Equivalent to

```java
List<String> names = emp.stream().map(Employee::name).collect(Collectors.toList());
```
or without method reference

```java
List<String> names = emp.stream().map(e -> e.name()).collect(Collectors.toList());
```


3. **groupingBy — Mental Model**
   General form
```
groupingBy(classifier, downstream)
```
Mental model for groupingby fuction

collect() executes a recipe to materialize the stream into a result.

```java
Map<Gender, List<Emp>> byGender =
    emp.stream().collect(groupingBy(Emp::gender));

```
response 

```
male   → [Ali, Omar]
female → [Sara, Aisha]
```
or Equivalent imperative mental model:

```java
Map<Gender, List<Emp>> createGroupingBYMap() {
  Map<Gender, List<Emp>> map = new HashMap<>();
  for (Emp e : emp) {
    Gender k = e.gender();                 // classifier (routing function)
    map.computeIfAbsent(k, x -> new ArrayList<>()).add(e);
  }
  return map;
}
```
**Count per gender**
```java
Map<Gender, Long> countByGender =
    emp.stream().collect(groupingBy(Emp::gender, counting()));

```

result 

```
male   → 2
female → 2

```
**Salary sum per gender**

```java
Map<Gender, Long> salarySumByGender =
  emp.stream().collect(groupingBy(Emp::gender, summingLong(Emp::salary)));

```

result 

```
male   → 11000
female → 11500
```

mapping function after groupby

```java
Map<Gender, List<String>> namesByGender =
  emp.stream().collect(
    groupingBy(Emp::gender, mapping(Emp::name, toList()))
  );

```

result

```
male   → ["Ali", "Omar"]
female → ["Sara", "Aisha"]

```
**Highest paid per gender**

```java
Map<Gender, Optional<Emp>> highestPaid =
  emp.stream().collect(
    groupingBy(Emp::gender, maxBy(comparing(Emp::salary)))
  );



```
**Map name → salary inside gender**

after grouping by gender map to name and salary

```java
Map<Gender, Map<String, Long>> salaryByNameGender =
  emp.stream().collect(
    groupingBy(
      Emp::gender,
      toMap(Emp::name, Emp::salary)
    )
  );

```

| Concept    | Mental Model                  |
| ---------- | ----------------------------- |
| Stream     | Conveyor belt                 |
| map        | Transform each item           |
| filter     | Drop unwanted items           |
| flatMap    | Expand/flatten                |
| collect    | Materialize into container    |
| groupingBy | Bucket + reduce               |
| downstream | What to do inside each bucket |



One-line rules

map(f) → change element

filter(p) → keep or drop element

groupingBy(k, d) → bucket by k, reduce with d

collect → execute and build result
