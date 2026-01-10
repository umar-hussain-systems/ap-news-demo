package com.systems.demo.apnewsdemo.streams;

import java.util.List;

public class StreamMapAndFlatMapExamples {
  public static void mapExample() {
    List<String> words = List.of("hello", "world");

    // map: String -> Integer (length)
    List<Integer> lengths = words.stream()
        .map(String::length)
        .toList();
// Result: [5, 5]

// map: String -> String (uppercase)
    List<String> upper = words.stream()
        .map(String::toUpperCase)
        .toList();
// Result: ["HELLO", "WORLD"]

  }

  public static void flatMapRealWorldSenario(){
    record Item(String name, double price) {}
    record Order(String id, List<Item> items) {}

    List<Order> orders = List.of(
        new Order("O1", List.of(new Item("Book", 29.99), new Item("Pen", 2.99))),
        new Order("O2", List.of(new Item("Laptop", 999.99)))
    );

// Get all item names across all orders
    List<String> allItemNames = orders.stream()
        .flatMap(order -> order.items().stream())
        .map(Item::name)
        .toList();
// Result: ["Book", "Pen", "Laptop"]

// Calculate total value of all orders
    double totalValue = orders.stream()
        .flatMap(order -> order.items().stream())
        .mapToDouble(Item::price)
        .sum();
// Result: 1032.97
  }

  public static void main(String[] args) {
    flatMapRealWorldSenario();
    mapExample();
  }
}
