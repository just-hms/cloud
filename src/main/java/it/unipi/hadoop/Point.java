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
    private Double[] position;

    public Point() {}

    public Point(Double[] position) {
        this.position = position;
    }

    // size returns the size of the point
    public int size() {
        return position.length;
    }

    // parsePoint given a point formatted in csv returns a point
    public static Point parsePoint(String value) throws IllegalArgumentException{
        List<Double> position = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(value, delim);
        while (tokenizer.hasMoreTokens()) {
            position.add(Double.parseDouble(tokenizer.nextToken()));
        }
        
        if (position.size() == 0){
            throw new IllegalArgumentException("a point cannot be empty");
        }

        return new Point(position.toArray(new Double[0]));
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String prefix = "";
        for (Double d : this.position) {
            sb.append(prefix);
            prefix = delim;
            sb.append(d);
        }
        return sb.toString();
    }
    
    // distance returns the euclidean distance between the point and another one
    public double distance(Point other) throws IllegalArgumentException{
        if (this.size() != other.size()) {
            throw new IllegalArgumentException(
                String.format("Data points have different dimensions %s %s", this.toString(),  other.toString())
            );
        }

        double squaredSum = 0.0;
        for (int i = 0; i < this.size(); i++) {
            squaredSum += Math.pow(this.position[i] - other.position[i], 2);
        }
        return Math.sqrt(squaredSum);
    }

    // nearest returns the index of the nearest points
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

    // average returns the average of provided points
    public static Point average(Iterable<Point> points) {
        Iterator<Point> iterator = points.iterator();
        if (!iterator.hasNext()) {
            throw new IllegalArgumentException("Cannot compute average of an empty iterable of points.");
        }

        // read out the first point to get the dimension
        Point firstPoint = iterator.next();
        int dimension = firstPoint.size();
        Double[] centerPosition = new Double[dimension];
        for (int i = 0; i < centerPosition.length; i++) {
            centerPosition[i] = firstPoint.position[i];
        }


        int count = 1;

        // Sum up the positions of all points
        while(iterator.hasNext()) {

            Point point = iterator.next();

            for (int i = 0; i < centerPosition.length; i++) {
                centerPosition[i] += point.position[i];
            }
            count++;
        }
    
        // Divide the sum by the number of points to get the average
        for (int i = 0; i < centerPosition.length; i++) {
            centerPosition[i] /=  count;
        }
    
        return new Point(centerPosition);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(position.length);
        for (Double coordinate : position) {
            out.writeDouble(coordinate);
        }
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        int length = in.readInt();
        position = new Double[length];
        for (int i = 0; i < length; i++) {
            position[i] = in.readDouble();
        }
    }    
}
