package org.jokeoa;

public class SortContext {
    private final int[] array;           // Original array to sort
    private final int[] buffer;          // Reusable buffer (for MergeSort)
    private final SortMetrics metrics;   // Performance metrics
    private final int cutoffThreshold;   // Threshold for switching to insertion sort

    /**
     * @param array array to sort
     * @param metrics metrics collection object (can be null)
     * @param cutoffThreshold threshold for switching to simple sort
     */
    public SortContext(int[] array, SortMetrics metrics, int cutoffThreshold) {
        this.array = array;
        this.metrics = metrics;
        this.cutoffThreshold = cutoffThreshold;

        this.buffer = new int[(array.length + 1) / 2];
    }

    /**
     * Constructor with default parameters
     */
    public SortContext(int[] array, SortMetrics metrics) {
        this(array, metrics, 7);
    }

    /**
     * Constructor without metrics
     */
    public SortContext(int[] array) {
        this(array, null, 7);
    }

    public int[] getArray() {
        return array;
    }

    public int[] getBuffer() {
        return buffer;
    }

    public SortMetrics getMetrics() {
        return metrics;
    }

    public int getCutoffThreshold() {
        return cutoffThreshold;
    }

    /**
     * Records metric if metrics object exists
     */
    public void recordComparison() {
        if (metrics != null) {
            metrics.recordComparison();
        }
    }

    public void recordArrayAccess() {
        if (metrics != null) {
            metrics.recordArrayAccess();
        }
    }

    public void recordSwap() {
        if (metrics != null) {
            metrics.recordSwap();
        }
    }

    public void recordPartition(int leftSize, int rightSize) {
        if (metrics != null) {
            metrics.recordPartition(leftSize, rightSize);
        }
    }

    public void enterRecursion() {
        if (metrics != null) {
            metrics.enterRecursion();
        }
    }

    public void exitRecursion() {
        if (metrics != null) {
            metrics.exitRecursion();
        }
    }

    /**
     * Gets the size of array segment
     */
    public int getSubarraySize(int left, int right) {
        return right - left + 1;
    }

    /**
     * Checks if cutoff should be used for this segment
     */
    public boolean shouldUseCutoff(int left, int right) {
        return getSubarraySize(left, right) <= cutoffThreshold;
    }
}