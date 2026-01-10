package com.systems.demo.apnewsdemo.service;

import com.systems.demo.apnewsdemo.service.impl.concurrency.UserSessionServiceImpl.SessionState;
import com.systems.demo.apnewsdemo.service.impl.concurrency.UserSessionServiceImpl.SimulationResult;
import java.util.List;

public interface UserSessionService {

  // this is adding a record not a class when updating value
  // so old.requestCount() + 1 is safe other wise it
  // will have to be atomicInteger to make the mutation thread safe
  SessionState touch(String userId);

  SessionState get(String userId);

  List<SessionState> snapshot();

  // Simulation logic here
  SimulationResult simulateConcurrentTouches(String userId, int requests)
      throws InterruptedException;
}
