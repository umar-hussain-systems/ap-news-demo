package com.systems.demo.apnewsdemo.multi.threading.design.pattern;

public class Singleton {

  private static volatile Singleton value;

  private Singleton() {
  }

  public static Singleton getInstance() {

    if(value == null) {
      synchronized (Singleton.class) {
        if(value == null) {
            value = new Singleton();
        }
      }
    }
    return value;
  }

}
