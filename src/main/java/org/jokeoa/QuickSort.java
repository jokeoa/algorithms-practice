package org.jokeoa;

import java.util.Random;

public class QuickSort {
    private static Random random = new Random();

    /**
     * Partition array around a random pivot
     * @param array array to partition
     * @param left left boundary (inclusive)
     * @param right right boundary (inclusive)
     * @return pivot index after partitioning
     */
    private static int partition(int[] array, int left, int right) {

        int randomIndex = left + random.nextInt(right - left + 1);
        swap(array, randomIndex, right);

        int pivot = array[right];


        int i = left - 1;

        for (int j = left; j < right; j++) {
            if (array[j] <= pivot) {
                i++;
                swap(array, i, j);
            }
        }


        swap(array, i + 1, right);

        return i + 1;
    }

    /**
     * Recursive sorting with "smaller first" optimization
     * @param array array to sort
     * @param left left boundary
     * @param right right boundary
     */
    private static void quickSort(int[] array, int left, int right) {
        while (left < right) {
            int pivotIndex = partition(array, left, right);


            int leftSize = pivotIndex - left;
            int rightSize = right - pivotIndex;

            if (leftSize < rightSize) {
                quickSort(array, left, pivotIndex - 1);
                left = pivotIndex + 1;
            } else {

                quickSort(array, pivotIndex + 1, right);
                right = pivotIndex - 1;
            }
        }
    }

    /**
     * Public sorting method
     */
    public static void sort(int[] array) {
        if (array.length > 1) {
            quickSort(array, 0, array.length - 1);
        }
    }

    /**
     * Swap elements in array
     */
    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}