package org.jokeoa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Random;

public class DeterministicSelectTest {

    private Random random;

    @BeforeEach
    void setUp() {
        random = new Random(42);
    }

    @Test
    @DisplayName("Single element array")
    void testSingleElement() {
        int[] array = {42};
        int result = DeterministicSelect.select(array, 1);
        assertEquals(42, result);
    }

    @Test
    @DisplayName("Two elements - first")
    void testTwoElementsFirst() {
        int[] array = {2, 1};
        int result = DeterministicSelect.select(array, 1);
        assertEquals(1, result);
    }

    @Test
    @DisplayName("Two elements - second")
    void testTwoElementsSecond() {
        int[] array = {2, 1};
        int result = DeterministicSelect.select(array, 2);
        assertEquals(2, result);
    }

    @Test
    @DisplayName("Basic selection - minimum")
    void testBasicSelectionMin() {
        int[] array = {64, 34, 25, 12, 22, 11, 90};
        int result = DeterministicSelect.select(array, 1);
        assertEquals(11, result);
    }

    @Test
    @DisplayName("Basic selection - maximum")
    void testBasicSelectionMax() {
        int[] array = {64, 34, 25, 12, 22, 11, 90};
        int result = DeterministicSelect.select(array, 7);
        assertEquals(90, result);
    }

    @Test
    @DisplayName("Basic selection - median")
    void testBasicSelectionMedian() {
        int[] array = {64, 34, 25, 12, 22, 11, 90};
        int result = DeterministicSelect.select(array, 4);
        assertEquals(25, result);
    }

    @Test
    @DisplayName("All elements the same")
    void testAllSameElements() {
        int[] array = {5, 5, 5, 5, 5, 5, 5};
        for (int k = 1; k <= array.length; k++) {
            int result = DeterministicSelect.select(array, k);
            assertEquals(5, result, "Failed for k=" + k);
        }
    }

