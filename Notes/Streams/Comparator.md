how to do comparison i have compare on more the one field sort by name and age

Mental Model for Comparison

Compare primary field first.
If equal → compare secondary field.
If equal → compare next field…

That’s it.

The comparator pattern

1️⃣ Always remember this pattern:

```
Comparator.comparing(primary)
.thenComparing(secondary)
.thenComparing(third)
```
2️⃣ Your case: sort by name, then by age

```java
Comparator<Emp> byNameThenAge =
    Comparator.comparing(Emp::name)
              .thenComparing(Emp::age);

```
Useage

```java

List<Emp> sorted =
    emp.stream()
       .sorted(byNameThenAge)
       .toList();

```

3️⃣ Descending age

```java
Comparator<Emp> byNameThenAgeDesc =
    Comparator.comparing(Emp::name)
              .thenComparing(Emp::age, Comparator.reverseOrder());

```
4️⃣ Case-insensitive name

```java
Comparator<Emp> byNameIgnoreCaseThenAge =
    Comparator.comparing(Emp::name, String.CASE_INSENSITIVE_ORDER)
              .thenComparing(Emp::age);

```

5️⃣ Full example

```java
List<Emp> sorted =
    emp.stream()
       .sorted(
           Comparator.comparing(Emp::name)
                     .thenComparing(Emp::age)
       )
       .toList();

```

6️⃣ If you already have a grouped map and want to sort inside each group

```java
Map<Gender, List<Emp>> byGenderSorted =
    emp.stream().collect(
        groupingBy(
            Emp::gender,
            collectingAndThen(
                toList(),
                list -> list.stream()
                            .sorted(
                                Comparator.comparing(Emp::name)
                                          .thenComparing(Emp::age)
                            )
                            .toList()
            )
        )
    );

```

| Want         | Code                            |
| ------------ | ------------------------------- |
| Primary sort | `Comparator.comparing(...)`     |
| Secondary    | `.thenComparing(...)`           |
| Descending   | `Comparator.reverseOrder()`     |
| Ignore case  | `String.CASE_INSENSITIVE_ORDER` |


One-line memory hook

Compare primary → then compare secondary → then compare next.

or

Comparator.comparing(...).thenComparing(...).thenComparing(...)

That’s the only pattern you need 