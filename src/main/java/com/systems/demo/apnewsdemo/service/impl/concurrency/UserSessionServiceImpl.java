package com.systems.demo.apnewsdemo.service.impl.concurrency;
import com.systems.demo.apnewsdemo.service.UserSessionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserSessionServiceImpl implements UserSessionService {

  private final ConcurrentHashMap<String, SessionState> sessions = new ConcurrentHashMap<>();
  private final ExecutorService sessionExecutor;


  public UserSessionServiceImpl(@Qualifier("sessionExecutor") ExecutorService sessionExecutor) {
    this.sessionExecutor = sessionExecutor;
  }

  // this is adding a record not a class when updating value
  // so old.requestCount() + 1 is safe other wise it
  // will have to be atomicInteger to make the mutation thread safe
  @Override
  public SessionState touch(String userId) {
    return sessions.compute(userId, (id, old) -> {
      if (old == null) {
        return new SessionState(id, 1, Instant.now());
      } else {
        return new SessionState(id, old.requestCount() + 1, Instant.now());
      }
    });
  }

  @Override
  public SessionState get(String userId) {
    return sessions.get(userId);
  }

  @Override
  public List<SessionState> snapshot() {
    return List.copyOf(sessions.values());
  }

  // Simulation logic here
  @Override
  public SimulationResult simulateConcurrentTouches(String userId, int requests)
      throws InterruptedException {

    List<Future<SessionState>> futures = new ArrayList<>(requests);

    for (int i = 0; i < requests; i++) {
      futures.add(sessionExecutor.submit(() -> touch(userId)));
    }

    for (Future<SessionState> f : futures) {
      try {
        f.get();
      } catch (ExecutionException e) {
        throw new RuntimeException("Session task failed", e.getCause());
      }
    }

    SessionState finalState = get(userId);
    return new SimulationResult(userId, requests, finalState.requestCount(), finalState.lastSeen());
  }

  public record SessionState(String userId, int requestCount, Instant lastSeen) {}
  public record SimulationResult(String userId, int requests, int finalCount, Instant lastSeen) {}
}

