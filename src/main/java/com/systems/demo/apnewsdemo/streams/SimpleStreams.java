package com.systems.demo.apnewsdemo.streams;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleStreams {

  public static void main(String[] args) {
    List<String> names = new ArrayList<>(List.of("John", "Jane", "Johnson"));
   List<String> capitalName =  names.stream().map(String::toUpperCase).collect(Collectors.toList());
  }
}
