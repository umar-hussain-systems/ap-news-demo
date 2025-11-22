package com.systems.demo.apnewsdemo.problem.solving;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AddingKElementsToLast {
    public static void moveKElementsToLast(int[]array,int k) {
        int n = array.length-1;
        k = k%n;
        if(k==0) {
            return;
        }
        reverse(array,0, k);
        reverse(array,k+1,n);
        reverse(array,0,n);
    }

    public static void reverse(int[] array,int k,int n) {
        while (k < n) {
            int temp = array[k];
            array[k] = array[n];
            array[n] = temp;
            k++;
            n--;
        }
    }

    public static void main(String[] args) {
        int [] array = {0,1,2,3,3,4,4,5};
        log.info("current array: "+ Arrays.toString(array));
        moveKElementsToLast(array,4);
        log.info("swapped array: " + Arrays.toString(array));
    }

}