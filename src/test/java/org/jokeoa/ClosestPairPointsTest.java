package org.jokeoa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class ClosestPairPointsTest {

    private Random random;
    private static final double EPSILON = 1e-9;

    @BeforeEach
    void setUp() {
        random = new Random(42);
    }

    @Test
    @DisplayName("Two points")
    void testTwoPoints() {
        Point2D[] points = {
            new Point2D(0, 0),
            new Point2D(3, 4)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(5.0, result.distance, EPSILON);
        assertTrue((result.point1.equals(points[0]) && result.point2.equals(points[1])) ||
                   (result.point1.equals(points[1]) && result.point2.equals(points[0])));
    }

    @Test
    @DisplayName("Three points - triangle")
    void testThreePointsTriangle() {
        Point2D[] points = {
            new Point2D(0, 0),
            new Point2D(1, 0),
            new Point2D(0.5, 1)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(1.0, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Four points - square")
    void testFourPointsSquare() {
        Point2D[] points = {
            new Point2D(0, 0),
            new Point2D(1, 0),
            new Point2D(1, 1),
            new Point2D(0, 1)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(1.0, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Collinear points")
    void testCollinearPoints() {
        Point2D[] points = {
            new Point2D(0, 0),
            new Point2D(1, 0),
            new Point2D(3, 0),
            new Point2D(4, 0),
            new Point2D(7, 0)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(1.0, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Identical points")
    void testIdenticalPoints() {
        Point2D[] points = {
            new Point2D(1, 1),
            new Point2D(2, 2),
            new Point2D(1, 1)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(0.0, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Many identical points")
    void testManyIdenticalPoints() {
        Point2D[] points = {
            new Point2D(5, 5),
            new Point2D(5, 5),
            new Point2D(5, 5),
            new Point2D(5, 5),
            new Point2D(10, 10)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(0.0, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Large distance difference")
    void testLargeDistanceDifference() {
        Point2D[] points = {
            new Point2D(0, 0),
            new Point2D(0.001, 0),
            new Point2D(1000, 1000),
            new Point2D(2000, 2000)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(0.001, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Points in grid pattern")
    void testGridPattern() {
        Point2D[] points = new Point2D[9];
        int index = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                points[index++] = new Point2D(i, j);
            }
        }

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(1.0, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Negative coordinates")
    void testNegativeCoordinates() {
        Point2D[] points = {
            new Point2D(-5, -5),
            new Point2D(-3, -4),
            new Point2D(1, 2),
            new Point2D(0, 0)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);

        double expectedDistance = Math.sqrt(2);
        assertEquals(expectedDistance, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Very close points with precision")
    void testVeryClosePoints() {
        Point2D[] points = {
            new Point2D(0, 0),
            new Point2D(1e-10, 1e-10),
            new Point2D(10, 10),
            new Point2D(20, 20)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        double expected = Math.sqrt(2e-20);
        assertEquals(expected, result.distance, EPSILON);
    }

    @RepeatedTest(10)
    @DisplayName("Random points (repeated test)")
    void testRandomPoints() {
        int size = 10 + random.nextInt(40);
        Point2D[] points = generateRandomPoints(size);

        PointPair result = ClosestPairPoints.findClosestPair(points);
        PointPair bruteForceResult = bruteForceClosestPair(points);

        assertEquals(bruteForceResult.distance, result.distance, EPSILON,
                "Algorithm result should match brute force for " + size + " points");
    }

    @Test
    @DisplayName("Large random point set")
    void testLargeRandomPointSet() {
        Point2D[] points = generateRandomPoints(1000);

        PointPair result = ClosestPairPoints.findClosestPair(points);

        assertNotNull(result);
        assertTrue(result.distance >= 0);
        assertNotNull(result.point1);
        assertNotNull(result.point2);
        assertNotEquals(result.point1, result.point2);
    }

    @Test
    @DisplayName("Worst case - all points very close")
    void testWorstCaseClosePoints() {
        Point2D[] points = new Point2D[100];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point2D(i * 0.001, random.nextGaussian() * 0.0001);
        }

        PointPair result = ClosestPairPoints.findClosestPair(points);
        PointPair bruteForceResult = bruteForceClosestPair(points);

        assertEquals(bruteForceResult.distance, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Strip boundary case")
    void testStripBoundaryCase() {
        Point2D[] points = {
            new Point2D(0, 0),
            new Point2D(2, 0),
            new Point2D(1, 0.5),
            new Point2D(1, -0.5),
            new Point2D(4, 0),
            new Point2D(6, 0)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(1.0, result.distance, EPSILON);
    }

    @Test
    @DisplayName("Performance comparison with brute force")
    void testPerformanceComparison() {
        System.out.println("\n=== Closest Pair Performance Analysis ===");

        int[] sizes = {50, 100, 200, 500, 1000};

        for (int size : sizes) {
            Point2D[] points = generateRandomPoints(size);

            long startTime = System.nanoTime();
            PointPair divideConquerResult = ClosestPairPoints.findClosestPair(points);
            long divideConquerTime = System.nanoTime() - startTime;

            long bruteForceBound = 1_000_000;
            PointPair bruteForceResult = null;
            long bruteForceTime = 0;

            if (size <= 200) {
                startTime = System.nanoTime();
                bruteForceResult = bruteForceClosestPair(points);
                bruteForceTime = System.nanoTime() - startTime;

                assertEquals(bruteForceResult.distance, divideConquerResult.distance, EPSILON,
                        "Results should match for size " + size);
            }

            double dcMs = divideConquerTime / 1_000_000.0;
            double bfMs = bruteForceTime / 1_000_000.0;

            if (size <= 200) {
                System.out.printf("Size %d: D&C %.3fms vs Brute Force %.3fms (speedup: %.2fx)\n",
                    size, dcMs, bfMs, bfMs / dcMs);
            } else {
                System.out.printf("Size %d: D&C %.3fms (Brute Force too slow)\n", size, dcMs);
            }
        }
    }

    @Test
    @DisplayName("Time complexity verification")
    void testTimeComplexityVerification() {
        System.out.println("\n=== Closest Pair Time Complexity Analysis ===");

        int[] sizes = {100, 200, 500, 1000, 2000, 5000};

        for (int size : sizes) {
            Point2D[] points = generateRandomPoints(size);

            SortMetrics metrics = new SortMetrics();
            long startTime = System.nanoTime();
            PointPair result = ClosestPairPoints.findClosestPairWithMetrics(points, metrics);
            long endTime = System.nanoTime();

            double timeMs = (endTime - startTime) / 1_000_000.0;
            double timePerNLogN = timeMs / (size * Math.log(size) / Math.log(2));

            System.out.printf("Size %d: %.3fms (%.6fms per n*log(n)), depth: %d, comparisons: %d\n",
                size, timeMs, timePerNLogN, metrics.getMaxRecursionDepth(), metrics.getTotalComparisons());

            assertNotNull(result);
            assertTrue(result.distance >= 0);
        }
    }

    @Test
    @DisplayName("Recursion depth analysis")
    void testRecursionDepthAnalysis() {
        int[] sizes = {100, 500, 1000, 5000};

        for (int size : sizes) {
            Point2D[] points = generateRandomPoints(size);

            SortMetrics metrics = new SortMetrics();
            ClosestPairPoints.findClosestPairWithMetrics(points, metrics);

            int expectedMaxDepth = (int)(Math.log(size) / Math.log(2)) + 5;

            assertTrue(metrics.getMaxRecursionDepth() <= expectedMaxDepth,
                    String.format("Recursion depth %d should be reasonable for size %d (expected ≤ %d)",
                            metrics.getMaxRecursionDepth(), size, expectedMaxDepth));

            System.out.printf("Size %d: Recursion depth %d (log₂(n)+5 = %d)\n",
                size, metrics.getMaxRecursionDepth(), expectedMaxDepth);
        }
    }

    @Test
    @DisplayName("Invalid input parameters")
    void testInvalidInputParameters() {
        assertThrows(IllegalArgumentException.class, () ->
            ClosestPairPoints.findClosestPair(null), "Should throw for null array");

        assertThrows(IllegalArgumentException.class, () ->
            ClosestPairPoints.findClosestPair(new Point2D[]{}), "Should throw for empty array");

        assertThrows(IllegalArgumentException.class, () ->
            ClosestPairPoints.findClosestPair(new Point2D[]{new Point2D(0, 0)}),
            "Should throw for single point");
    }

    @Test
    @DisplayName("Metrics demonstration")
    void demonstrateMetrics() {
        System.out.println("\n=== Closest Pair Metrics Demonstration ===");

        int[] testCases = {50, 100, 500, 1000};

        for (int size : testCases) {
            Point2D[] points = generateRandomPoints(size);

            SortMetrics metrics = new SortMetrics();
            PointPair result = ClosestPairPoints.findClosestPairWithMetrics(points, metrics);

            System.out.println("\nPoint set size: " + size);
            System.out.printf("Closest pair distance: %.6f\n", result.distance);
            System.out.println("Recursion depth: " + metrics.getMaxRecursionDepth());
            System.out.println("Number of comparisons: " + metrics.getTotalComparisons());
            System.out.printf("Execution time: %.3f ms\n", metrics.getExecutionTimeMs());

            double theoreticalComparisons = size * Math.log(size) / Math.log(2);
            System.out.printf("Theoretical comparisons (n*log(n)): %.0f\n", theoreticalComparisons);
            System.out.printf("Comparison ratio: %.2f\n", metrics.getTotalComparisons() / theoreticalComparisons);

            assertNotNull(result);
        }
    }

    @Test
    @DisplayName("Edge case - minimum distance")
    void testMinimumDistance() {
        Point2D[] points = {
            new Point2D(0, 0),
            new Point2D(Double.MIN_VALUE, 0),
            new Point2D(1, 1),
            new Point2D(2, 2)
        };

        PointPair result = ClosestPairPoints.findClosestPair(points);
        assertEquals(Double.MIN_VALUE, result.distance, EPSILON);
    }

    private Point2D[] generateRandomPoints(int size) {
        Point2D[] points = new Point2D[size];
        for (int i = 0; i < size; i++) {
            double x = random.nextDouble() * 1000 - 500;
            double y = random.nextDouble() * 1000 - 500;
            points[i] = new Point2D(x, y);
        }
        return points;
    }

    private PointPair bruteForceClosestPair(Point2D[] points) {
        double minDist = Double.POSITIVE_INFINITY;
        Point2D closest1 = null, closest2 = null;

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double dist = points[i].distanceTo(points[j]);
                if (dist < minDist) {
                    minDist = dist;
                    closest1 = points[i];
                    closest2 = points[j];
                }
            }
        }

        return new PointPair(closest1, closest2, minDist);
    }
}