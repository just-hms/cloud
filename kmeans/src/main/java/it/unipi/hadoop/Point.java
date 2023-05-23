package it.unipi.hadoop;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.io.Writable;


public class Point implements Writable {

    private Double[] position;

    public Point() {}

    public Point(Double[] position) {
        this.position = position;
    }

    public int Size() {
        return position.length;
    }
    
    public static Point parsePoint(String value) {
        List<Double> position = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(value, ";");
        while (tokenizer.hasMoreTokens()) {
            position.add(Double.parseDouble(tokenizer.nextToken()));
        }
        
        if (position.size() == 0){
            return null;
        }

        return new Point(position.toArray(new Double[0]));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String prefix = "[";
        for (Double d : position) {
            sb.append(prefix);
            prefix = ",";
            sb.append(d);
        }
        sb.append("]");
        return sb.toString();
    }

    public double distance(Point other) {
        if (this.position.length != other.position.length) {
            throw new IllegalArgumentException(
                String.format("Data points have different dimensions %s %s", this.toString(),  other.toString())
            );
        }

        double squaredSum = 0.0;
        for (int i = 0; i < this.position.length; i++) {
            squaredSum += Math.pow(this.position[i] - other.position[i], 2);
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

    public static Point average(Iterable<Point> points, int dimensions) {
        Double[] centerPosition = new Double[dimensions];
        for (int i = 0; i < centerPosition.length; i++) {
            centerPosition[i] = 0.0;
        }

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

    // TODO: fix me
    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(position.length);
        for (Double coordinate : position) {
            out.writeDouble(coordinate);
        }
    }

    // TODO: check if you can use this to get Points in input in the map phase
    @Override
    public void readFields(DataInput in) throws IOException {
        int length = in.readInt();
        position = new Double[length];
        for (int i = 0; i < length; i++) {
            position[i] = in.readDouble();
        }
    }    
}
