package org.jokeoa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;

public class QuickSortTest {

    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random(42);
    }



    @Test
    @DisplayName("Empty array")
    void testEmptyArray() {
        int[] array = {};
        QuickSort.sort(array);
        assertEquals(0, array.length);
    }

    @Test
    @DisplayName("Single element array")
    void testSingleElement() {
        int[] array = {42};
        QuickSort.sort(array);
        assertArrayEquals(new int[]{42}, array);
    }

    @Test
    @DisplayName("Two elements - correct order")
    void testTwoElementsCorrect() {
        int[] array = {1, 2};
        QuickSort.sort(array);
        assertArrayEquals(new int[]{1, 2}, array);
    }

    @Test
    @DisplayName("Two elements - incorrect order")
    void testTwoElementsIncorrect() {
        int[] array = {2, 1};
        QuickSort.sort(array);
        assertArrayEquals(new int[]{1, 2}, array);
    }

    @Test
    @DisplayName("Basic sorting")
    void testBasicSorting() {
        int[] array = {64, 34, 25, 12, 22, 11, 90};
        int[] expected = {11, 12, 22, 25, 34, 64, 90};

        QuickSort.sort(array);
        assertArrayEquals(expected, array);
    }


    @Test
    @DisplayName("Already sorted array")
    void testAlreadySorted() {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] expected = array.clone();

        QuickSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    @DisplayName("Reverse sorted array")
    void testReverseSorted() {
        int[] array = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        QuickSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    @DisplayName("All elements the same")
    void testAllSameElements() {
        int[] array = {5, 5, 5, 5, 5, 5, 5};
        int[] expected = {5, 5, 5, 5, 5, 5, 5};

        QuickSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    @DisplayName("Many duplicates")
    void testManyDuplicates() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 8, 9, 7, 9};
        int[] expected = {1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 7, 8, 9, 9, 9};

        QuickSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    @DisplayName("Negative numbers")
    void testNegativeNumbers() {
        int[] array = {3, -1, 4, -5, 0, 2, -3};
        int[] expected = {-5, -3, -1, 0, 2, 3, 4};

        QuickSort.sort(array);
        assertArrayEquals(expected, array);
    }


    @RepeatedTest(10)
    @DisplayName("Random arrays (repeated test)")
    void testRandomArrays() {
        int size = 100 + random.nextInt(900);
        int[] array = generateRandomArray(size);
        int[] reference = array.clone();

        QuickSort.sort(array);
        Arrays.sort(reference);

        assertArrayEquals(reference, array,
                "Array of size " + size + " sorted incorrectly");
    }

    @Test
    @DisplayName("Large random array")
    void testLargeRandomArray() {
        int[] array = generateRandomArray(10000);
        int[] reference = array.clone();

        QuickSort.sort(array);
        Arrays.sort(reference);

        assertArrayEquals(reference, array);
    }

    @Test
    @DisplayName("Extreme values")
    void testExtremeValues() {
        int[] array = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
        int[] expected = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};

        QuickSort.sort(array);
        assertArrayEquals(expected, array);
    }


    @Test
    @DisplayName("Recursion depth check on random data")
    void testRecursionDepthRandom() {
        int[] sizes = {100, 500, 1000, 5000};

        for (int size : sizes) {
            int[] array = generateRandomArray(size);
            SortMetrics metrics = QuickSort.sortWithMetrics(array);


            int expectedMaxDepth = (int)(2 * Math.log(size) / Math.log(2)) + 5;

            assertTrue(metrics.getMaxRecursionDepth() <= expectedMaxDepth,
                    String.format("Recursion depth %d exceeds expected %d for size %d",
                            metrics.getMaxRecursionDepth(), expectedMaxDepth, size));

            assertTrue(isSorted(array), "Array must be sorted");
        }
    }

    @Test
    @DisplayName("Recursion depth check on sorted data")
    void testRecursionDepthSorted() {
        int[] sizes = {100, 500, 1000};

        for (int size : sizes) {
            int[] array = generateSortedArray(size);
            SortMetrics metrics = QuickSort.sortWithMetrics(array);


            int expectedMaxDepth = (int)(3 * Math.log(size) / Math.log(2)) + 10;

            assertTrue(metrics.getMaxRecursionDepth() <= expectedMaxDepth,
                    String.format("Recursion depth %d exceeds expected %d for sorted array of size %d",
                            metrics.getMaxRecursionDepth(), expectedMaxDepth, size));
        }
    }

    @Test
    @DisplayName("Pivot quality analysis")
    void testPivotQuality() {
        int[] array = generateRandomArray(1000);
        SortMetrics metrics = QuickSort.sortWithMetrics(array);

        assertTrue(metrics.getAveragePartitionBalance() < 80.0,
                "Average pivot balance too poor: " + metrics.getAveragePartitionBalance() + "%");


        assertTrue(metrics.getPartitionCalls() > 0, "There should be partition calls");


        assertTrue(metrics.getBestPartition() < 50,
                "Best partition too poor: " + metrics.getBestPartition() + "%");

        assertTrue(isSorted(array), "Array must be sorted");
    }



    @Test
    @DisplayName("Comparison with Arrays.sort() on 100 random arrays")
    void testAgainstArraysSort() {
        for (int test = 0; test < 100; test++) {
            int size = 10 + random.nextInt(100);
            int[] array = generateRandomArray(size);
            int[] reference = array.clone();

            QuickSort.sort(array);
            Arrays.sort(reference);

            assertArrayEquals(reference, array,
                    "Test " + test + ", size " + size + ": result does not match Arrays.sort()");
        }
    }


    @Test
    @DisplayName("Cutoff optimization test")
    void testCutoffOptimization() {

        int[] array = {5, 2, 8, 1, 9, 3};
        SortMetrics metrics = QuickSort.sortWithMetrics(array);

        assertTrue(isSorted(array), "Array must be sorted even with cutoff");
        assertTrue(metrics.getMaxRecursionDepth() > 0, "There should be at least one recursion");
    }


    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 2) - size; // numbers from -size to +size
        }
        return array;
    }

    private int[] generateSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = i;
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


    @Test
    @DisplayName("Metrics demonstration")
    void demonstrateMetrics() {
        System.out.println("\n=== QuickSort Metrics Demonstration ===");

        int[] testCases = {50, 100, 500};

        for (int size : testCases) {
            int[] array = generateRandomArray(size);
            SortMetrics metrics = QuickSort.sortWithMetrics(array);

            System.out.println("\nArray size: " + size);
            System.out.println("Recursion depth: " + metrics.getMaxRecursionDepth());
            System.out.println("Number of comparisons: " + metrics.getTotalComparisons());
            System.out.println("Number of swaps: " + metrics.getTotalSwaps());
            System.out.printf("Average pivot balance: %.1f%%\n", metrics.getAveragePartitionBalance());
            System.out.printf("Execution time: %.3f ms\n", metrics.getExecutionTimeMs());

            assertTrue(isSorted(array));
        }
    }
}