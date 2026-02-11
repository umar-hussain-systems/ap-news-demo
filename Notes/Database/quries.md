
for given table 

```sql
EMP(emp_id, name, dept_id, salary)

```
Write a query to get all employees whose salary is above the average salary.

```sql
SELECT e.*
FROM emp e
WHERE e.salary > (SELECT AVG(salary) FROM emp);

```

second max higest salary query

2️⃣ Using DISTINCT (safer with duplicates)

```sql
SELECT MAX(salary)
FROM (SELECT DISTINCT salary FROM employee)
WHERE salary < (SELECT MAX(salary) FROM employee);

```


```sql
SELECT salary
FROM (
SELECT salary, DENSE_RANK() OVER (ORDER BY salary DESC) AS rnk
FROM employee
)
WHERE rnk = 2;

```
