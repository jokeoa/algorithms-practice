package org.jokeoa;

import java.util.Random;

public class QuickSort {
    private static Random random = new Random();

    /**
     * Partition array around a random pivot
     * @param left left boundary (inclusive)
     * @param right right boundary (inclusive)
     * @return pivot index after partitioning
     */
    private static int partition(SortContext context, int left, int right) {
        int[] array = context.getArray();

        int randomIndex = left + random.nextInt(right - left + 1);
        swap(context, array, randomIndex, right);

        int pivot = array[right];
        context.recordArrayAccess();


        int i = left - 1;

        for (int j = left; j < right; j++) {
            context.recordArrayAccess();
            context.recordComparison();
            if (array[j] <= pivot) {
                i++;
                swap(context, array, i, j);
            }
        }
        swap(context, array, i + 1, right);

        int pivotIndex = i + 1;
        int leftSize = pivotIndex - left;
        int rightSize = right - pivotIndex;
        context.recordPartition(leftSize, rightSize);

        return pivotIndex;
    }

    /**
     * Recursive sorting with "smaller first" optimization
     * @param left left boundary
     * @param right right boundary
     */
    private static void quickSort(SortContext context, int left, int right) {
        while (left < right) {
            context.recordArrayAccess();

            if (context.shouldUseCutoff(left, right)) {
                InsertionSort.sort(context, left, right);
                context.exitRecursion();
                break;
            }

            int pivotIndex = partition(context, left, right);


            int leftSize = pivotIndex - left;
            int rightSize = right - pivotIndex;

            if (leftSize < rightSize) {
                quickSort(context, left, pivotIndex - 1);
                left = pivotIndex + 1;
            } else {

                quickSort(context, pivotIndex + 1, right);
                right = pivotIndex - 1;
            }
            context.exitRecursion();
        }
    }

    /**
     * Public sorting method
     */
    public static void sort(int[] array) {
        if (array.length > 1) {
            SortContext context = new SortContext(array);
            quickSort(context, 0, array.length - 1);
        }
    }

    /**
     * Swap elements in array
     */
    private static void swap(SortContext context, int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;

        context.recordSwap();
        context.recordArrayAccess();
        context.recordArrayAccess();
        context.recordArrayAccess();
        context.recordArrayAccess();
    }

    public static SortMetrics sortWithMetrics(int[] array) {
        SortMetrics metrics = new SortMetrics();
        metrics.reset();
        metrics.startTiming();

        if (array.length > 1) {
            SortContext context = new SortContext(array, metrics);
            quickSort(context, 0, array.length - 1);
        }

        metrics.endTiming();
        return metrics;
    }
}