package com.systems.demo.apnewsdemo.concurrent.hashmap;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Cache-Aside pattern:
 * - READ: cache -> DB -> cache
 * - WRITE: DB -> invalidate cache
 *
 * Includes TTL + per-key synchronization via compute(...) to avoid thundering herd.
 */
public class CacheAsideDemo {

  // ---- Example DB repository ----
  interface UserRepo {
    Optional<User> findById(String id);
    void save(User user);
    void deleteById(String id);
  }

  // ---- Domain ----
  record User(String id, String name) {}

  // ---- Cache entry with TTL ----
  static final class CacheEntry<V> {
    final V value;
    final long expiresAtMillis;

    CacheEntry(V value, long expiresAtMillis) {
      this.value = value;
      this.expiresAtMillis = expiresAtMillis;
    }

    boolean isExpired(long nowMillis) {
      return nowMillis >= expiresAtMillis;
    }
  }

  // ---- Cache-Aside service ----
  static final class UserService {
    private final UserRepo repo;
    private final ConcurrentMap<String, CacheEntry<User>> cache = new ConcurrentHashMap<>();
    private final long ttlMillis;

    UserService(UserRepo repo, Duration ttl) {
      this.repo = Objects.requireNonNull(repo);
      this.ttlMillis = ttl.toMillis();
    }

    /**
     * READ: try cache first, on miss/expired go to DB and populate cache.
     * Concurrency-safe: compute(...) ensures only one thread loads per key at a time.
     */
    public Optional<User> getUser(String id) {
      long now = System.currentTimeMillis();

      CacheEntry<User> entry = cache.get(id);
      if (entry != null && !entry.isExpired(now)) {
        return Optional.of(entry.value);
      }

      // If missing/expired, compute atomically for this key
      CacheEntry<User> loaded = cache.compute(id, (key, existing) -> {
        long nowInside = System.currentTimeMillis();
        if (existing != null && !existing.isExpired(nowInside)) {
          return existing; // another thread refreshed it
        }

        // Load from DB
        Optional<User> fromDb = repo.findById(key);
        if (fromDb.isEmpty()) {
          return null; // keep cache empty for this key (no entry)
        }

        return new CacheEntry<>(fromDb.get(), nowInside + ttlMillis);
      });

      return loaded == null ? Optional.empty() : Optional.of(loaded.value);
    }

    /**
     * WRITE: update DB first, then invalidate cache (or update it).
     * This avoids serving stale data.
     */
    public void updateUser(User user) {
      repo.save(user);

      // Option A (safer & common): invalidate so next read loads fresh
      cache.remove(user.id());

      // Option B (faster reads): update cache with fresh value + TTL
      // long now = System.currentTimeMillis();
      // cache.put(user.id(), new CacheEntry<>(user, now + ttlMillis));
    }

    public void deleteUser(String id) {
      repo.deleteById(id);
      cache.remove(id);
    }
  }
}

