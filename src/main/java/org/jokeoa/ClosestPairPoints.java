package org.jokeoa;

import java.util.Arrays;
import java.util.Comparator;

public class ClosestPairPoints {

    public static PointPair findClosestPair(Point2D[] points) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("Need at least 2 points");
        }

        Point2D[] pointsByX = points.clone();
        Point2D[] pointsByY = points.clone();

        Arrays.sort(pointsByX, Comparator.comparingDouble(p -> p.x));
        Arrays.sort(pointsByY, Comparator.comparingDouble(p -> p.y));

        return closestPairRec(pointsByX, pointsByY, 0, points.length - 1);
    }

    private static PointPair closestPairRec(Point2D[] pointsByX, Point2D[] pointsByY, int left, int right) {
        int n = right - left + 1;

        if (n <= 3) {
            return bruteForce(pointsByX, left, right);
        }

        int mid = left + (right - left) / 2;
        Point2D midPoint = pointsByX[mid];

        Point2D[] leftY = new Point2D[mid - left + 1];
        Point2D[] rightY = new Point2D[right - mid];

        int leftIndex = 0, rightIndex = 0;
        for (Point2D point : pointsByY) {
            if (point.x <= midPoint.x && leftIndex < leftY.length) {
                leftY[leftIndex++] = point;
            } else if (rightIndex < rightY.length) {
                rightY[rightIndex++] = point;
            }
        }

        PointPair leftClosest = closestPairRec(pointsByX, leftY, left, mid);
        PointPair rightClosest = closestPairRec(pointsByX, rightY, mid + 1, right);

        PointPair minPair = (leftClosest.distance <= rightClosest.distance) ? leftClosest : rightClosest;
        double minDist = minPair.distance;

        Point2D[] strip = new Point2D[n];
        int stripSize = 0;

        for (Point2D point : pointsByY) {
            if (Math.abs(point.x - midPoint.x) < minDist) {
                strip[stripSize++] = point;
            }
        }

        PointPair stripClosest = findClosestInStrip(strip, stripSize, minDist);
        if (stripClosest != null && stripClosest.distance < minPair.distance) {
            return stripClosest;
        }

        return minPair;
    }

    private static PointPair bruteForce(Point2D[] points, int left, int right) {
        double minDist = Double.POSITIVE_INFINITY;
        Point2D closest1 = null, closest2 = null;

        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
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

    private static PointPair findClosestInStrip(Point2D[] strip, int size, double minDist) {
        double min = minDist;
        Point2D closest1 = null, closest2 = null;

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size && (strip[j].y - strip[i].y) < min; j++) {
                double dist = strip[i].distanceTo(strip[j]);
                if (dist < min) {
                    min = dist;
                    closest1 = strip[i];
                    closest2 = strip[j];
                }
            }
        }

        return (closest1 != null) ? new PointPair(closest1, closest2, min) : null;
    }

    public static PointPair findClosestPairWithMetrics(Point2D[] points, SortMetrics metrics) {
        if (points == null || points.length < 2) {
            throw new IllegalArgumentException("Need at least 2 points");
        }

        metrics.reset();
        metrics.startTiming();

        Point2D[] pointsByX = points.clone();
        Point2D[] pointsByY = points.clone();

        ClosestPairContext context = new ClosestPairContext(metrics);

        Arrays.sort(pointsByX, (p1, p2) -> {
            context.recordComparison();
            return Double.compare(p1.x, p2.x);
        });

        Arrays.sort(pointsByY, (p1, p2) -> {
            context.recordComparison();
            return Double.compare(p1.y, p2.y);
        });

        PointPair result = closestPairRecWithMetrics(pointsByX, pointsByY, 0, points.length - 1, context);

        metrics.endTiming();
        return result;
    }

    private static PointPair closestPairRecWithMetrics(Point2D[] pointsByX, Point2D[] pointsByY,
                                                       int left, int right, ClosestPairContext context) {
        context.enterRecursion();
        int n = right - left + 1;

        if (n <= 3) {
            PointPair result = bruteForceWithMetrics(pointsByX, left, right, context);
            context.exitRecursion();
            return result;
        }

        int mid = left + (right - left) / 2;
        Point2D midPoint = pointsByX[mid];

        Point2D[] leftY = new Point2D[mid - left + 1];
        Point2D[] rightY = new Point2D[right - mid];

        int leftIndex = 0, rightIndex = 0;
        for (Point2D point : pointsByY) {
            context.recordComparison();
            if (point.x <= midPoint.x && leftIndex < leftY.length) {
                leftY[leftIndex++] = point;
            } else if (rightIndex < rightY.length) {
                rightY[rightIndex++] = point;
            }
        }

        PointPair leftClosest = closestPairRecWithMetrics(pointsByX, leftY, left, mid, context);
        PointPair rightClosest = closestPairRecWithMetrics(pointsByX, rightY, mid + 1, right, context);

        context.recordComparison();
        PointPair minPair = (leftClosest.distance <= rightClosest.distance) ? leftClosest : rightClosest;
        double minDist = minPair.distance;

        Point2D[] strip = new Point2D[n];
        int stripSize = 0;

        for (Point2D point : pointsByY) {
            context.recordComparison();
            if (Math.abs(point.x - midPoint.x) < minDist) {
                strip[stripSize++] = point;
            }
        }

        PointPair stripClosest = findClosestInStripWithMetrics(strip, stripSize, minDist, context);
        if (stripClosest != null) {
            context.recordComparison();
            if (stripClosest.distance < minPair.distance) {
                context.exitRecursion();
                return stripClosest;
            }
        }

        context.exitRecursion();
        return minPair;
    }

    private static PointPair bruteForceWithMetrics(Point2D[] points, int left, int right, ClosestPairContext context) {
        double minDist = Double.POSITIVE_INFINITY;
        Point2D closest1 = null, closest2 = null;

        for (int i = left; i <= right; i++) {
            for (int j = i + 1; j <= right; j++) {
                double dist = points[i].distanceTo(points[j]);
                context.recordComparison();
                if (dist < minDist) {
                    minDist = dist;
                    closest1 = points[i];
                    closest2 = points[j];
                }
            }
        }

        return new PointPair(closest1, closest2, minDist);
    }

    private static PointPair findClosestInStripWithMetrics(Point2D[] strip, int size, double minDist, ClosestPairContext context) {
        double min = minDist;
        Point2D closest1 = null, closest2 = null;

        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                context.recordComparison();
                if ((strip[j].y - strip[i].y) >= min) {
                    break;
                }
                double dist = strip[i].distanceTo(strip[j]);
                context.recordComparison();
                if (dist < min) {
                    min = dist;
                    closest1 = strip[i];
                    closest2 = strip[j];
                }
            }
        }

        return (closest1 != null) ? new PointPair(closest1, closest2, min) : null;
    }

    private static class ClosestPairContext {
        private final SortMetrics metrics;

        public ClosestPairContext(SortMetrics metrics) {
            this.metrics = metrics;
        }

        public void recordComparison() {
            if (metrics != null) {
                metrics.recordComparison();
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
    }
}