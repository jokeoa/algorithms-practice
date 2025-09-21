package org.jokeoa;
public class MergeSort {

    /**
     *  @param array source array
     * @param left begin of left side
     * @param middle end of left side
     * @param right end of the right side
     * @param metrics metrics collector
     */
    public static void merge(int[] array, int left, int middle, int right, SortMetrics metrics) {
        int leftSize = middle - left + 1;
        int rightSize = right - middle;

        int[] leftArray = new int[leftSize];
        int[] rightArray = new int[rightSize];

        for (int i = 0; i < leftSize; i++) {
            leftArray[i] = array[left + i];
            if (metrics != null) metrics.recordArrayAccess();
        }
        for (int j = 0; j < rightSize; j++) {
            rightArray[j] = array[middle + 1 + j];
            if (metrics != null) metrics.recordArrayAccess();
        }
        int i = 0;
        int j = 0;
        int k = left;
        while (i < leftSize && j < rightSize) {
            if (metrics != null) metrics.recordComparison();
            if (leftArray[i] <= rightArray[j]) {
                array[k] = leftArray[i];
                i++;
                if (metrics != null) metrics.recordArrayAccess();
            } else {
                array[k] = rightArray[j];
                j++;
                if (metrics != null) metrics.recordArrayAccess();
            }
            k++;
        }

        while (i<leftSize){
            array[k] = leftArray[i];
            if (metrics != null) metrics.recordArrayAccess();
            i++;
            k++;
        }

        while (j<rightSize){
            array[k] = rightArray[j];
            if (metrics != null) metrics.recordArrayAccess();
            j++;
            k++;
        }
    }

    /**
     * @param array array to sort
     * @param left left boundary
     * @param right right boundary
     * @param metrics metrics collector
     */
    public static void mergeSort(int[] array, int left, int right,  SortMetrics metrics) {
        if (metrics != null) metrics.enterRecursion();
        if (left < right){

            int middle = left+(right-left)/2;

            mergeSort(array, left, middle, metrics);
            mergeSort(array, middle+1, right, metrics);

            merge(array, left, middle, right, metrics);
        }
        if (metrics != null) metrics.exitRecursion();
    }
    public static void sort(int[] array) {
        if (array.length > 1) {
            mergeSort(array, 0, array.length - 1, null);
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
            mergeSort(array, 0, array.length - 1, metrics);
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
            mergeSort(array, 0, array.length - 1, metrics);
        }

        metrics.endTiming();
    }
}
