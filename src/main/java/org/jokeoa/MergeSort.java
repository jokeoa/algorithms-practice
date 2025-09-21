package org.jokeoa;
public class MergeSort {

    /**
     * @param left begin of left side
     * @param middle end of left side
     * @param right end of the right side
     * @param context context with all data
     */
    public static void merge(MergeSortContext context, int left, int middle, int right) {

        int[] buffer = context.getBuffer();
        int[] array = context.getArray();

        int leftSize = middle - left + 1;

        for (int i = 0; i < leftSize; i++) {
            buffer[i] = array[left + i];
            context.recordArrayAccess();
        }

        int i = 0;
        int j = middle + 1;
        int k = left;

        while (i < leftSize && j <= right) {
            context.recordComparison();
            if (buffer[i] <= array[j]) {
                array[k] = buffer[i];
                i++;
                context.recordArrayAccess();
            } else {
                array[k] = array[j];
                j++;
                context.recordArrayAccess();
            }
            k++;
        }

        while (i<leftSize){
            array[k] = buffer[i];
            context.recordArrayAccess();
            i++;
            k++;
        }
    }

    /**
     * @param left left boundary
     * @param right right boundary
     * @param context context with all data
     */
    public static void mergeSort(MergeSortContext context, int left, int right) {
        context.enterRecursion();
        if (left < right) {
            if (context.shouldUseCutoff(left, right)) {
                InsertionSort.sort(context, left, right);
            } else {
                int middle = left+(right-left)/2;

                mergeSort(context, left, middle);
                mergeSort(context, middle+1, right);

                merge(context, left, middle, right);
            }
        }
        context.exitRecursion();
    }
    public static void sort(int[] array) {
        if (array.length > 1) {
            MergeSortContext context = new MergeSortContext(array);
            mergeSort(context, 0, array.length - 1);
        }
    }
    /**
     * Sorts the array and returns the collected metrics.
     * @param array array to sort
     * @return collected metrics
     */
    public static SortMetrics sortWithMetrics(int[] array) {
        SortMetrics metrics = new SortMetrics();
        metrics.reset();
        metrics.startTiming();

        if (array.length > 1) {
            MergeSortContext context = new MergeSortContext(array, metrics);
            mergeSort(context, 0, array.length - 1);
        }

        metrics.endTiming();
        return metrics;
    }
    /**
     * Sorts the array and fills the provided metrics object.
     * @param array array to sort
     * @param metrics metrics collector to fill
     */
    public static void sort(int[] array, SortMetrics metrics) {
        metrics.reset();
        metrics.startTiming();

        if (array.length > 1) {
            MergeSortContext context = new MergeSortContext(array, metrics);
            mergeSort(context, 0, array.length - 1);
        }

        metrics.endTiming();
    }
}
