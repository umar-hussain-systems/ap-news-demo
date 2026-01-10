# ConcurrentHashMap — Memorization Cheat Sheet

## 1. put(key, value)
- **What**: Put value, overwrite if exists.
- **Atomic**: Yes (per key).
- **Lambda**: ❌ none

```java
map.put(k, v);
```

---
## 2. putIfAbsent(key, value)
- **What**: Insert only if key missing.
- **Atomic**: Yes

```java
map.putIfAbsent(k, v);
```

---
## 3. computeIfAbsent(key, mappingFn)
- **What**: If missing → compute & insert.
- **Lambda**: `(key) -> value`

```java
map.computeIfAbsent(k, key -> load(key));
```

---
## 4. computeIfPresent(key, remapFn)
- **What**: If present → update or delete.
- **Lambda**: `(key, oldVal) -> newVal`
- **Delete**: return `null`

```java
map.computeIfPresent(k, (key, old) -> old + 1);
```

---
## 5. compute(key, remapFn)
- **What**: Always run remap.
- **Lambda**: `(key, oldVal) -> newVal`
- **Create**: oldVal == null
- **Delete**: return `null`

```java
map.compute(k, (key, old) -> old == null ? 1 : old + 1);
```

---
## 6. merge(key, newVal, remapFn)
- **What**: If absent → put newVal; else combine.
- **Lambda**: `(oldVal, newVal) -> combined`
- **Delete**: return `null`

```java
map.merge(k, 1, Integer::sum);
```

---
## 7. remove(key)
- **What**: Remove entry.

```java
map.remove(k);
```

---
## 8. replace(key, value)
- **What**: Replace only if exists.

```java
map.replace(k, v);
```

---
## 9. replace(key, oldVal, newVal)
- **What**: Replace if key exists AND old matches.

```java
map.replace(k, old, newV);
```

---
## 10. getOrDefault(key, default)
- **What**: Read with fallback.

```java
map.getOrDefault(k, 0);
```

---
# Lambda Summary

| Method | Lambda Form |
|--------|------------|
| computeIfAbsent | `(key) -> value` |
| computeIfPresent | `(key, old) -> newVal` |
| compute | `(key, old) -> newVal` |
| merge | `(old, new) -> combined` |

---
# Deletion Rule

- compute / computeIfPresent: return `null` → delete
- merge: return `null` → delete

---
# Mental Model

```
merge:   (oldVal, newVal) -> combined
compute: (key, oldVal)    -> newVal
```


# Four Key Update Methods — Mental Model

## computeIfAbsent — Create if missing
```
Absent → computeIfAbsent(key, k -> v)
```
Example:
```java
map.computeIfAbsent("u1", k -> 1);
```

---
## computeIfPresent — Update if exists
```
Present → computeIfPresent(key, (k, old) -> v)
```
Example:
```java
map.computeIfPresent("u1", (k, old) -> old + 1);
```

---
## compute — Full control
```
Always → compute(key, (k, old) -> v)
```
Example:
```java
map.compute("u1", (k, old) -> old == null ? 1 : old + 1);
```

---
## merge — Accumulate
```
Accumulate → merge(key, newVal, (old, new) -> v)
```
Example:
```java
map.merge("u1", 1, Integer::sum);
```

