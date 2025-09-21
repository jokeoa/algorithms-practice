package org.jokeoa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;

public class MergeSortTest {

    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random(42);
    }


    @Test
    @DisplayName("Empty array")
    void testEmptyArray() {
        int[] array = {};
        MergeSort.sort(array);
        assertEquals(0, array.length);
    }

    @Test
    @DisplayName("Single element array")
    void testSingleElement() {
        int[] array = {42};
        MergeSort.sort(array);
        assertArrayEquals(new int[]{42}, array);
    }

    @Test
    @DisplayName("Two elements - correct order")
    void testTwoElementsCorrect() {
        int[] array = {1, 2};
        MergeSort.sort(array);
        assertArrayEquals(new int[]{1, 2}, array);
    }

    @Test
    @DisplayName("Two elements - incorrect order")
    void testTwoElementsIncorrect() {
        int[] array = {2, 1};
        MergeSort.sort(array);
        assertArrayEquals(new int[]{1, 2}, array);
    }

    @Test
    @DisplayName("Basic sorting")
    void testBasicSorting() {
        int[] array = {64, 34, 25, 12, 22, 11, 90};
        int[] expected = {11, 12, 22, 25, 34, 64, 90};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }


    @Test
    @DisplayName("Already sorted array")
    void testAlreadySorted() {
        int[] array = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] expected = array.clone();

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    @DisplayName("Reverse sorted array")
    void testReverseSorted() {
        int[] array = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    @DisplayName("All same elements")
    void testAllSameElements() {
        int[] array = {5, 5, 5, 5, 5, 5, 5};
        int[] expected = {5, 5, 5, 5, 5, 5, 5};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    @DisplayName("Many duplicates")
    void testManyDuplicates() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 8, 9, 7, 9};
        int[] expected = {1, 1, 2, 3, 3, 4, 5, 5, 5, 6, 7, 8, 9, 9, 9};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    @DisplayName("Negative numbers")
    void testNegativeNumbers() {
        int[] array = {3, -1, 4, -5, 0, 2, -3};
        int[] expected = {-5, -3, -1, 0, 2, 3, 4};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }


    @RepeatedTest(10)
    @DisplayName("Random arrays (repeated test)")
    void testRandomArrays() {
        int size = 100 + random.nextInt(900);
        int[] array = generateRandomArray(size);
        int[] reference = array.clone();

        MergeSort.sort(array);
        Arrays.sort(reference);

        assertArrayEquals(reference, array,
                "Array of size " + size + " sorted incorrectly");
    }

    @Test
    @DisplayName("Large random array")
    void testLargeRandomArray() {
        int[] array = generateRandomArray(10000);
        int[] reference = array.clone();

        MergeSort.sort(array);
        Arrays.sort(reference);

        assertArrayEquals(reference, array);
    }

    @Test
    @DisplayName("Extreme values")
    void testExtremeValues() {
        int[] array = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
        int[] expected = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }


    @Test
    @DisplayName("Check stable recursion depth")
    void testStableRecursionDepth() {
        int[] sizes = {100, 500, 1000, 5000, 10000};

        for (int size : sizes) {
            int[] array = generateRandomArray(size);
            SortMetrics metrics = MergeSort.sortWithMetrics(array);

            int expectedDepth = (int) Math.ceil(Math.log(size) / Math.log(2));
            int maxAllowedDepth = expectedDepth + 2;

            assertTrue(metrics.getMaxRecursionDepth() <= maxAllowedDepth,
                    String.format("Size %d: depth %d > expected %d (theoretical: %d)",
                            size, metrics.getMaxRecursionDepth(), maxAllowedDepth, expectedDepth));

            assertTrue(isSorted(array), "Array should be sorted");

            assertTrue(metrics.getExecutionTimeMs() < size / 10.0,
                    "Execution time suspiciously large: " + metrics.getExecutionTimeMs() + " ms");
        }
    }

    @Test
    @DisplayName("Check reusable buffer functionality")
    void testReusableBuffer() {
        int[] sizes = {32, 64, 128, 256};

        for (int size : sizes) {
            int[] array = generateRandomArray(size);
            SortMetrics metrics = MergeSort.sortWithMetrics(array);

            assertTrue(isSorted(array), "Array of size " + size + " should be sorted");

            int expectedAccesses = (int)(size * Math.log(size) / Math.log(2) * 10);
            assertTrue(metrics.getTotalArrayAccesses() < expectedAccesses,
                    String.format("Size %d: too many array accesses: %d > %d",
                            size, metrics.getTotalArrayAccesses(), expectedAccesses));
        }
    }

    @Test
    @DisplayName("Check cutoff optimization with Insertion Sort")
    void testCutoffOptimization() {
        int[] array = generateRandomArray(50);
        SortMetrics metrics = MergeSort.sortWithMetrics(array);

        assertTrue(isSorted(array), "Array should be sorted with cutoff");

        int theoreticalDepth = (int) Math.ceil(Math.log(50) / Math.log(2));
        assertTrue(metrics.getMaxRecursionDepth() > 0, "Should have recursion");
        assertTrue(metrics.getMaxRecursionDepth() <= theoreticalDepth + 3,
                "Depth should not significantly exceed theoretical");
    }


    @Test
    @DisplayName("Performance test on different data types")
    void testPerformanceOnDifferentDataTypes() {
        int size = 1000;

        int[] randomArray = generateRandomArray(size);
        SortMetrics randomMetrics = MergeSort.sortWithMetrics(randomArray);

        int[] sortedArray = generateSortedArray(size);
        SortMetrics sortedMetrics = MergeSort.sortWithMetrics(sortedArray);

        int[] reverseArray = generateReverseSortedArray(size);
        SortMetrics reverseMetrics = MergeSort.sortWithMetrics(reverseArray);

        assertTrue(isSorted(randomArray));
        assertTrue(isSorted(sortedArray));
        assertTrue(isSorted(reverseArray));

        double maxTime = Math.max(Math.max(randomMetrics.getExecutionTimeMs(),
                        sortedMetrics.getExecutionTimeMs()),
                reverseMetrics.getExecutionTimeMs());
        double minTime = Math.min(Math.min(randomMetrics.getExecutionTimeMs(),
                        sortedMetrics.getExecutionTimeMs()),
                reverseMetrics.getExecutionTimeMs());

        double timeVariation = (maxTime - minTime) / minTime;
        assertTrue(timeVariation < 2.0,
                String.format("Too large execution time variance: %.2fx", timeVariation + 1));

        System.out.println("MergeSort performance test:");
        System.out.printf("  Random:        %.3f ms (depth: %d)\n",
                randomMetrics.getExecutionTimeMs(), randomMetrics.getMaxRecursionDepth());
        System.out.printf("  Sorted:        %.3f ms (depth: %d)\n",
                sortedMetrics.getExecutionTimeMs(), sortedMetrics.getMaxRecursionDepth());
        System.out.printf("  Reverse:       %.3f ms (depth: %d)\n",
                reverseMetrics.getExecutionTimeMs(), reverseMetrics.getMaxRecursionDepth());
    }


    @Test
    @DisplayName("Comparison with Arrays.sort() on 100 random arrays")
    void testAgainstArraysSort() {
        for (int test = 0; test < 100; test++) {
            int size = 10 + random.nextInt(100);
            int[] array = generateRandomArray(size);
            int[] reference = array.clone();

            MergeSort.sort(array);
            Arrays.sort(reference);

            assertArrayEquals(reference, array,
                    "Test " + test + ", size " + size + ": result does not match Arrays.sort()");
        }
    }


    @Test
    @DisplayName("Buffer optimization efficiency test")
    void testBufferEfficiency() {
        int[] array = generateRandomArray(256);
        SortMetrics metrics = MergeSort.sortWithMetrics(array);

        assertTrue(isSorted(array));

        int naiveEstimate = array.length * (int)(Math.log(array.length) / Math.log(2)) * 4;
        assertTrue(metrics.getTotalArrayAccesses() < naiveEstimate,
                "Too many array accesses, buffer optimization might not be working");
    }

    @Test
    @DisplayName("Check different cutoff values")
    void testDifferentCutoffValues() {
        int[] originalArray = generateRandomArray(100);

        int[] cutoffValues = {1, 4, 7, 10, 15, 20};

        for (int cutoff : cutoffValues) {
            int[] array = originalArray.clone();
            SortMetrics metrics = new SortMetrics();
            metrics.reset();
            metrics.startTiming();

            SortContext context = new SortContext(array, metrics, cutoff);
            MergeSort.mergeSort(context, 0, array.length - 1);

            metrics.endTiming();

            assertTrue(isSorted(array), "Array should be sorted with cutoff=" + cutoff);

            assertTrue(metrics.getMaxRecursionDepth() > 0, "Should have recursion");
        }
    }


    @Test
    @DisplayName("Demonstrate metrics and optimizations")
    void demonstrateMetrics() {
        System.out.println("\n=== MergeSort metrics demonstration ===");

        int[] testSizes = {50, 100, 500, 1000};

        for (int size : testSizes) {
            int[] array = generateRandomArray(size);
            SortMetrics metrics = MergeSort.sortWithMetrics(array);

            int theoreticalDepth = (int) Math.ceil(Math.log(size) / Math.log(2));

            System.out.println("\nArray size: " + size);
            System.out.println("Theoretical depth: " + theoreticalDepth);
            System.out.println("Actual depth: " + metrics.getMaxRecursionDepth());
            System.out.println("Number of comparisons: " + metrics.getTotalComparisons());
            System.out.println("Array accesses: " + metrics.getTotalArrayAccesses());
            System.out.printf("Execution time: %.3f ms\n", metrics.getExecutionTimeMs());

            assertTrue(isSorted(array));
        }
    }


    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 2) - size;
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

    private int[] generateReverseSortedArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = size - i;
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