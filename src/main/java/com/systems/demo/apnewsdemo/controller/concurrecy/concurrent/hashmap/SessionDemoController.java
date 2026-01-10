package com.systems.demo.apnewsdemo.controller.concurrecy.concurrent.hashmap;

import com.systems.demo.apnewsdemo.service.UserSessionService;
import com.systems.demo.apnewsdemo.service.impl.concurrency.UserSessionServiceImpl.SessionState;
import com.systems.demo.apnewsdemo.service.impl.concurrency.UserSessionServiceImpl.SimulationResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/demo/sessions")
public class SessionDemoController {

  private final UserSessionService service;

  public SessionDemoController(UserSessionService service) {
    this.service = service;
  }

  @PostMapping("/touch/{userId}")
  public SessionState touch(@PathVariable String userId) {
    return service.touch(userId);
  }

  @PostMapping("/simulate")
  public SimulationResult simulate(
      @RequestParam String userId,
      @RequestParam(defaultValue = "100") int requests
  ) throws InterruptedException {
    return service.simulateConcurrentTouches(userId, requests);
  }

  @GetMapping
  public Object all() {
    return service.snapshot();
  }
}
