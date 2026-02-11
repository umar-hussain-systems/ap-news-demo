
#### 🔹 Stored Procedure vs Function in Oracle
1️⃣ High-level difference

| Aspect                                  | **Stored Procedure**            | **Function**                        |
| --------------------------------------- | ------------------------------- | ----------------------------------- |
| Purpose                                 | Perform an **action / process** | Compute and **return a value**      |
| Must return value?                      | ❌ No                            | ✅ Yes (exactly one)                 |
| Can be used in SQL (SELECT, WHERE)?     | ❌ No                            | ✅ Yes                               |
| Can have OUT parameters?                | ✅ Yes                           | ❌ No (only IN)                      |
| Can modify data (INSERT/UPDATE/DELETE)? | ✅ Yes                           | ⚠️ Technically yes, but discouraged |
| Can be called from SQL                  | ❌ No                            | ✅ Yes                               |
| Typical use                             | Business operations, batch jobs | Calculations, validations, lookups  |
| Returns via                             | OUT parameters                  | RETURN keyword                      |


2️⃣ Syntax comparison

- **Stored Procedure** 

```sql
CREATE OR REPLACE PROCEDURE update_salary(
  p_emp_id IN NUMBER,
  p_new_salary IN NUMBER
) AS
BEGIN
  UPDATE employees SET salary = p_new_salary WHERE emp_id = p_emp_id;
END;

```
call

```sql
BEGIN
  update_salary(1001, 9000);
END;

```

- **Function**

definition

```sql
CREATE OR REPLACE FUNCTION get_salary(p_emp_id IN NUMBER)
RETURN NUMBER AS
  v_salary NUMBER;
BEGIN
  SELECT salary INTO v_salary FROM employees WHERE emp_id = p_emp_id;
  RETURN v_salary;
END;

```


calling 
```sql
SELECT get_salary(1001) FROM dual;
```


3️⃣ Interview mental model

Procedure = "Do something"
Function = "Calculate something"
 
- If your DB logic is performing an operation → use Procedure

- If your DB logic is returning a value to be used in a query → use Function

4️⃣ Key interview points
🔹 Why can't procedures be used in SQL?

Because SQL expects a scalar value. Procedures return nothing (or multiple OUT params), so SQL can't embed them.

🔹 Why functions should avoid DML?

Because functions can be used inside SELECT statements. 
If they change data, it breaks SQL purity and can cause side effects, rollback issues, or ORA errors.

🔹 Can functions call procedures and vice versa?

Yes — but functions should not call procedures that modify data.

5️⃣ In Struts 2 / legacy JDBC context

| Use case                            | Use       |
| ----------------------------------- | --------- |
| Validation logic                    | Function  |
| Derived fields (tax, discount, age) | Function  |
| Saving form data                    | Procedure |
| Batch jobs / nightly sync           | Procedure |
| Transactional business flow         | Procedure |

6️⃣ Java (JDBC) calling difference

Calling a procedure
```sql

CallableStatement cs = conn.prepareCall("{ call update_salary(?, ?) }");
cs.setInt(1, 1001);
cs.setInt(2, 9000);
cs.execute();

```

Calling a function

```sql
CallableStatement cs = conn.prepareCall("{ ? = call get_salary(?) }");
cs.registerOutParameter(1, Types.NUMERIC);
cs.setInt(2, 1001);
cs.execute();
int salary = cs.getInt(1);

```
7️⃣ One-line interview answer

Stored procedures are used to perform actions and manage business workflows, 
while functions are used to compute and return values that can be embedded in SQL queries. 
Procedures do not return a value and cannot be used in SELECT statements, 
whereas functions must return a value and can be used inside SQL expressions.

**8️⃣ Common traps interviewers check**

| Trap                                     | Correct Answer      |
| ---------------------------------------- | ------------------- |
| Can procedure return value?              | Only via OUT params |
| Can function be used in SELECT?          | Yes                 |
| Can procedure be used in SELECT?         | No                  |
| Should functions update tables?          | No                  |
| Can both be called from Java?            | Yes                 |
| Are they both compiled and stored in DB? | Yes                 |
