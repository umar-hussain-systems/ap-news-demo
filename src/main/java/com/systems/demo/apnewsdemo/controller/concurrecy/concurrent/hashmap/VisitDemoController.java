package com.systems.demo.apnewsdemo.controller.concurrecy.concurrent.hashmap;

import com.systems.demo.apnewsdemo.service.VisitCounterService;
import com.systems.demo.apnewsdemo.service.impl.concurrency.VisitCounterServiceImpl.SimulationResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/demo/visits")
public class VisitDemoController {


  private final VisitCounterService service;

  public VisitDemoController(VisitCounterService service) {
    this.service = service;
  }

  @GetMapping("/visit-count")
  public Integer visitCountPerId(@RequestParam("userId") String userId){
    return service.getVisits(userId);
  }

  /**
   * Simulate many concurrent hits.
   * Example:
   * POST /demo/visits/simulate?userId=1&requests=1000
   */
  @PostMapping("/simulate")
  public SimulationResult simulate(
      @RequestParam String userId,
      @RequestParam(defaultValue = "1000") int requests
  ) throws InterruptedException {
    return service.simulateConcurrentVisits(userId, requests);
  }

  /**
   * See all counts
   * GET /demo/visits
   */
  @GetMapping
  public Map<String, Integer> all() {
    return service.snapshot();
  }
}
