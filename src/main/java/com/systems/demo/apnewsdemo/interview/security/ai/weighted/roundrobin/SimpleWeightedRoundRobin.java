package com.systems.demo.apnewsdemo.interview.security.ai.weighted.roundrobin;

import java.util.ArrayList;
import java.util.List;

public class SimpleWeightedRoundRobin {

  private final List<String> expandedServers;
  private int index = 0;

  public SimpleWeightedRoundRobin(List<String> servers, List<Integer> weights) {
    if (servers == null || servers.isEmpty()) {
      throw new IllegalArgumentException("Servers list cannot be empty");
    }
    if (servers.size() != weights.size()) {
      throw new IllegalArgumentException("Servers and weights size mismatch");
    }

    this.expandedServers = new ArrayList<>();

    // Build expanded list
    for (int i = 0; i < servers.size(); i++) {
      String server = servers.get(i);
      int weight = weights.get(i);
      if (weight <= 0) {
        throw new IllegalArgumentException("Weight must be positive");
      }
      for (int w = 0; w < weight; w++) {
        expandedServers.add(server);
      }
    }

    if (expandedServers.isEmpty()) {
      throw new IllegalArgumentException("No servers after expansion");
    }
  }

  // Not thread-safe; for multi-thread use synchronized or AtomicInteger
  public String getNextServer() {
    String server = expandedServers.get(index);
    index = (index + 1) % expandedServers.size();
    return server;
  }

  public static void main(String[] args) {
    List<String> servers = List.of("S1", "S2", "S3");
    List<Integer> weights = List.of(3, 1, 2); // S1=3, S2=1, S3=2

    SimpleWeightedRoundRobin lb = new SimpleWeightedRoundRobin(servers, weights);

    for (int i = 0; i < 12; i++) {
      System.out.print(lb.getNextServer() + " ");
    }
    // One possible sequence:
    // S1 S1 S1 S2 S3 S3 S1 S1 S1 S2 S3 S3 ...
  }
}

