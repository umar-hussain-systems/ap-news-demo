package com.systems.demo.apnewsdemo.problem.solving;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
public class SwappingZerosToLeft {

    /***
     * Problem Statement:
     *     given an array, all 0's should be on the left and all 1's to be on the right. Eg. {0, 1, 1, 0, 0,0, 1, 0, 1}
     */


    public static List<Integer> moveZerosToLeft(int [] array) {
        LinkedList<Integer> integers = new LinkedList<>();
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                if(array[i] == 0) {
                    integers.add(array[i]);
                } else {
                    integers.addLast(array[i]);
                }
            }
        }
        return integers;
    }
}
@Slf4j
class Main {

    public static void main(String[] args) {
        int [] array = {0,1,0,1};
        log.info("current array: "+ Arrays.toString(array));
        log.info("swapped array: " + SwappingZerosToLeft.moveZerosToLeft(array));
    }
}
