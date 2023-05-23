package it.unipi.hadoop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class KMeansHadoop {
    public static class KMeansMapper extends Mapper<Object, Text, IntWritable, Point> {
        private Point[] centroids;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {

            // parse input random centroids
            String[] centroidString = context.getConfiguration().getStrings("centroids", "");

            centroids = new Point[centroidString.length];
            for (int i = 0; i < centroidString.length; i++) {
                if (centroidString[i] == ""){
                    break;
                }
                centroids[i] = Point.parsePoint(centroidString[i]);
            }

            if (centroids.length == 0){
                throw new IllegalArgumentException(
                    String.format("Cannot perform %d-means", centroids.length)
                );
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            Point p = Point.parsePoint(value.toString());
            if (p == null){
                return;
            }
            // Find the nearest centroid for the data point
            int idx = p.nearest(centroids);

            // Emit the nearest centroid index and data point
            context.write(new IntWritable(idx), p);
        }
    }

    // TODO maybe add a shuffle

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

    public static void main(String[] args) throws Exception {

        // get centroids
        List<String> centroids = Files.readAllLines(Paths.get(args[0]));
        Point p = Point.parsePoint(centroids.get(0));
        
        // infer configuration from centroids
        Configuration conf = new Configuration();
        conf.setStrings("centroids", centroids.toArray(new String[0]));
        conf.setInt("dimensions", p.Size());

        Job job = Job.getInstance(conf, "KMeans");

        job.setJarByClass(KMeansHadoop.class);
        job.setMapperClass(KMeansMapper.class);
        job.setReducerClass(KMeansReducer.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Point.class);

        job.setNumReduceTasks(centroids.size());


        FileInputFormat.addInputPath(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        // add stop conditions
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
