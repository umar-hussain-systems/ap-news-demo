package com.systems.demo.apnewsdemo.streams;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StreamExamples {

  Predicate<Integer> isEven = x -> x % 2 == 0;
  Function<String, Integer> len = s -> s.length();
  Consumer<String> print = s -> System.out.println(s);
  Supplier<Long> now = () -> System.currentTimeMillis();



}
