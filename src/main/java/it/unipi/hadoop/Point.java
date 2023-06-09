package it.unipi.hadoop;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Writable;

public class Point implements Writable {

    private static final String delim = ";";
    private double[] coordinates;
    private int weight = 1;

    public Point() {}

    public Point(double[] coordinates, int weight) {
        this.coordinates = coordinates;
        this.weight = weight;
    }

    // size returns the size of the point
    public int size() {
        return coordinates.length;
    }

    // parsePoint given a point formatted in csv returns a point
    public static Point parsePoint(String value) throws IllegalArgumentException {
        List<Double> coordinates = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(value, delim);
        while (tokenizer.hasMoreTokens()) {
            coordinates.add(Double.parseDouble(tokenizer.nextToken()));
        }
    
        if (coordinates.isEmpty()) {
            throw new IllegalArgumentException("A point cannot be empty.");
        }
    
        double[] coordinatesArray = new double[coordinates.size()];
        for (int i = 0; i < coordinates.size(); i++) {
            coordinatesArray[i] = coordinates.get(i);
        }
    
        return new Point(coordinatesArray, 1);
    }
    

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (double d : this.coordinates) {
            sb.append(prefix);
            prefix = delim;
            sb.append(d);
        }
        return sb.toString();
    }

    // distance returns the euclidean distance between the point and another one
    public double distance(Point other) throws IllegalArgumentException {
        if (this.size() != other.size()) {
            throw new IllegalArgumentException(
                    String.format("Data points have different dimensions %s %s", this.toString(), other.toString()));
        }

        double squaredSum = 0.0;
        for (int i = 0; i < this.size(); i++) {
            squaredSum += Math.pow(this.coordinates[i] - other.coordinates[i], 2);
        }
        return Math.sqrt(squaredSum);
    }

    // nearest returns the index of the nearest points
    public int nearest(Point[] points) throws IllegalArgumentException{
        if (points.length == 0) {
            throw new IllegalArgumentException("cannot calculate the nearest of an empty list");
        }

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

    // average returns the average of provided points
    public static Point average(Iterable<Point> points) throws IllegalArgumentException {
        Iterator<Point> iterator = points.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Cannot compute average of an empty iterable of points.");
        }
    
        // Read out the first point to get the dimension and weight
        Point firstPoint = iterator.next();
        final int dimension = firstPoint.size();
        double[] centerCoordinates = new double[dimension];
        for (int i = 0; i < centerCoordinates.length; i++) {
            centerCoordinates[i] = firstPoint.coordinates[i] * firstPoint.weight;
        }
        int totalWeight = firstPoint.weight;
    
        // Sum up the positions of all points
        while (iterator.hasNext()) {
            Point point = iterator.next();
            for (int i = 0; i < dimension; i++) {
                centerCoordinates[i] += point.coordinates[i] * point.weight;
            }
            totalWeight += point.weight;
        }
    
        // Divide the sum by the total weight to get the average
        for (int i = 0; i < dimension; i++) {
            centerCoordinates[i] /= totalWeight;
        }
    
        return new Point(centerCoordinates, totalWeight);
    }
    
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(coordinates.length);
        for (double coordinate : this.coordinates) {
            out.writeDouble(coordinate);
        }
        out.writeInt(this.weight);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        int length = in.readInt();
        this.coordinates = new double[length];
        for (int i = 0; i < length; i++) {
            coordinates[i] = in.readDouble();
        }
        this.weight = in.readInt();
    }
}
