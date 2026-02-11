package com.systems.demo.apnewsdemo.interview.onic;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

public class NthLargestNumber {

  public static int findNthLargestNumber(Integer [] array,int n){
    Set<Integer> set = new HashSet<Integer>();
    PriorityQueue<Integer>priorityQueue = new PriorityQueue<>();

    for (int i = 0; i < array.length; i++) {
      if(set.contains(array[i])){
        continue;
      }
      set.add(array[i]);
      priorityQueue.offer(array[i]);

      if(priorityQueue.size() > n){
        priorityQueue.poll();
      }

    }
    return priorityQueue.peek();
  }


  public static void main(String[] args) {

  }
}
