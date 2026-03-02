🔥 1️⃣ REQUIRED (default)

	Join existing transaction

	If none exists → create new

	Most common.

🔥 2️⃣ REQUIRES_NEW

	Suspend existing transaction

	Always create new transaction

	Commits independently

	Used for:

	Audit logs

	Outbox events

	Error tracking

🔥 3️⃣ NESTED

	Executes inside existing transaction

	Creates a savepoint

	Can roll back to savepoint without rolling back outer transaction

	Requires DB support (JDBC savepoints)

	Key difference from REQUIRES_NEW:

	NESTED is still part of outer transaction

	REQUIRES_NEW is fully independent

🔥 4️⃣ SUPPORTS

	If transaction exists → join it

	If none → execute non-transactionally

	Used when method can work both ways.

🔥 5️⃣ NOT_SUPPORTED

	Suspend existing transaction

	Execute non-transactionally

	Useful when:

	You don’t want locking overhead

	Long read-only reporting queries

🔥 6️⃣ MANDATORY

	Must run inside existing transaction

	If none exists → throw exception

	Used when:

	Method must always be part of larger transaction

🔥 7️⃣ NEVER

	Must NOT run inside transaction

	If transaction exists → throw exception

Rarely used.

🧠 Clean Interview Summary Table

| Type          | If Tx Exists  | If No Tx  |
| ------------- | ------------- | --------- |
| REQUIRED      | Join          | Create    |
| REQUIRES_NEW  | Suspend + New | Create    |
| NESTED        | Savepoint     | Create    |
| SUPPORTS      | Join          | Non-Tx    |
| NOT_SUPPORTED | Suspend       | Non-Tx    |
| MANDATORY     | Join          | Exception |
| NEVER         | Exception     | Non-Tx    |
