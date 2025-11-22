package com.systems.demo.apnewsdemo.round.robin;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleRoundRobinAlgorithm {
    private List<String> servers = new ArrayList<>();
    private int currentServerIndex = 0;
    SimpleRoundRobinAlgorithm(List<String> servers) {
      this.servers = servers;
    }

    public String getNextServerIndex(){
      String server = servers.get(currentServerIndex);
      currentServerIndex = (currentServerIndex + 1) % servers.size();
      return server;
    }
}

class DriverClass {
  public static void main(String[] args) {
    List<String> servers = new ArrayList<>();
    servers.add("localhost");
    servers.add("127.0.0.1");
    servers.add("127.0.0.2");
    servers.add("127.0.0.3");

    SimpleRoundRobinAlgorithm algorithm = new SimpleRoundRobinAlgorithm(servers);

    for (int i = 0; i < 10; i++) {
      System.out.println(algorithm.getNextServerIndex());
    }
  }
}