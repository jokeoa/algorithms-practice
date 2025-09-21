package org.jokeoa;

/**
 * Universal class for collecting metrics of sorting algorithms.
 * Supports various types of operations and algorithm quality analysis.
 */
public class SortMetrics {
    // Basic metrics
    private int maxRecursionDepth;
    private int currentDepth;
    private int totalComparisons;
    private int totalArrayAccesses;
    private long startTime;
    private long endTime;

    private int totalSwaps;
    private int partitionCalls;
    private double totalPartitionBalance;


    private int bestPartition;
    private int worstPartition;

    /**
     * Reset all counters before starting a new sort
     */
    public void reset() {
        maxRecursionDepth = 0;
        currentDepth = 0;
        totalComparisons = 0;
        totalArrayAccesses = 0;
        startTime = 0;
        endTime = 0;


        totalSwaps = 0;
        partitionCalls = 0;
        totalPartitionBalance = 0.0;
        bestPartition = 100;
        worstPartition = 0;
    }

    /**
     * Called when entering a recursive function
     */
    public void enterRecursion() {
        currentDepth++;
        if (currentDepth > maxRecursionDepth) {
            maxRecursionDepth = currentDepth;
        }
    }

    /**
     * Called when exiting a recursive function
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
     * Record element swap operation (for QuickSort)
     */
    public void recordSwap() {
        totalSwaps++;
    }

    /**
     * Record partition operation call with partition quality analysis
     * @param leftSize size of left part after partitioning
     * @param rightSize size of right part after partitioning
     */
    public void recordPartition(int leftSize, int rightSize) {
        partitionCalls++;

        int totalSize = leftSize + rightSize;
        if (totalSize > 0) {

            double idealSize = totalSize / 2.0;
            double leftBalance = Math.abs(leftSize - idealSize) / idealSize * 100;
            double rightBalance = Math.abs(rightSize - idealSize) / idealSize * 100;
            double partitionBalance = Math.max(leftBalance, rightBalance);

            totalPartitionBalance += partitionBalance;

            if (partitionBalance < bestPartition) {
                bestPartition = (int) partitionBalance;
            }
            if (partitionBalance > worstPartition) {
                worstPartition = (int) partitionBalance;
            }
        }
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

    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    public int getTotalComparisons() {
        return totalComparisons;
    }

    public int getTotalArrayAccesses() {
        return totalArrayAccesses;
    }

    public int getTotalSwaps() {
        return totalSwaps;
    }

    public int getPartitionCalls() {
        return partitionCalls;
    }

    public double getAveragePartitionBalance() {
        return partitionCalls > 0 ? totalPartitionBalance / partitionCalls : 0.0;
    }

    public int getBestPartition() {
        return bestPartition == 100 ? 0 : bestPartition;
    }

    public int getWorstPartition() {
        return worstPartition;
    }

    /**
     * Returns execution time in milliseconds
     */
    public double getExecutionTimeMs() {
        return (endTime - startTime) / 1_000_000.0;
    }

    /**
     * Pretty print all collected metrics
     */
    public void printMetrics(String algorithmName, int arraySize) {
        System.out.println("=== Metrics for " + algorithmName + " ===");
        System.out.println("Array size: " + arraySize);
        System.out.println("Maximum recursion depth: " + maxRecursionDepth);
        System.out.println("Total comparisons: " + totalComparisons);
        System.out.println("Total array accesses: " + totalArrayAccesses);

        // QuickSort specific metrics
        if (totalSwaps > 0) {
            System.out.println("Total swaps: " + totalSwaps);
        }
        if (partitionCalls > 0) {
            System.out.println("Partition calls: " + partitionCalls);
            System.out.printf("Average partition balance: %.1f%%%n", getAveragePartitionBalance());
            System.out.println("Best partition: " + getBestPartition() + "% deviation");
            System.out.println("Worst partition: " + getWorstPartition() + "% deviation");
        }

        System.out.printf("Execution time: %.3f ms%n", getExecutionTimeMs());

        int theoreticalDepth = (int) Math.ceil(Math.log(arraySize) / Math.log(2));
        System.out.println("Theoretical depth (log₂ n): " + theoreticalDepth);

        if (maxRecursionDepth <= theoreticalDepth + 3) {
            System.out.println("✓ Recursion depth within expected bounds");
        } else {
            System.out.println("⚠ Recursion depth exceeds expected bounds");
        }
    }
}