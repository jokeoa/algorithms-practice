package org.jokeoa;

public class InsertionSort {

    /**
     * @param context context with array and metrics
     * @param left segment start (inclusive)
     * @param right segment end (inclusive)
     */
    public static void sort(SortContext context, int left, int right) {
        int[] array = context.getArray();

        for (int i = left + 1; i <= right; i++) {
            int key = array[i];
            context.recordArrayAccess();

            int j = i - 1;


            while (j >= left && array[j] > key) {
                context.recordComparison();
                context.recordArrayAccess();

                array[j + 1] = array[j];
                context.recordArrayAccess();

                j--;
            }

            if (j >= left) {
                context.recordComparison();
                context.recordArrayAccess();
            }

            array[j + 1] = key;
            context.recordArrayAccess();
        }
    }

    /**
     * Simple version without metrics for testing
     */
    public static void sort(int[] array, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = array[i];
            int j = i - 1;

            while (j >= left && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = key;
        }
    }

    /**
     * Sort the entire array
     */
    public static void sort(int[] array) {
        sort(array, 0, array.length - 1);
    }
}