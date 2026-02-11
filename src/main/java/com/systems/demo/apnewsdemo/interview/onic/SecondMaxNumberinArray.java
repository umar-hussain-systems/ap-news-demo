package com.systems.demo.apnewsdemo.interview.onic;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SecondMaxNumberinArray {

  ExecutorService executorService = Executors.newFixedThreadPool(10);

  ExecutorService executorService1 = new ThreadPoolExecutor(4,8,30,
      TimeUnit.MILLISECONDS,new ArrayBlockingQueue<>(10),new ThreadPoolExecutor.CallerRunsPolicy());


  public static void main(String[] args) {
    int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9};

    int max = Integer.MIN_VALUE;
    int max2 = Integer.MIN_VALUE;

    for (int i = 0; i < array.length; i++) {
      int v= array[i];
      if(v > max){
       max2 = max;
       max = v;
      }else if(array[i] > max2  && v != max ){
        max2 = array[i];
      }

    }
  }

}
