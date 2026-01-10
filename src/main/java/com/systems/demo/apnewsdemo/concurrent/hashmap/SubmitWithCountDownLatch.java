package com.systems.demo.apnewsdemo.concurrent.hashmap;
import java.util.*;
import java.util.concurrent.*;

public class SubmitWithCountDownLatch {

  record UserProfile(String userId, String name) {}

  static class UserProfileService {
    private final ConcurrentHashMap<String, UserProfile> cache = new ConcurrentHashMap<>();

    public UserProfile getUserProfile(String userId) {
      return cache.computeIfAbsent(userId, this::dbLoad);
    }

    private UserProfile dbLoad(String userId) {
      System.out.println(Thread.currentThread().getName() + " DB LOAD " + userId);
      try { Thread.sleep(150); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
      return new UserProfile(userId, "User " + userId);
    }
  }

  public static void main(String[] args) throws Exception {

    // this not safe for production example
    ExecutorService pool = Executors.newFixedThreadPool(4);
    UserProfileService service = new UserProfileService();

    List<String> userIds = List.of("1","2","3","1","2","4","5","3","1","6");

    CountDownLatch latch = new CountDownLatch(userIds.size());
    List<Future<UserProfile>> futures = new ArrayList<>();

    for (String userId : userIds) {
      Future<UserProfile> f = pool.submit(() -> {
        try {
          return service.getUserProfile(userId);   // Callable result
        } finally {
          latch.countDown(); // ✅ always decrement, even if exception happens
        }
      });
      futures.add(f);
    }

    // ✅ main waits until ALL tasks called countDown()
    latch.await();

    // (Optional) now safely read results + catch exceptions
    // even if we dont iterate over the list countdown latch makes sure all the thread
    // gets excecuted before pool shut down

    for (Future<UserProfile> f : futures) {
      try {
        UserProfile p = f.get(); // should return immediately now
        System.out.println("MAIN got: " + p);
      } catch (ExecutionException e) {
        System.out.println("Task failed: " + e.getCause());
      }
    }

    pool.shutdown();
  }
}
