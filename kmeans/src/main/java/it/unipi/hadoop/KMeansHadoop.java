package it.unipi.hadoop;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.StringTokenizer;

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
        }

        @Override
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {

            final StringTokenizer itr = new StringTokenizer(value.toString(), "\n");
            while (itr.hasMoreTokens()) {
                String s = itr.nextToken();
                if (s == ""){
                    break;
                }
                
                Point p = Point.parsePoint(s);

                // Find the nearest centroid for the data point
                int idx = p.nearest(centroids);

                // Emit the nearest centroid index and data point
                context.write(new IntWritable(idx), p);
            }
        }
    }

    public static class KMeansReducer extends Reducer<IntWritable, Point, IntWritable, Text> {
        @Override
        protected void reduce(IntWritable key, Iterable<Point> values, Context context)
                throws IOException, InterruptedException {

            Point newCentroid = Point.Average(values);
            context.write(key, new Text(newCentroid.toString()));
        }
    }

    public static void main(String[] args) throws Exception {

        // list of k centroids
        // list of points n
        // every point has d dimensions

        Configuration conf = new Configuration();

        List<String> centroids = Files.readAllLines(Paths.get(args[2]));
        conf.setStrings("centroids", centroids.toArray(new String[0]));

        Job job = Job.getInstance(conf, "KMeans");

        job.setJarByClass(KMeansHadoop.class);
        job.setMapperClass(KMeansMapper.class);
        job.setReducerClass(KMeansReducer.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        // add stop conditions
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
