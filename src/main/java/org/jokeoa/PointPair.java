package org.jokeoa;

public class PointPair {
    public final Point2D point1;
    public final Point2D point2;
    public final double distance;

    public PointPair(Point2D point1, Point2D point2) {
        this.point1 = point1;
        this.point2 = point2;
        this.distance = point1.distanceTo(point2);
    }

    public PointPair(Point2D point1, Point2D point2, double distance) {
        this.point1 = point1;
        this.point2 = point2;
        this.distance = distance;
    }

    @Override
    public String toString() {
        return String.format("Pair[%s, %s] distance=%.6f", point1, point2, distance);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PointPair pointPair = (PointPair) obj;
        return Double.compare(pointPair.distance, distance) == 0 &&
               ((point1.equals(pointPair.point1) && point2.equals(pointPair.point2)) ||
                (point1.equals(pointPair.point2) && point2.equals(pointPair.point1)));
    }

    @Override
    public int hashCode() {
        return point1.hashCode() + point2.hashCode() + Double.hashCode(distance);
    }
}