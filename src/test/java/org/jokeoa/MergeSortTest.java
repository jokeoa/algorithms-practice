package org.jokeoa;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.Random;

public class MergeSortTest {

    @Test
    public void testEmptyArray() {
        int[] array = {};
        MergeSort.sort(array);
        assertEquals(0, array.length);
    }

    @Test
    public void testSingleElement() {
        int[] array = {42};
        MergeSort.sort(array);
        assertArrayEquals(new int[]{42}, array);
    }

    @Test
    public void testTwoElements() {
        int[] array = {2, 1};
        MergeSort.sort(array);
        assertArrayEquals(new int[]{1, 2}, array);
    }

    @Test
    public void testBasicSorting() {
        int[] array = {64, 34, 25, 12, 22, 11, 90};
        int[] expected = {11, 12, 22, 25, 34, 64, 90};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testAlreadySorted() {
        int[] array = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testReverseSorted() {
        int[] array = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testDuplicates() {
        int[] array = {3, 1, 4, 1, 5, 9, 2, 6, 5};
        int[] expected = {1, 1, 2, 3, 4, 5, 5, 6, 9};

        MergeSort.sort(array);
        assertArrayEquals(expected, array);
    }

    @Test
    public void testRandomArray() {

        Random random = new Random(42);
        int[] array = new int[100];
        int[] reference = new int[100];

        for (int i = 0; i < array.length; i++) {
            int value = random.nextInt(1000);
            array[i] = value;
            reference[i] = value;
        }

        MergeSort.sort(array);
        Arrays.sort(reference);

        assertArrayEquals(reference, array);
    }
}