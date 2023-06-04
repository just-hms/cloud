package it.unipi.hadoop;

import org.apache.hadoop.fs.FileStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.hadoop.fs.FileSystem;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.io.IntWritable;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import it.unipi.hadoop.KMeansHadoop.KMeansMapper;
import it.unipi.hadoop.KMeansHadoop.KMeansReducer;;

// HadoopExtension adds utility functions for Hadoop
public class HadoopUtil {

    // extractResult returns an array of point which are the result of the last
    // hadoop run
    public static Point[] extractResult(Configuration conf, Path output, int K) throws IOException {

        // extract the hadoop's run outputs
        FileSystem fs = FileSystem.get(conf);
        Point[] newcentroids = new Point[K];

        for (FileStatus file : fs.listStatus(output)) {
            // if not a file skip it
            if (!file.isFile()) continue;

            // skip the success file
            if (file.getPath().getName().endsWith("_SUCCESS")) {
                continue;
            }

            // Open an input stream for reading the file
            Path filePath = file.getPath();
            BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(filePath)));

            // each file should have one or more line formatted like this
            // ```
            // key1 1.01;23.31;-12
            // ```

            br.lines().forEach(line -> {
                String[] splitted = line.split("\t");
                if (splitted.length != 2){
                    return;
                }
                newcentroids[Integer.parseInt(splitted[0])] = Point.parsePoint(splitted[1]);
            });

            // Close the input stream
            br.close();
        }
        
        return newcentroids;
    }

    public static Job createKMeansJob(Configuration conf, Path input, Path output, int K) throws IOException {
        Job job = Job.getInstance(conf, "KMeans");
        job.setJarByClass(KMeansHadoop.class);
        job.setMapperClass(KMeansMapper.class);

        job.setCombinerClass(KMeansReducer.class);
        job.setReducerClass(KMeansReducer.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Point.class);

        // Set the number of reducers to K
        int maxreducers = 10;        
        job.setNumReduceTasks(K < maxreducers ? K : maxreducers);

        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);
        return job;
    }
}
