package com.systems.demo.apnewsdemo.interview.security.ai.weighted.roundrobin;

import java.util.List;

public class SmoothWeightedRoundRobinDebug {

  static class Server {
    String name;
    int weight;
    int currentWeight = 0;

    Server(String name, int weight) {
      this.name = name;
      this.weight = weight;
    }
  }

  private final List<Server> servers;
  private final int totalWeight;

  public SmoothWeightedRoundRobinDebug(List<Server> servers) {
    if (servers == null || servers.isEmpty()) {
      throw new IllegalArgumentException("Servers list cannot be empty");
    }
    this.servers = servers;

    int sum = 0;
    for (Server s : servers) {
      if (s.weight <= 0) {
        throw new IllegalArgumentException("Weight must be positive");
      }
      sum += s.weight;
    }
    this.totalWeight = sum;
  }

  // synchronized -> thread-safe for demo; remove if not needed
  public synchronized String getNextServerWithLog(int requestNumber) {
    System.out.println("=== Request #" + requestNumber + " ===");

    Server best = null;

    // 1. Increase each server’s currentWeight by its weight
    System.out.println("Before adding weights:");
    printStates();

    for (Server s : servers) {
      s.currentWeight += s.weight;
    }

    System.out.println("After adding weights:");
    printStates();

    // 2. Pick server with largest currentWeight
    for (Server s : servers) {
      if (best == null || s.currentWeight > best.currentWeight) {
        best = s;
      }
    }

    System.out.println("Chosen server: " + best.name + " (currentWeight=" + best.currentWeight + ")");

    // 3. Subtract totalWeight from chosen server
    best.currentWeight -= totalWeight;

    System.out.println("After subtracting totalWeight (" + totalWeight + ") from " + best.name + ":");
    printStates();
    System.out.println();

    return best.name;
  }

  private void printStates() {
    for (Server s : servers) {
      System.out.println("  " + s.name + " -> weight=" + s.weight + ", currentWeight=" + s.currentWeight);
    }
  }

  public static void main(String[] args) {
    SmoothWeightedRoundRobinDebug lb = new SmoothWeightedRoundRobinDebug(
        List.of(
            new Server("S1", 1),
            new Server("S2", 1),
            new Server("S3", 1)
        )
    );

    // Simulate 10 requests
    for (int i = 1; i <= 9; i++) {
      String server = lb.getNextServerWithLog(i);
      System.out.println("Request #" + i + " routed to: " + server);
      System.out.println("-----------------------------------------");
    }
  }
}

