package org.jokeoa;

public class DeterministicSelect {

    public static int select(int[] array, int k) {
        if (array == null || array.length == 0 || k < 1 || k > array.length) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        int[] workingArray = array.clone();
        return selectKth(workingArray, 0, workingArray.length - 1, k - 1);
    }

    private static int selectKth(int[] array, int left, int right, int k) {
        if (left == right) {
            return array[left];
        }

        int pivotIndex = medianOfMedians(array, left, right);
        pivotIndex = partition(array, left, right, pivotIndex);

        if (k == pivotIndex) {
            return array[k];
        } else if (k < pivotIndex) {
            return selectKth(array, left, pivotIndex - 1, k);
        } else {
            return selectKth(array, pivotIndex + 1, right, k);
        }
    }

    private static int medianOfMedians(int[] array, int left, int right) {
        int n = right - left + 1;

        if (n <= 5) {
            insertionSort(array, left, right);
            return left + (n - 1) / 2;
        }

        int numGroups = (n + 4) / 5;
        int[] medians = new int[numGroups];

        for (int i = 0; i < numGroups; i++) {
            int groupLeft = left + i * 5;
            int groupRight = Math.min(groupLeft + 4, right);

            insertionSort(array, groupLeft, groupRight);
            int medianIndex = groupLeft + (groupRight - groupLeft) / 2;
            medians[i] = array[medianIndex];
        }

        int medianOfMediansValue = selectKth(medians, 0, medians.length - 1, medians.length / 2);

        for (int i = left; i <= right; i++) {
            if (array[i] == medianOfMediansValue) {
                return i;
            }
        }

        return left;
    }

    private static int partition(int[] array, int left, int right, int pivotIndex) {
        int pivotValue = array[pivotIndex];

        swap(array, pivotIndex, right);

        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (array[i] < pivotValue) {
                swap(array, i, storeIndex);
                storeIndex++;
            }
        }

        swap(array, storeIndex, right);
        return storeIndex;
    }

    private static void insertionSort(int[] array, int left, int right) {
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

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static int selectWithMetrics(int[] array, int k, SortMetrics metrics) {
        if (array == null || array.length == 0 || k < 1 || k > array.length) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        metrics.reset();
        metrics.startTiming();

        int[] workingArray = array.clone();
        SortContext context = new SortContext(workingArray, metrics);
        int result = selectKthWithMetrics(context, 0, workingArray.length - 1, k - 1);

        metrics.endTiming();
        return result;
    }

    private static int selectKthWithMetrics(SortContext context, int left, int right, int k) {
        int[] array = context.getArray();
        context.enterRecursion();

        if (left == right) {
            context.recordArrayAccess();
            context.exitRecursion();
            return array[left];
        }

        int pivotIndex = medianOfMediansWithMetrics(context, left, right);
        pivotIndex = partitionWithMetrics(context, left, right, pivotIndex);

        context.recordComparison();
        if (k == pivotIndex) {
            context.recordArrayAccess();
            context.exitRecursion();
            return array[k];
        } else if (k < pivotIndex) {
            context.recordComparison();
            int result = selectKthWithMetrics(context, left, pivotIndex - 1, k);
            context.exitRecursion();
            return result;
        } else {
            int result = selectKthWithMetrics(context, pivotIndex + 1, right, k);
            context.exitRecursion();
            return result;
        }
    }

    private static int medianOfMediansWithMetrics(SortContext context, int left, int right) {
        int[] array = context.getArray();
        int n = right - left + 1;

        if (n <= 5) {
            insertionSortWithMetrics(context, left, right);
            return left + (n - 1) / 2;
        }

        int numGroups = (n + 4) / 5;
        int[] medians = new int[numGroups];

        for (int i = 0; i < numGroups; i++) {
            int groupLeft = left + i * 5;
            int groupRight = Math.min(groupLeft + 4, right);

            insertionSortWithMetrics(context, groupLeft, groupRight);
            int medianIndex = groupLeft + (groupRight - groupLeft) / 2;
            context.recordArrayAccess();
            medians[i] = array[medianIndex];
        }

        SortContext medianContext = new SortContext(medians, context.getMetrics());
        int medianOfMediansValue = selectKthWithMetrics(medianContext, 0, medians.length - 1, medians.length / 2);

        for (int i = left; i <= right; i++) {
            context.recordArrayAccess();
            context.recordComparison();
            if (array[i] == medianOfMediansValue) {
                return i;
            }
        }

        return left;
    }

    private static int partitionWithMetrics(SortContext context, int left, int right, int pivotIndex) {
        int[] array = context.getArray();
        context.recordArrayAccess();
        int pivotValue = array[pivotIndex];

        swapWithMetrics(context, pivotIndex, right);

        int storeIndex = left;
        for (int i = left; i < right; i++) {
            context.recordArrayAccess();
            context.recordComparison();
            if (array[i] < pivotValue) {
                swapWithMetrics(context, i, storeIndex);
                storeIndex++;
            }
        }

        swapWithMetrics(context, storeIndex, right);
        context.recordPartition(storeIndex - left, right - storeIndex);
        return storeIndex;
    }

    private static void insertionSortWithMetrics(SortContext context, int left, int right) {
        int[] array = context.getArray();

        for (int i = left + 1; i <= right; i++) {
            context.recordArrayAccess();
            int key = array[i];
            int j = i - 1;

            while (j >= left) {
                context.recordArrayAccess();
                context.recordComparison();
                if (array[j] <= key) {
                    break;
                }
                context.recordArrayAccess();
                array[j + 1] = array[j];
                j--;
            }
            context.recordArrayAccess();
            array[j + 1] = key;
        }
    }

    private static void swapWithMetrics(SortContext context, int i, int j) {
        int[] array = context.getArray();
        context.recordArrayAccess();
        context.recordArrayAccess();
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
        context.recordArrayAccess();
        context.recordArrayAccess();
        context.recordSwap();
    }
}