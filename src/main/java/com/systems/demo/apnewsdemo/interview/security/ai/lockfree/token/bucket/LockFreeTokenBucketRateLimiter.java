package com.systems.demo.apnewsdemo.interview.security.ai.lockfree.token.bucket;

import java.util.concurrent.atomic.AtomicLong;

public class LockFreeTokenBucketRateLimiter {

  // We store tokens as scaled integers to avoid floating point atomics.
  private static final long SCALE = 1_000_000L; // 1 token = 1_000_000 units

  private final long capacityScaled;          // capacity * SCALE
  private final double refillTokensPerSecond; // logical tokens per second

  // currentTokensScaled: 0 .. capacityScaled
  private final AtomicLong currentTokensScaled;

  // last time we applied a refill
  private final AtomicLong lastRefillTimeNanos;

  public LockFreeTokenBucketRateLimiter(long capacity, double refillTokensPerSecond) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("capacity must be > 0");
    }
    if (refillTokensPerSecond <= 0.0) {
      throw new IllegalArgumentException("refillTokensPerSecond must be > 0");
    }

    this.capacityScaled = capacity * SCALE;
    this.refillTokensPerSecond = refillTokensPerSecond;

    this.currentTokensScaled = new AtomicLong(this.capacityScaled);
    this.lastRefillTimeNanos = new AtomicLong(System.nanoTime());
  }

  /**
   * Try to consume 1 token.
   * @return true if allowed, false if rate-limited.
   */
  public boolean allowRequest() {
    long now = System.nanoTime();
    refillTokens(now);

    // Now try to consume exactly 1 token (SCALE units)
    while (true) {
      long current = currentTokensScaled.get();
      if (current < SCALE) {
        // not enough tokens
        return false;
      }

      long updated = current - SCALE;
      if (currentTokensScaled.compareAndSet(current, updated)) {
        return true;
      }
      // else some other thread changed it, retry
    }
  }

  /**
   * Lock-free refill using CAS on lastRefillTimeNanos and currentTokensScaled.
   */
  private void refillTokens(long now) {
    while (true) {
      long lastRefill = lastRefillTimeNanos.get();
      long elapsedNanos = now - lastRefill;

      if (elapsedNanos <= 0) {
        // clock didn't move forward, or someone already updated
        return;
      }

      // Try to "win" the right to perform the refill for this time window
      if (!lastRefillTimeNanos.compareAndSet(lastRefill, now)) {
        // another thread updated lastRefillTimeNanos first, so recompute with new values
        continue;
      }

      // We are the winner: compute how many tokens to add for elapsed time
      double elapsedSeconds = elapsedNanos / 1_000_000_000.0;
      double tokensToAdd = elapsedSeconds * refillTokensPerSecond;

      if (tokensToAdd <= 0.0) {
        return;
      }

      long addScaled = (long) (tokensToAdd * SCALE);
      if (addScaled <= 0L) {
        return;
      }

      // Add tokens to currentTokensScaled, capped at capacityScaled
      while (true) {
        long current = currentTokensScaled.get();
        long newValue = current + addScaled;
        if (newValue > capacityScaled) {
          newValue = capacityScaled;
        }

        if (currentTokensScaled.compareAndSet(current, newValue)) {
          return; // refill done
        }
        // else retry CAS with updated current value
      }
    }
  }

  // for debugging / monitoring
  public double getApproximateTokens() {
    long now = System.nanoTime();
    refillTokens(now);
    long current = currentTokensScaled.get();
    return ((double) current) / SCALE;
  }

  // Simple demo
  public static void main(String[] args) throws InterruptedException {
    // capacity = 5 tokens, refill = 2 tokens/second
    LockFreeTokenBucketRateLimiter limiter =
        new LockFreeTokenBucketRateLimiter(5, 2.0);

    for (int i = 1; i <= 15; i++) {
      boolean allowed = limiter.allowRequest();
      double tokens = limiter.getApproximateTokens();
      System.out.println("Request " + i + " allowed=" + allowed
          + " tokens≈" + String.format("%.3f", tokens));
      Thread.sleep(200); // 200 ms between calls
    }
  }
}

