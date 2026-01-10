package com.systems.demo.apnewsdemo.service;

import com.systems.demo.apnewsdemo.service.impl.concurrency.VisitCounterServiceImpl.SimulationResult;
import java.util.Map;

public interface VisitCounterService {

  int recordVisit(String userId);

  Integer getVisits(String userId);

  Map<String, Integer> snapshot();

  SimulationResult simulateConcurrentVisits(String userId,
      int requests) throws InterruptedException;
}
