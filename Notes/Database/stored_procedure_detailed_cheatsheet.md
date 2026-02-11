# Stored Procedures with IN / OUT Parameters — Detailed Cheat Sheet

## 1. What is a Stored Procedure
A stored procedure is a named PL/SQL block stored in the database that can accept parameters, execute logic, and optionally return values.

## 2. Parameter Types
| Type | Direction | Description |
|------|-----------|-------------|
| IN | Input | Passed from Java to DB |
| OUT | Output | Returned from DB to Java |
| IN OUT | Both | Passed in and modified |

---

## 3. Oracle Examples

### 3.1 IN + OUT Procedure
```sql
CREATE OR REPLACE PROCEDURE get_employee_salary (
    p_emp_id   IN  NUMBER,
    p_name     OUT VARCHAR2,
    p_salary   OUT NUMBER
)
AS
BEGIN
    SELECT name, salary
    INTO p_name, p_salary
    FROM employee
    WHERE emp_id = p_emp_id;
EXCEPTION
    WHEN NO_DATA_FOUND THEN
        p_name := NULL;
        p_salary := NULL;
END;
/
```

### 3.2 IN OUT Procedure
```sql
CREATE OR REPLACE PROCEDURE increment_salary (
    p_salary IN OUT NUMBER
)
AS
BEGIN
    p_salary := p_salary + 1000;
END;
/
```

---

## 4. Calling from Java (CallableStatement)

### 4.1 IN + OUT
```java
String sql = "{ call get_employee_salary(?, ?, ?) }";
CallableStatement cs = conn.prepareCall(sql);

cs.setInt(1, 101);
cs.registerOutParameter(2, Types.VARCHAR);
cs.registerOutParameter(3, Types.NUMERIC);

cs.execute();

String name = cs.getString(2);
Double salary = cs.getDouble(3);
```

### 4.2 IN OUT
```java
String sql = "{ call increment_salary(?) }";
CallableStatement cs = conn.prepareCall(sql);

cs.setDouble(1, 5000);
cs.registerOutParameter(1, Types.NUMERIC);

cs.execute();

double newSalary = cs.getDouble(1);
```

---

## 5. Mental Model
1. Prepare call
2. Set IN values
3. Register OUT values
4. Execute
5. Read outputs

---

## 6. Common Pitfalls
- Forgetting registerOutParameter
- Using wrong SQL type
- Wrong parameter index (1-based)
- Using executeQuery instead of execute
- Calling get before execute

---

## 7. Interview Notes
- Procedures do not return values directly; they use OUT params.
- Functions return a value and can be used in SELECT.
- Java uses CallableStatement for procedures.
- REF CURSOR is used for result sets.

---

## 8. Function Example (for comparison)
```sql
CREATE OR REPLACE FUNCTION get_bonus(p_salary NUMBER)
RETURN NUMBER IS
BEGIN
  RETURN p_salary * 0.1;
END;
/
```

```java
CallableStatement cs = conn.prepareCall("{ ? = call get_bonus(?) }");
cs.registerOutParameter(1, Types.NUMERIC);
cs.setDouble(2, 10000);
cs.execute();
double bonus = cs.getDouble(1);
```

---

## 9. REF CURSOR Example
```sql
CREATE OR REPLACE PROCEDURE get_all_employees(p_cursor OUT SYS_REFCURSOR) AS
BEGIN
  OPEN p_cursor FOR SELECT * FROM employee;
END;
/
```

---

## 10. Error Handling
```sql
EXCEPTION
  WHEN OTHERS THEN
    RAISE;
```

Use try-catch in Java to handle SQLException.
