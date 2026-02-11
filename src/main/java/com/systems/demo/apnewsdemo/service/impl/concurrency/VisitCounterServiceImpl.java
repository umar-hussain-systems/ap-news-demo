package com.systems.demo.apnewsdemo.service.impl.concurrency;

import com.systems.demo.apnewsdemo.service.VisitCounterService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VisitCounterServiceImpl implements VisitCounterService {

  // userId -> visitCount
  private final ConcurrentHashMap<String, Integer> visits = new ConcurrentHashMap<>();
  private final ExecutorService visitExecutor;

  public VisitCounterServiceImpl(@Qualifier("visitExecutor") ExecutorService visitExecutor) {
    this.visitExecutor = visitExecutor;
  }


  @Override
  public int recordVisit(String userId) {
    //written in lamda to remeber the bi-function logic
    // atomic: insert 1 if absent, else add 1
    return visits.merge(userId, 1, (oldValue, newValue) -> oldValue + newValue);
  }



  @Override
  public Integer getVisits(String userId) {
    return visits.getOrDefault(userId, 0);
  }

  @Override
  public Map<String, Integer> snapshot() {
    return Map.copyOf(visits);
  }


  /** ✅ Simulation moved here */
  @Override
  public SimulationResult simulateConcurrentVisits(String userId, int requests) throws InterruptedException {
    List<Future<Integer>> futures = new ArrayList<>(requests);

    for (int i = 0; i < requests; i++) {
      futures.add(visitExecutor.submit(() -> recordVisit(userId)));
    }visitExecutor.execute();

    // Wait for all tasks and surface failures
    for (Future<Integer> f : futures) {
      try {
        f.get();
      } catch (ExecutionException e) {
        throw new RuntimeException("Visit task failed", e.getCause());
      }
    }

    return new SimulationResult(userId, requests, getVisits(userId));
  }

  public record SimulationResult(String userId, int requests, int finalCount) {}
}

