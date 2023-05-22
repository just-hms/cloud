package it.unipi.hadoop;

import java.io.IOException;
import java.util.ArrayList;
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
    public static class KMeansMapper extends Mapper<Object, Text, IntWritable, Text> {
        private List<Point> centroids;

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            String[] centroidString = context.getConfiguration().getStrings("centroids", "");

            // TODO: check me
            for (String value : centroidString) {
                centroids.add(Point.parsePoint(value));
            }
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            // Parse data point from the input value
            Point p = Point.parsePoint(value.toString());

            // Find the nearest centroid for the data point
            int nearestCentroidIndex = p.nearest(centroids);

            // Emit the nearest centroid index and data point
            context.write(new IntWritable(nearestCentroidIndex), value);
        }
    }

    public static class KMeansReducer extends Reducer<IntWritable, Text, IntWritable, Text> {
        @Override
        public void reduce(IntWritable key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            // Accumulate all data points for the same centroid
            List<Point> points = new ArrayList<>();
            for (Text value : values) {
                Point p = Point.parsePoint(value.toString());
                points.add(p);
            }

            Point newCentroid = Point.center(points);

            // Emit the new centroid
            context.write(key, new Text(newCentroid.toString()));
        }
    }

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration();

        // TODO get the centroids from a file
        conf.setStrings("centroids", "0.9, 1.0", "0.9, 1.0");

        Job job = Job.getInstance(conf, "KMeans Clustering");
        job.setJarByClass(KMeansHadoop.class);
        job.setMapperClass(KMeansMapper.class);
        job.setReducerClass(KMeansReducer.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
