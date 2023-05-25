package it.unipi.hadoop;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

public class KMeansHadoop {
    public static class KMeansMapper extends Mapper<Object, Text, IntWritable, Point> {
        private Point[] centroids;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {

            // parse input random centroids
            String[] centroidsString = context.getConfiguration().getStrings("centroids", "");

            for (String string : centroidsString) {
                System.out.println(string);            
            }

            centroids = new Point[centroidsString.length];
            for (int i = 0; i < centroidsString.length; i++) {
                if (centroidsString[i] == ""){
                    break;
                }
                centroids[i] = Point.fromCSV(centroidsString[i]);
            }

            if (centroids.length == 0){
                throw new IllegalArgumentException(
                    String.format("Cannot perform %d-means", centroids.length)
                );
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Point p = Point.fromCSV(value.toString());
            if (p == null){
                return;
            }
            // Find the nearest centroid for the data point
            int idx = p.nearest(centroids);

            // Emit the nearest centroid index and data point
            context.write(new IntWritable(idx), p);
        }
    }

    public static class KMeansReducer extends Reducer<IntWritable, Point, IntWritable, Point> {
        
        @Override
        protected void reduce(IntWritable key, Iterable<Point> cluster, Context context)
                throws IOException, InterruptedException {

            int dimension = context.getConfiguration().getInt("dimensions", -1);
            if (dimension == -1){
                throw new IllegalArgumentException(
                    String.format("Dimensions cannot be negative %d", dimension)
                );
            }
            Point newCentroid = Point.average(cluster, dimension);
            context.write(key, newCentroid);
        }
    }

}
