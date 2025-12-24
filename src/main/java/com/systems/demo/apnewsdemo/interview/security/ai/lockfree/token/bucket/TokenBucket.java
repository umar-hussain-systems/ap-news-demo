package com.systems.demo.apnewsdemo.interview.security.ai.lockfree.token.bucket;

public class TokenBucket {

  private final int capacity;
  private final int refillRatePerSecond;
  private double currentTokens;
  private long lastRefillTimestamp;

  public TokenBucket(int capacity, int refillRatePerSecond) {


    if(capacity <=  0) {
      throw new IllegalArgumentException("capacity must be greater than zero");
    }

    if(refillRatePerSecond <= 0) {
      throw new IllegalArgumentException("refillRatePerSecond must be greater than zero");
    }

    this.capacity = capacity;
    this.refillRatePerSecond = refillRatePerSecond;
    this.currentTokens = capacity;
    this.lastRefillTimestamp = System.nanoTime();


  }

  private void refill() {
    long elapsedTime = System.nanoTime() - lastRefillTimestamp;
    long elapsedSeconds = elapsedTime / 1000000000;

    if(elapsedSeconds <= 0) {
      return;
    }

    long refilledToken = refillRatePerSecond * elapsedSeconds;
    double newTokenCount = currentTokens + refilledToken;
    if(newTokenCount > capacity) {
      newTokenCount = capacity;
    }
    currentTokens = newTokenCount;

    lastRefillTimestamp = System.nanoTime();

  }

  public synchronized boolean allowRequest() {
    refill();
    if(currentTokens >0){
         currentTokens--;
         return true;
       }
       return false;
  }


}

class Driver {
  public static void main(String[] args) {
    TokenBucket tokenBucket = new TokenBucket(5, 10);

    for (int i = 0; i < 10; i++) {
      System.out.println(tokenBucket.allowRequest());
    }
  }
}