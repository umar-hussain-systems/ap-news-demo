# 🧬 Spring Bean Lifecycle — Bullet Points

## Lifecycle Steps (in order)

### 1. Instantiation
- Spring creates the bean using the constructor.

### 2. Dependency Injection
- Spring injects dependencies via:
  - Constructor injection
  - Field injection
  - Setter injection

### 3. Aware Callbacks (optional)
If implemented, Spring calls:
- `BeanNameAware`
- `ApplicationContextAware`
- `BeanFactoryAware`
- etc.

### 4. BeanPostProcessor — Before Initialization
- `postProcessBeforeInitialization()` is called.

### 5. Initialization
One of the following is called:
- `@PostConstruct`
- `InitializingBean.afterPropertiesSet()`
- Custom `init-method`

### 6. BeanPostProcessor — After Initialization
- `postProcessAfterInitialization()` is called.
- Proxies like `@Transactional`, `@Async`, `@Cacheable` are created here.

### 7. Bean is Ready for Use
- The bean can now be injected and used.

### 8. Destruction (on shutdown)
One of the following is called:
- `@PreDestroy`
- `DisposableBean.destroy()`
- Custom `destroy-method`

---

## Bean Scope Lifecycle Comparison

| Scope                    | Created                     | Destroyed               | Typical Use               |
| ------------------------ | --------------------------- |-------------------------| ------------------------- |
| **singleton** (default) | Once per context            | On context shutdown     | Stateless services        |
| **prototype**           | Every time requested        | ❌ Not by Spring         | Stateful per-use objects |
| **request**             | Once per HTTP request       | End of request          | Web request state         |
| **session**             | Once per HTTP session       | Session ends            | User session data         |
| **application**         | Once per ServletContext     | On context shutdown     | App-wide shared           |
| **websocket**           | Once per WebSocket session  | Session ends            | WebSocket state           |


**BeanPostProcessor — Before Initialization**
Where postProcessBeforeInitialization() fits

```
Constructor
→ Dependency Injection
→ Aware callbacks
→ postProcessBeforeInitialization()   👈 HERE
→ @PostConstruct / afterPropertiesSet
→ postProcessAfterInitialization()

```
Spring internally loops over all registered BeanPostProcessors and calls:

```java
{
for (BeanPostProcessor bpp : beanPostProcessors) {
    bean = bpp.postProcessBeforeInitialization(bean, beanName);
}

```
***✅ Production-style example: Auto-validate configuration beans***

Use case:
Before a bean is initialized, we want to verify something about it — 
e.g., validate required fields, enforce invariants, or auto-wrap it.

**1️⃣ Define an annotation**

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateConfig {
}

```
***2️⃣ Example bean that uses it***
```java
@Component
@ValidateConfig
public class PaymentConfig {

    @Value("${payment.timeout}")
    private Integer timeout;

    @PostConstruct
    public void init() {
        System.out.println("PaymentConfig @PostConstruct called");
    }

    public Integer getTimeout() {
        return timeout;
    }
}

```

**3️⃣ Implement BeanPostProcessor**

```java
@Component
public class ConfigValidationPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        if (bean.getClass().isAnnotationPresent(ValidateConfig.class)) {
            System.out.println("🔍 Validating bean before init: " + beanName);

