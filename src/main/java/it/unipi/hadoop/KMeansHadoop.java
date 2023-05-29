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
            
            // recover the config from the config
            String[] centroidCfg = context.getConfiguration().getStrings("centroids", "");

            centroids = new Point[centroidCfg.length];
            for (int i = 0; i < centroidCfg.length; i++) {
                if (centroidCfg[i].isEmpty()) {
                    throw new IllegalArgumentException(
                        String.format("Provided empty centroid")
                    );
                }
                centroids[i] = Point.parsePoint(centroidCfg[i]);
            }

            if (centroids.length == 0) {
                throw new IllegalArgumentException(
                    String.format("Cannot perform %d-means", centroids.length)
                );
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Point p = Point.parsePoint(value.toString());
            if (p == null) {
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
            
            // set the new centroid as the average of the cluster
            Point newCentroid = Point.average(cluster);
            context.write(key, newCentroid);
        }
    }
}
