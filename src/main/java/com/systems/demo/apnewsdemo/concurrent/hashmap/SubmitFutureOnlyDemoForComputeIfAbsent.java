package com.systems.demo.apnewsdemo.concurrent.hashmap;
import java.util.*;
import java.util.concurrent.*;
public class SubmitFutureOnlyDemoForComputeIfAbsent {

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

    ExecutorService pool = Executors.newFixedThreadPool(4);
    UserProfileService service = new UserProfileService();

    List<String> userIds = List.of("1","2","3","1","2","4","5","3","1","6");

    List<Future<UserProfile>> futures = new ArrayList<>();

    // Submit all tasks
    for (String userId : userIds) {
      Future<UserProfile> f = pool.submit(() -> service.getUserProfile(userId));
      futures.add(f);
    }

    // Wait for all tasks + collect results
    for (Future<UserProfile> f : futures) {
      try {
        UserProfile profile = f.get();   // blocks main until this task finishes
        System.out.println("MAIN got: " + profile);
      } catch (ExecutionException e) {
        System.err.println("Task failed: " + e.getCause());
      }
    }

    pool.shutdown();
  }
}
