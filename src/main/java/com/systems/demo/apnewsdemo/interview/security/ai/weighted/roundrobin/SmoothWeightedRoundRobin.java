package com.systems.demo.apnewsdemo.interview.security.ai.weighted.roundrobin;

import java.util.List;

public class SmoothWeightedRoundRobin {

  static class Server {
    String name;
    int weight;
    int currentWeight = 0;  // dynamic

    Server(String name, int weight) {
      this.name = name;
      this.weight = weight;
    }
  }

  private final List<Server> servers;
  private final int totalWeight;

  public SmoothWeightedRoundRobin(List<Server> servers) {
    if (servers == null || servers.isEmpty()) {
      throw new IllegalArgumentException("Servers list cannot be empty");
    }
    this.servers = servers;

    int sum = 0;
    for (Server s : servers) {
      if (s.weight <= 0) {
        throw new IllegalArgumentException("Weight must be positive: " + s.name);
      }
      sum += s.weight;
    }
    this.totalWeight = sum;
  }

  // synchronized just to make it safe if multiple threads call this demo
  public synchronized String getNextServer() {
    Server best = null;

    // 1) Add each server's weight to its currentWeight
    for (Server s : servers) {
      s.currentWeight = s.currentWeight + s.weight;

      // pick server with max currentWeight
      if (best == null || s.currentWeight > best.currentWeight) {
        best = s;
      }
    }

    // 2) Reduce chosen server by totalWeight so others can catch up
    best.currentWeight = best.currentWeight -  totalWeight;

    return best.name;
  }

  public static void main(String[] args) {
    List<Server> servers = List.of(
        new Server("S1", 4),
        new Server("S2", 1),
        new Server("S3", 1)
    );

    SmoothWeightedRoundRobin lb = new SmoothWeightedRoundRobin(servers);

    for (int i = 0; i < 18; i++) {
      System.out.print(lb.getNextServer() + " ");
    }
    // Example pattern (smooth):
    // S1 S1 S2 S1 S3 S1 S1 S2 S1 S3 ...
  }
}

