package it.unipi.hadoop;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import it.unipi.hadoop.KMeansHadoop.KMeansMapper;
import it.unipi.hadoop.KMeansHadoop.KMeansReducer;
import org.apache.hadoop.fs.FileSystem;

public class Main {

    static int MAX_ITERATION = 5;
    static Double STOP_TRESHOLD = 0.001;

    public static void main(String[] args) throws Exception {

        // get centroids
        List<String> centroids = Files.readAllLines(Paths.get(args[0]));
        Point p = Point.fromCSV(centroids.get(0));

        // infer configuration from centroids
        Configuration conf = new Configuration();
        conf.setStrings("centroids", centroids.toArray(new String[0]));
        conf.setInt("dimensions", p.Size());

        for (int i = 0; i < MAX_ITERATION; i++) {

            Job job = Job.getInstance(conf, "KMeans");

            job.setJarByClass(KMeansHadoop.class);
            job.setMapperClass(KMeansMapper.class);
            job.setReducerClass(KMeansReducer.class);

            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(Point.class);
            job.setNumReduceTasks(centroids.size());
            // get the centroids from the last input
            
            // TODO something with path combine to have a single output folder
            // String input = (i == 0) ? args[1] : args[2] + i;
            // String output = args[2] + (i + 1);

            String input = args[1];
            String output = args[2];

            // fix me
            FileInputFormat.addInputPath(job, new Path(input));
            FileOutputFormat.setOutputPath(job, new Path(output));

            Boolean res = job.waitForCompletion(false);

            if (!res) {
                // error during job
                System.exit(1);
            }
            
            FileSystem fs = FileSystem.get(conf);
            for (FileStatus file : fs.listStatus(new Path(output))) {
                if (!file.isFile()) {
                    continue;
                }

                // Open an input stream for reading the file
                Path filePath = file.getPath();
                BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(filePath)));
                String line;
                while ((line = br.readLine()) != null) {
                    // Process the output line
                    System.out.println(line);
                }
                // Close the input stream
                br.close();
            }
            break;
        }
    }
}
