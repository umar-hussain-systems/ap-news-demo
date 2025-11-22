package com.systems.demo.apnewsdemo.problem.solving;

public class ZeroOneSort {
    public static void main(String[] args) {
        int[] arr = {0, 1, 1, 0, 0, 0, 1, 0, 1};

        // Call the method to sort 0s and 1s
        sortZeroesAndOnes(arr);

        // Print the result
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }

    public static void sortZeroesAndOnes(int[] arr) {
        int left = 0;                 // Pointer at the start of the array
        int right = arr.length - 1;   // Pointer at the end of the array

        while (left < right) {
            // If the left pointer is already 0, move it to the right
            if (arr[left] == 0) {
                left++;
            }
            // If the right pointer is already 1, move it to the left
            else if (arr[right] == 1) {
                right--;
            }
            // If left is 1 and right is 0, swap them
            else {
                int temp = arr[left];
                arr[left] = arr[right];
                arr[right] = temp;

                // Move both pointers
                left++;
                right--;
            }
        }
    }
}

