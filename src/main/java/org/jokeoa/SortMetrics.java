package org.jokeoa;

/**
 * Class for collecting metrics of sorting algorithms.
 * Helps understand how the algorithm behaves in practice.
 */
public class SortMetrics {
    private int maxRecursionDepth;     // Maximum recursion depth
    private int currentDepth;          // Current recursion depth
    private int totalComparisons;      // Total number of comparisons
    private int totalArrayAccesses;    // Total number of array accesses
    private long startTime;            // Sorting start time
    private long endTime;              // Sorting end time

    /**
     * Reset all counters before starting new sorting
     */
    public void reset() {
        maxRecursionDepth = 0;
        currentDepth = 0;
        totalComparisons = 0;
        totalArrayAccesses = 0;
        startTime = 0;
        endTime = 0;
    }

    /**
     * Called when entering recursive function
     */
    public void enterRecursion() {
        currentDepth++;
        if (currentDepth > maxRecursionDepth) {
            maxRecursionDepth = currentDepth;
        }
    }

    /**
     * Called when exiting recursive function
     */
    public void exitRecursion() {
        currentDepth--;
    }

    /**
     * Increment comparison counter (when comparing two elements)
     */
    public void recordComparison() {
        totalComparisons++;
    }

    /**
     * Increment array access counter (read or write)
     */
    public void recordArrayAccess() {
        totalArrayAccesses++;
    }

    /**
     * Mark the start of sorting
     */
    public void startTiming() {
        startTime = System.nanoTime();
    }

    /**
     * Mark the end of sorting
     */
    public void endTiming() {
        endTime = System.nanoTime();
    }

    // Getters for collected metrics
    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public int getTotalComparisons() {
        return totalComparisons;
    }

    public int getTotalArrayAccesses() {
        return totalArrayAccesses;
    }

    /**
     * Returns execution time in milliseconds
     */
    public double getExecutionTimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * Print all collected metrics nicely
     */
    public void printMetrics(String algorithmName, int arraySize) {
        System.out.println("=== Metrics for " + algorithmName + " ===");
        System.out.println("Array size: " + arraySize);
        System.out.println("Maximum recursion depth: " + maxRecursionDepth);
        System.out.println("Total comparisons: " + totalComparisons);
        System.out.println("Total array accesses: " + totalArrayAccesses);
        System.out.printf("Execution time: %.3f ms%n", getExecutionTimeMs());

        // Calculate theoretical recursion depth for comparison
        int theoreticalDepth = (int) Math.ceil(Math.log(arraySize) / Math.log(2));
        System.out.println("Theoretical depth (log₂ n): " + theoreticalDepth);

        if (maxRecursionDepth <= theoreticalDepth + 1) {
            System.out.println("✓ Recursion depth within expected limits");
        } else {
            System.out.println("⚠ Recursion depth exceeds expected");
        }
    }
}