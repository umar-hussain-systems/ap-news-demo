1 concurrent hash map vs map

	HashMap

		Not thread-safe

		Fast in single-thread use

		Concurrent writes can corrupt structure / lose updates

	ConcurrentHashMap
	
	
		HashMap is not thread-safe, so concurrent writes can corrupt its internal structure, leading to lost updates, infinite loops during resize (pre-Java 8), or inconsistent reads.

		ConcurrentHashMap is thread-safe by design and achieves this without synchronizing the entire map. Internally it uses fine-grained locking and CAS operations, allowing multiple threads to update different segments concurrently.

		It also provides atomic compound operations like putIfAbsent, computeIfAbsent, and merge, which avoid the classic read-modify-write race condition.

		Unlike Collections.synchronizedMap, it does not block all threads on every operation, making it suitable for high-concurrency systems like payment processing or caching.”

		ConcurrentHashMap is Thread-safe for concurrent access

		Allows high concurrency (reads don’t block like a single global lock)

		Operations like putIfAbsent, computeIfAbsent, merge are designed for safe atomic-style updates
		
		putIfAbsent -> always requires a pre-computed value, so the value is created even if unused.
		computeIfAbsent -> computes the value lazily and atomically, ensuring the computation happens only once when the key is absent. Returning null → removes key
		merge -> If key absent → insert value, If key present → remappingFunction(old, value) basically accumalte or add those two values  Returning null → removes key

		Iterators are weakly consistent (won’t throw ConcurrentModificationException like fail-fast collections; may not show every latest update)


2 what is the differnce between flat map and map in java

	Use map() when you transform values one to one and preserve the order
	
	Use flatMap() when you transform AND flatten list<list<Integer>> to single list


3 what is optional

	Optional is a container that may or may not contain a value.
	it exists to Prevent NullPointerException Force developers to handle absence explicitly


what is java memory model

		The Java Memory Model defines:

		How threads interact through memory

		Visibility, ordering, and atomicity guarantees

		Problems it solves

		CPU caching

		Instruction reordering

		Multi-core inconsistencies



what are checked and unchecked exceptions
Checked Exceptions are Checked at compile time  Must handle or declare are child of Exception
Unchecked Exceptions are run time errors

what is jvm life cycle
Jvm start :
Jvm Life cycle starts with jvm process creation

	class loading :
		loads byte code using class loader
		performs verifcation of byte code 
		intializes static variables and run static code block
	
	Runtime excution:
		excutes application threads 
		
	Garbage collection: 
		run in parallel in deamon threads
		
	Shutdown:
		after all the non deamon threads have finish the jvm terminates
	
	for grace full exit close all the thread pools and resources created by jmv process
	


jsp and servlet life cycle

🧠 Mental Model First

| Layer                     | Lifecycle                              | Thread Safety                  |
| ------------------------- | -------------------------------------- | ------------------------------ |
| **JSP**                   | Compiled into **one Servlet instance** | ❌ *Not thread-safe by default* |
| **Struts 2 Action**       | **New instance per request**           | ✅ *Thread-safe by design*      |
| **Struts 2 Interceptors** | Singleton                              | Must be thread-safe            |


1️⃣ Are JSPs Servlets? Are they Singleton?

Yes.

- A JSP is compiled into a Servlet class

- That servlet is instantiated once by the container

- All requests share the same servlet instance

- Each request is handled in a separate thread	

  Browser → Tomcat → JSP Servlet (single instance) → service(req,res) per thread

JSP = Singleton servlet

But each request has its own:

 - HttpServletRequest

 - HttpServletResponse

 - Stack frames

 - Thread

🧠 Servlet Lifecycle (Init → Service → Destroy)	

The servlet lifecycle is fully managed by the Servlet Container (Tomcat, Jetty, WebLogic, etc.).
```
Load → Instantiate → Init → Service → Destroy → GC
```
1️⃣ Loading & Instantiation

When does it happen?

On first request OR

On startup if load-on-startup is configured

``` xml
<servlet>
  <servlet-name>myServlet</servlet-name>
  <servlet-class>com.app.MyServlet</servlet-class>
  <load-on-startup>1</load-on-startup>
</servlet>
```


Container actions:

```
Load class
Create one instance of servlet
```


✔️ Only one instance per servlet definition

2️⃣ Initialization — init()

``` java
@Override
public void init(ServletConfig config) throws ServletException {
    // Initialize resources
    // DB pools, caches, configs, etc.
} 
```
  



