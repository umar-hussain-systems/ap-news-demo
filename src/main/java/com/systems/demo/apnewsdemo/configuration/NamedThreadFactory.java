package com.systems.demo.apnewsdemo.configuration;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {
  private final String prefix;
  private final AtomicInteger n = new AtomicInteger(1);

  public NamedThreadFactory(String prefix) {
    this.prefix = prefix;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r);
    t.setName(prefix + n.getAndIncrement());
    return t;
  }
}


