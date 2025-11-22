package com.systems.demo.apnewsdemo.problem.solving;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TwoPairSum {

    public static int[] twoSum(int[] nums, int target) {
        Map<Integer,Integer> map = new HashMap<>();
        int[]result = new int[2];
        for(int i = 0; i < nums.length; i++ ) {
            if(map.containsKey(nums[i])) {
                result[0] = map.get(nums[i]);
                result[1] = i;
            }
            map.put(target-nums[i],i);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] arr = {2,7,11,15};
        log.info(Arrays.toString(twoSum(arr, 9)));
    }

}