            for (Field field : bean.getClass().getDeclaredFields()) {
                if (field.getType().equals(Integer.class)) {
                    field.setAccessible(true);
                    try {
                        Object value = field.get(bean);
                        if (value == null) {
                            throw new IllegalStateException(
                                "Field " + field.getName() + " in " + beanName + " must not be null"
                            );
                        }
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return bean; // IMPORTANT
    }
}

```
**4️⃣ Startup sequence (what happens)**

**Assume** payment.timeout is defined.

```
🔍 Validating bean before init: paymentConfig
PaymentConfig @PostConstruct called

```
If payment.timeout is missing:

```
🔍 Validating bean before init: paymentConfig
Exception in thread "main" java.lang.IllegalStateException:
Field timeout in paymentConfig must not be null
```

🧠 What exactly happened?

| Step                                             | Action                    |
| ------------------------------------------------ | ------------------------- |
| Spring instantiates `PaymentConfig`              | `new PaymentConfig()`     |
| Spring injects `@Value`                          | `timeout = ...`           |
| Spring calls aware methods                       | (none here)               |
| Spring calls `postProcessBeforeInitialization()` | validation happens        |
| Spring calls `@PostConstruct`                    | only if validation passes |
| Bean becomes usable                              | if no exception           |


#### 🧬 Step 5 — Initialization Phase

This step happens after:

 - Constructor
 - Dependency injection
 - *Aware callbacks
 - postProcessBeforeInitialization()

and **before**:

 - postProcessAfterInitialization()
 - Bean becomes usable

So the call stack is effectively:

```
create bean
→ inject dependencies
→ aware callbacks
→ postProcessBeforeInitialization()
→ INIT callbacks   👈 YOU ARE HERE
→ postProcessAfterInitialization()

```
 #### What does “Initialization” mean?

Initialization is where **your bean is allowed** to:

 - validate its own state
 - initialize internal resources
 - start background threads / schedulers
 - build caches
 - verify configuration

It’s the first moment your bean is considered “complete”.

#### **The 3 initialization mechanisms**

They all do the same kind of thing — just through different APIs.

| Mechanism                               | Style             | Typical use        |
| --------------------------------------- | ----------------- | ------------------ |
| `@PostConstruct`                        | Annotation-based  | Most common        |
| `InitializingBean.afterPropertiesSet()` | Interface-based   | Framework / legacy |
| `init-method`                           | XML / Java config | External config    |


#### **1️⃣ @PostConstruct (recommended)**

```java
@Component
public class CacheService {

    private final Repository repo;
    private Map<Long, Entity> cache;

    public CacheService(Repository repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void init() {
        System.out.println("🔹 @PostConstruct: building cache");
        this.cache = repo.findAll()
                         .stream()
                         .collect(Collectors.toMap(Entity::getId, e -> e));
    }
}

```

**🎯 What should go in init?**

Good things:

 - Verify required configuration
 - Warm up caches
 - Validate external connections
 - Register with registries
 - Start schedulers

Bad things:

 - Blocking network calls without timeout
 - Heavy DB loads on startup
 - Infinite loops / long-running tasks
 - Business logic

🧪 Example: Fail-fast validation

```java
@PostConstruct
void validate() {
if (timeout <= 0) {
throw new IllegalStateException("timeout must be positive");
}
}
```


Prevents broken system from starting.

💬 Interview-ready answer

During the initialization phase, Spring invokes the bean’s initialization callbacks in the following order: 
first any 
@PostConstruct methods, 
then InitializingBean.afterPropertiesSet(), 
and finally any custom init-method defined in configuration.
This phase is intended for validating configuration and initializing internal resources before the bean becomes usable.

🧠 Mental model

Initialization = “the bean is now complete, let me prepare myself to work.”

TL;DR

| Mechanism              | Use                         |
| ---------------------- | --------------------------- |
| `@PostConstruct`       | Normal apps                 |
| `afterPropertiesSet()` | Framework-level             |
| `init-method`          | External config / 3rd party |



#### **🧬 Step 6 — postProcessAfterInitialization()**

This is the step where Spring:

- wraps your bean in a proxy
- applies AOP advice (@Transactional, @Async, @Cacheable, @Retryable, etc.)
- possibly replaces the bean instance with a different one

In other words:
- The bean that leaves this phase is not necessarily the same object that entered it.

```java
for (BeanPostProcessor bpp : beanPostProcessors) {
    bean = bpp.postProcessAfterInitialization(bean, beanName);
}

```

Concrete Example: How @Transactional is applied

 Let’s build a minimal, observable example.
 1️⃣ Service with @Transactional

```java
@Service
public class PaymentService {

    @Transactional
    public void pay() {
        System.out.println("💳 Executing payment logic");
    }
}

```
2️⃣ Inspect whether it becomes a proxy

```java
@Component
public class ProxyInspector implements CommandLineRunner {

    private final PaymentService service;

    public ProxyInspector(PaymentService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        System.out.println("Bean class: " + service.getClass());
        System.out.println("Is proxy? " + AopUtils.isAopProxy(service));
        service.pay();
    }
}

```
result 

```
Bean class: class com.sun.proxy.$Proxy123   // or CGLIB subclass
Is proxy? true
💳 Executing payment logic

```

**🧪 Show it explicitly using a custom post-processor**

```java
@Component
public class LoggingPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (AopUtils.isAopProxy(bean)) {
            System.out.println("🧩 Proxy created for bean: " + name);
        }
        return bean;
    }
}

```