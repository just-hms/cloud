package it.unipi.hadoop;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Point {

    private Double[] position;

    public Point(Double[] position) {
        this.position = position;
    }

    public static Point parsePoint(String value) {
        List<Double> position = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(value, ",");
        while (tokenizer.hasMoreTokens()) {
            position.add(Double.parseDouble(tokenizer.nextToken()));
        }
        return new Point(position.toArray(new Double[0]));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Double d : position) {
            sb.append(prefix);
            prefix = ",";
            sb.append(d);
        }
        return sb.toString();
    }

    public double distance(Point next) {
        if (this.position.length != next.position.length) {
            throw new IllegalArgumentException("Data points have different dimensions");
        }

        double squaredSum = 0.0;
        for (int i = 0; i < this.position.length; i++) {
            squaredSum += Math.pow(this.position[i] - next.position[i], 2);
        }
        return Math.sqrt(squaredSum);
    }

    public int nearest(Point[] points) {
        int nearestIndex = 0;
        double minDistance = this.distance(points[0]);
        for (int i = 1; i < points.length; i++) {
            double distance = this.distance(points[i]);
            if (distance >= minDistance) {
                continue;
            }
            minDistance = distance;
            nearestIndex = i;
        }
        return nearestIndex;
    }

    public static Point Average(Iterable<Point> points) {
        int dimensions = points.iterator().next().position.length;
        Double[] centerPosition = new Double[dimensions];

        int count = 0;
    
        // Sum up the positions of all points
        for (Point point : points) {
            for (int i = 0; i < dimensions; i++) {
                centerPosition[i] += point.position[i];
            }
            count++;
        }
    
        // Divide the sum by the number of points to get the average
        for (int i = 0; i < dimensions; i++) {
            centerPosition[i] /=  count;
        }
    
        return new Point(centerPosition);
    }    
}