    @Test
    @DisplayName("Many duplicates")
    void testManyDuplicates() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5, 3, 5, 8, 9, 7, 9};
        int[] sorted = array.clone();
        Arrays.sort(sorted);

        for (int k = 1; k <= array.length; k++) {
            int result = DeterministicSelect.select(array, k);
            assertEquals(sorted[k-1], result, "Failed for k=" + k);
        }
    }

    @Test
    @DisplayName("Negative numbers")
    void testNegativeNumbers() {
        int[] array = {3, -1, 4, -5, 0, 2, -3};
        int[] sorted = {-5, -3, -1, 0, 2, 3, 4};

        for (int k = 1; k <= array.length; k++) {
            int result = DeterministicSelect.select(array, k);
            assertEquals(sorted[k-1], result, "Failed for k=" + k);
        }
    }

    @Test
    @DisplayName("Large array selection")
    void testLargeArraySelection() {
        int[] array = generateRandomArray(1000);
        int[] sorted = array.clone();
        Arrays.sort(sorted);

        int[] testPositions = {1, 10, 100, 500, 900, 1000};
        for (int k : testPositions) {
            int result = DeterministicSelect.select(array, k);
            assertEquals(sorted[k-1], result, "Failed for k=" + k + " in large array");
        }
    }

    @Test
    @DisplayName("Extreme values")
    void testExtremeValues() {
        int[] array = {Integer.MAX_VALUE, Integer.MIN_VALUE, 0, -1, 1};
        int[] sorted = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE};

        for (int k = 1; k <= array.length; k++) {
            int result = DeterministicSelect.select(array, k);
            assertEquals(sorted[k-1], result, "Failed for k=" + k);
        }
    }

    @RepeatedTest(20)
    @DisplayName("Random arrays (repeated test)")
    void testRandomArrays() {
        int size = 50 + random.nextInt(200);
        int[] array = generateRandomArray(size);
        int[] sorted = array.clone();
        Arrays.sort(sorted);

        int k = 1 + random.nextInt(size);
        int result = DeterministicSelect.select(array, k);
        assertEquals(sorted[k-1], result,
                "Array of size " + size + " failed for k=" + k);
    }

    @Test
    @DisplayName("Select all positions in random array")
    void testSelectAllPositions() {
        int[] array = generateRandomArray(50);
        int[] sorted = array.clone();
        Arrays.sort(sorted);

        for (int k = 1; k <= array.length; k++) {
            int result = DeterministicSelect.select(array, k);
            assertEquals(sorted[k-1], result, "Failed for k=" + k);
        }
    }

    @Test
    @DisplayName("Array remains unchanged after selection")
    void testArrayUnchanged() {
        int[] array = {64, 34, 25, 12, 22, 11, 90};
        int[] original = array.clone();

        DeterministicSelect.select(array, 4);

        assertArrayEquals(original, array, "Original array should remain unchanged");
    }

    @Test
    @DisplayName("Invalid input parameters")
    void testInvalidInputParameters() {
        int[] array = {1, 2, 3, 4, 5};

        assertThrows(IllegalArgumentException.class, () ->
            DeterministicSelect.select(null, 1), "Should throw for null array");

        assertThrows(IllegalArgumentException.class, () ->
            DeterministicSelect.select(new int[]{}, 1), "Should throw for empty array");

        assertThrows(IllegalArgumentException.class, () ->
            DeterministicSelect.select(array, 0), "Should throw for k=0");

        assertThrows(IllegalArgumentException.class, () ->
            DeterministicSelect.select(array, 6), "Should throw for k > array length");

        assertThrows(IllegalArgumentException.class, () ->
            DeterministicSelect.select(array, -1), "Should throw for negative k");
    }

    @Test
    @DisplayName("Performance comparison with sorting")
    void testPerformanceComparison() {
        int[] sizes = {100, 500, 1000, 5000};

        for (int size : sizes) {
            int[] array = generateRandomArray(size);
            int k = size / 2;

            long startTime = System.nanoTime();
            int selectResult = DeterministicSelect.select(array, k);
            long selectTime = System.nanoTime() - startTime;

            int[] sortArray = array.clone();
            startTime = System.nanoTime();
            Arrays.sort(sortArray);
            int sortResult = sortArray[k-1];
            long sortTime = System.nanoTime() - startTime;

            assertEquals(sortResult, selectResult, "Results should match for size " + size);

            double selectMs = selectTime / 1_000_000.0;
            double sortMs = sortTime / 1_000_000.0;

            System.out.printf("Size %d: Select %.3fms vs Sort %.3fms (ratio: %.2fx)\n",
                size, selectMs, sortMs, sortMs / selectMs);
        }
    }

    @Test
    @DisplayName("Median-of-medians worst case (groups of 5)")
    void testMedianOfMediansWorstCase() {
        int[] array = new int[25];
        for (int i = 0; i < 25; i++) {
            array[i] = i + 1;
        }

        int median = DeterministicSelect.select(array, 13);
        assertEquals(13, median, "Median of 1..25 should be 13");

        int min = DeterministicSelect.select(array, 1);
        assertEquals(1, min, "Minimum should be 1");

        int max = DeterministicSelect.select(array, 25);
        assertEquals(25, max, "Maximum should be 25");
    }

    @Test
    @DisplayName("Linear time complexity verification")
    void testLinearTimeComplexity() {
        System.out.println("\n=== Deterministic Select Time Complexity Analysis ===");

        int[] sizes = {100, 200, 500, 1000, 2000, 5000};

        for (int size : sizes) {
            int[] array = generateRandomArray(size);
            int k = size / 2;

            SortMetrics metrics = new SortMetrics();
            long startTime = System.nanoTime();
            int result = DeterministicSelect.selectWithMetrics(array, k, metrics);
            long endTime = System.nanoTime();

            double timeMs = (endTime - startTime) / 1_000_000.0;
            double timePerElement = timeMs / size;

            System.out.printf("Size %d: %.3fms (%.6fms/element), depth: %d, comparisons: %d\n",
                size, timeMs, timePerElement, metrics.getMaxRecursionDepth(), metrics.getTotalComparisons());

            int[] sorted = array.clone();
            Arrays.sort(sorted);
            assertEquals(sorted[k-1], result, "Result should be correct for size " + size);
        }
    }

    @Test
    @DisplayName("Recursion depth analysis")
    void testRecursionDepthAnalysis() {
        int[] sizes = {100, 500, 1000, 5000};

        for (int size : sizes) {
            int[] array = generateRandomArray(size);
            int k = size / 2;

            SortMetrics metrics = new SortMetrics();
            DeterministicSelect.selectWithMetrics(array, k, metrics);

            int expectedMaxDepth = (int)(Math.log(size) / Math.log(2)) + 10;

            assertTrue(metrics.getMaxRecursionDepth() <= expectedMaxDepth,
                    String.format("Recursion depth %d should be reasonable for size %d (expected ≤ %d)",
                            metrics.getMaxRecursionDepth(), size, expectedMaxDepth));

            System.out.printf("Size %d: Recursion depth %d (log₂(n)+10 = %d)\n",
                size, metrics.getMaxRecursionDepth(), expectedMaxDepth);
        }
    }

    @Test
    @DisplayName("Metrics demonstration")
    void demonstrateMetrics() {
        System.out.println("\n=== Deterministic Select Metrics Demonstration ===");

        int[] testCases = {50, 100, 500, 1000};

        for (int size : testCases) {
            int[] array = generateRandomArray(size);
            int k = size / 2;

            SortMetrics metrics = new SortMetrics();
            int result = DeterministicSelect.selectWithMetrics(array, k, metrics);

            System.out.println("\nArray size: " + size + ", k: " + k);
            System.out.println("Selected element: " + result);
            System.out.println("Recursion depth: " + metrics.getMaxRecursionDepth());
            System.out.println("Number of comparisons: " + metrics.getTotalComparisons());
            System.out.println("Number of array accesses: " + metrics.getTotalArrayAccesses());
            System.out.println("Number of swaps: " + metrics.getTotalSwaps());
            System.out.printf("Execution time: %.3f ms\n", metrics.getExecutionTimeMs());

            int[] sorted = array.clone();
            Arrays.sort(sorted);
            assertEquals(sorted[k-1], result, "Result should be correct");
        }
    }

    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size * 2) - size;
        }
        return array;
    }
}