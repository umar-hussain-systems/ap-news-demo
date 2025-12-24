package com.systems.demo.apnewsdemo.interview.security.ai.lru.cache;

import java.util.HashMap;
import java.util.Map;

public class LRUCache {

  private static class Node {
    int key;
    int value;
    Node prev;
    Node next;

    Node(int key, int value) {
      this.key = key;
      this.value = value;
    }
  }

  private final int capacity;
  private final Map<Integer, Node> map;

  // Real head and tail (can be null when list is empty)
  private Node head; // most recently used
  private Node tail; // least recently used

  public LRUCache(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be > 0");
    }
    this.capacity = capacity;
    this.map = new HashMap<>();
    this.head = null;
    this.tail = null;
  }

  public int get(int key) {
    Node node = map.get(key);
    if (node == null) {
      return -1;
    }
    moveToFront(node);
    return node.value;
  }

  public void put(int key, int value) {
    Node node = map.get(key);

    if (node != null) {
      // Key already exists → update and move to front
      node.value = value;
      moveToFront(node);
    } else {
      // New key
      Node newNode = new Node(key, value);
      map.put(key, newNode);
      addToFront(newNode);

      if (map.size() > capacity) {
        // Evict least recently used (tail)
        if (tail != null) {
          map.remove(tail.key);
          removeNode(tail);
        }
      }
    }
  }

  // ========== Doubly linked list helpers (no dummy nodes) ==========

  // Insert node at front (as new head)
  private void addToFront(Node node) {
    if (head == null) {
      // Empty list
      head = node;
      tail = node;
    } else {
      node.next = head;
      node.prev = null;

      head.prev = node;
      head = node;
    }
  }

  // Remove a node from the list
  private void removeNode(Node node) {
    if (node == null) {
      return;
    }

    // Case 1: only node in the list
    if (node == head && node == tail) {
      head = null;
      tail = null;
      return;
    }

    // Case 2: node is head
    if (node == head) {
      head = head.next;
      if (head != null) {
        head.prev = null;
      }
      node.next = null;
      node.prev = null;
      return;
    }

    // Case 3: node is tail
    if (node == tail) {
      tail = tail.prev;
      if (tail != null) {
        tail.next = null;
      }
      node.next = null;
      node.prev = null;
      return;
    }

    // Case 4: node is in the middle
    Node prevNode = node.prev;
    Node nextNode = node.next;

    if (prevNode != null) {
      prevNode.next = nextNode;
    }
    if (nextNode != null) {
      nextNode.prev = prevNode;
    }

    node.prev = null;
    node.next = null;
  }

  private void moveToFront(Node node) {
    if (node == head) {
      return; // already most recent
    }
    removeNode(node);
    addToFront(node);
  }

  // optional debugging
  public void printState() {
    System.out.print("Cache state (MRU -> LRU): ");
    Node curr = head;
    while (curr != null) {
      System.out.print("[" + curr.key + "=" + curr.value + "] ");
      curr = curr.next;
    }
    System.out.println();
  }

  // small demo
  public static void main(String[] args) {
    LRUCache cache = new LRUCache(2);

    cache.put(1, 10); // [1]
    cache.printState();

    cache.put(2, 20); // [2,1]
    cache.printState();

    System.out.println(cache.get(1)); // 10, [1,2]
    cache.printState();

    cache.put(3, 30); // evict 2, [3,1]
    cache.printState();

    System.out.println(cache.get(2)); // -1

    cache.put(4, 40); // evict 1, [4,3]
    cache.printState();

    System.out.println(cache.get(1)); // -1
    System.out.println(cache.get(3)); // 30
    System.out.println(cache.get(4)); // 40
  }
}


