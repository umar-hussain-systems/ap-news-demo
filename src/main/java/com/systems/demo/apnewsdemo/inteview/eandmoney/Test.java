package com.systems.demo.apnewsdemo.inteview.eandmoney;
import java.util.HashMap;
import java.util.Map;
public class Test {

    public static int[] pairOfSum(int[] array,int result) {
        Map<Integer,Integer> map = new HashMap<>();
        int[] resultArray = new int[2];
        for(int i =0; i<array.length; i++) {
            if(map.containsKey(array[i])) {

                  resultArray[0] = array[i];
                  resultArray[1] = map.get(array[i]);
                  return resultArray;
            }
            map.put(result-array[i],array[i]);
        }
        return resultArray;
    }


    public static void main(String[] args) {
        int [] array = {3, 8, 1, 9, 10, 23, 50, 34, 78, 54, 12, 67};
       int [] result = pairOfSum(array,1000);
        System.out.println(result[0] +","+ result[1]);
    }

}
