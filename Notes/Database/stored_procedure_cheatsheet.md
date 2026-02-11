# Stored Procedure with IN / OUT Parameters — Cheat Sheet

## Oracle Procedure

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

## Java Call

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

## Mental Model

1. Set IN params
2. Register OUT params
3. Execute
4. Read OUT params

## Common Pitfalls

- Forgetting registerOutParameter
- Wrong SQL type
- Wrong index (1-based)
- Using executeQuery instead of execute
