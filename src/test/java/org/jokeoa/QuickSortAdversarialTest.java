package org.jokeoa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Special "adversarial" tests for QuickSort - cases that can
 * reveal implementation problems or show worst performance.
 */
public class QuickSortAdversarialTest {

    @Test
    @DisplayName("Classic worst-case: sorted array")
    void testWorstCaseSorted() {

        int[] array = generateSortedArray(1000);

        long startTime = System.nanoTime();
        SortMetrics metrics = QuickSort.sortWithMetrics(array);
        long endTime = System.nanoTime();


        double timeMs = (endTime - startTime) / 1_000_000.0;
        assertTrue(timeMs < 100, "Sorting took too much time: " + timeMs + " ms");

        int maxAllowedDepth = (int)(3 * Math.log(1000) / Math.log(2)) + 20;
        assertTrue(metrics.getMaxRecursionDepth() < maxAllowedDepth,
                "Recursion depth too large: " + metrics.getMaxRecursionDepth());

        assertTrue(isSorted(array));

        System.out.println("Worst-case test (sorted array):");
        System.out.println("  Time: " + timeMs + " ms");
        System.out.println("  Recursion depth: " + metrics.getMaxRecursionDepth());
        System.out.println("  Average pivot balance: " + metrics.getAveragePartitionBalance() + "%");
    }

    @Test
    @DisplayName("All elements identical - duplicate test")
    void testAllIdentical() {
        int[] sizes = {100, 500, 1000};

        for (int size : sizes) {
            int[] array = new int[size];
            Arrays.fill(array, 42);

            SortMetrics metrics = QuickSort.sortWithMetrics(array);


            assertTrue(isSorted(array));

            int maxDepth = (int)(2 * Math.log(size) / Math.log(2)) + 10;
            assertTrue(metrics.getMaxRecursionDepth() <= maxDepth,
                    String.format("Size %d: depth %d > expected %d",
                            size, metrics.getMaxRecursionDepth(), maxDepth));
        }
    }

    @Test
    @DisplayName("Organ pipe pattern - decreasing, then increasing")
    void testOrganPipePattern() {
        int[] array = generateOrganPipeArray(200);

        SortMetrics metrics = QuickSort.sortWithMetrics(array);

        assertTrue(isSorted(array));

        System.out.println("Organ pipe test:");
        System.out.println("  Recursion depth: " + metrics.getMaxRecursionDepth());
        System.out.println("  Number of comparisons: " + metrics.getTotalComparisons());
        System.out.println("  Average pivot balance: " + metrics.getAveragePartitionBalance() + "%");
    }

    @Test
    @DisplayName("Many repeating values - stability test")
    void testManyDuplicatesStability() {
        int[] array = generateArrayWithFewUniqueValues(1000, 10);

        SortMetrics metrics = QuickSort.sortWithMetrics(array);

        assertTrue(isSorted(array));

        System.out.println("Multiple duplicates test:");
        System.out.println("  Execution time: " + metrics.getExecutionTimeMs() + " ms");
        System.out.println("  Partition calls: " + metrics.getPartitionCalls());
        System.out.println("  Worst partition: " + metrics.getWorstPartition() + "% deviation");
    }

    @Test
    @DisplayName("Array with 'bad' pivot choices")
    void testBadPivotScenarios() {

        int[] array = createBadPivotArray(500);

        SortMetrics metrics = QuickSort.sortWithMetrics(array);

        assertTrue(isSorted(array));

        assertTrue(metrics.getExecutionTimeMs() < 50,
                "Execution time too large: " + metrics.getExecutionTimeMs() + " ms");

        System.out.println("Bad pivot test:");
        System.out.println("  Average balance: " + metrics.getAveragePartitionBalance() + "%");
        System.out.println("  Best partition: " + metrics.getBestPartition() + "%");
        System.out.println("  Worst partition: " + metrics.getWorstPartition() + "%");
    }

    @Test
    @DisplayName("Stress test: many small arrays")
    void testManySmallArrays() {

        int totalTime = 0;
        int totalTests = 1000;

        for (int i = 0; i < totalTests; i++) {
            int size = 2 + i % 15;
            int[] array = generateRandomArray(size);

            long start = System.nanoTime();
            QuickSort.sort(array);
            long end = System.nanoTime();

            totalTime += (end - start);

            assertTrue(isSorted(array), "Array " + i + " of size " + size + " not sorted");
        }

        double avgTimeNs = totalTime / (double) totalTests;
        System.out.println("Small arrays stress test:");
        System.out.println("  Average time per array: " + avgTimeNs / 1000 + " microseconds");
        System.out.println("  Total tested: " + totalTests + " arrays");
    }

    @Test
    @DisplayName("Extremely large array (if memory allows)")
    void testVeryLargeArray() {
        try {
            int size = 100_000;
            int[] array = generateRandomArray(size);

            long start = System.currentTimeMillis();
            SortMetrics metrics = QuickSort.sortWithMetrics(array);
            long end = System.currentTimeMillis();

            assertTrue(isSorted(array));

            System.out.println("Large array test (100K elements):");
            System.out.println("  Time: " + (end - start) + " ms");
            System.out.println("  Recursion depth: " + metrics.getMaxRecursionDepth());
            System.out.println("  Comparisons: " + metrics.getTotalComparisons());
            System.out.println("  Swaps: " + metrics.getTotalSwaps());

        } catch (OutOfMemoryError e) {
            System.out.println("Insufficient memory for large array test");
        }
    }


    private int[] generateSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
        }
        return array;
    }

    private int[] generateOrganPipeArray(int size) {
        int[] array = new int[size];
        int mid = size / 2;

        // Decreasing to middle
        for (int i = 0; i < mid; i++) {
            array[i] = mid - i;
        }

        // Increasing from middle
        for (int i = mid; i < size; i++) {
            array[i] = i - mid + 1;
        }

        return array;
    }

    private int[] generateArrayWithFewUniqueValues(int size, int uniqueCount) {
        int[] array = new int[size];
        java.util.Random random = new java.util.Random(42);

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(uniqueCount);
        }

        return array;
    }

    private int[] createBadPivotArray(int size) {
        int[] array = new int[size];


        for (int i = 0; i < size * 0.9; i++) {
            array[i] = i % 10;
        }

        for (int i = (int)(size * 0.9); i < size; i++) {
            array[i] = 1000 + i;
        }


        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < size; i++) {
            int j = random.nextInt(size);
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }

        return array;
    }

    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 2);
        }
        return array;
    }

    private boolean isSorted(int[] array) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] < array[i-1]) {
                return false;
            }
        }
        return true;
    }
}