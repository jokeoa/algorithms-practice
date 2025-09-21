package org.jokeoa;
public class MergeSort {

 /**
  *  @param array source array
  * @param left begin of left side
  * @param middle end of left side
  * @param right end of the right side
  */
    public static void merge(int[] array, int left, int middle, int right, SortMetrics metrics) {
        int leftSize = middle - left + 1;
        int rightSize = right - middle;

        int[] leftArray = new int[leftSize];
        int[] rightArray = new int[rightSize];

        for (int i = 0; i < leftSize; i++) {
            leftArray[i] = array[left + i];

        }
        for (int j = 0; j < rightSize; j++) {
            rightArray[j] = array[middle + 1 + j];

        }
        int i = 0;
        int j = 0;
        int k = left;
        while (i < leftSize && j < rightSize) {

            if (leftArray[i] <= rightArray[j]) {
                array[k] = leftArray[i];
                i++;

            } else {
                array[k] = rightArray[j];
                j++;

            }
            k++;
        }

        while (i<leftSize){
            array[k] = leftArray[i];

            i++;
            k++;
        }

        while (j<rightSize){
            array[k] = rightArray[j];

            j++;
            k++;
        }
    }

 /**
 * @param array array for sorting
 * @param left left edge
 * @param right right edge
 */
    public static void mergeSort(int[] array, int left, int right,  SortMetrics metrics) {
        if (left < right) {

            int middle = left + (right - left) / 2;

            mergeSort(array, left, middle, metrics);
            mergeSort(array, middle + 1, right, metrics);

            merge(array, left, middle, right, metrics);
        }
    }
    public static void sort(int[] array) {
        if (array.length > 1) {
            mergeSort(array, 0, array.length - 1, null);
        }
    }
}