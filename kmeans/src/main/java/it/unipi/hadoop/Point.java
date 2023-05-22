package it.unipi.hadoop;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Point {

    private List<Double> position;

    public Point(List<Double> position) {
        this.position = position;
    }

    public static Point parsePoint(String value) {
        List<Double> position = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        while (tokenizer.hasMoreTokens()) {
            position.add(Double.parseDouble(tokenizer.nextToken()));
        }
        return new Point(position);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < position.size(); i++) {
            sb.append(position.get(i));
            if (i < position.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public double distance(Point next) {
        if (this.position.size() != next.position.size()) {
            throw new IllegalArgumentException("Data points have different dimensions");
        }

        double squaredSum = 0.0;
        for (int i = 0; i < this.position.size(); i++) {
            squaredSum += Math.pow(this.position.get(i) - next.position.get(i), 2);
        }
        return Math.sqrt(squaredSum);
    }

    public int nearest(List<Point> points) {
        int nearestIndex = 0;
        double minDistance = this.distance(points.get(0));
        for (int i = 1; i < points.size(); i++) {
            double distance = this.distance(points.get(i));
            if (distance >= minDistance) {
                continue;
            }
            minDistance = distance;
            nearestIndex = i;
        }
        return nearestIndex;
    }

    public static Point center(List<Point> points) {
        int dimensions = points.get(0).position.size();
        List<Double> centerPosition = new ArrayList<>(dimensions);

        // Initialize the center position with zeros
        for (int i = 0; i < dimensions; i++) {
            centerPosition.add(0.0);
        }

        // Sum up the positions of all points
        for (Point point : points) {
            for (int i = 0; i < dimensions; i++) {
                centerPosition.set(i, centerPosition.get(i) + point.position.get(i));
            }
        }

        // Divide the sum by the number of points to get the average
        for (int i = 0; i < dimensions; i++) {
            centerPosition.set(i, centerPosition.get(i) / points.size());
        }

        return new Point(centerPosition);
    }
}
