package it.unipi.hadoop;

import org.apache.hadoop.fs.FileStatus;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
        List<Point> newcentroids = new ArrayList<Point>();

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
            String line = br.readLine();

            if (line == null) {
                throw new IOException("one centroid is empty");
            };

            // each file should have one line formatted like this
            // ```
            // key1 1.01;23.31;-12
            // ```
            
            newcentroids.add(Point.parsePoint(line.split("\t")[1]));

            // Close the input stream
            br.close();
        }

        return newcentroids.toArray(new Point[0]);
    }

    public static Job createKMeansJob(Configuration conf, Path input, Path output, int K) throws IOException {
        Job job = Job.getInstance(conf, "KMeans");
        job.setJarByClass(KMeansHadoop.class);
        job.setMapperClass(KMeansMapper.class);

        // job.setCombinerClass(KMeansReducer.class);
        job.setReducerClass(KMeansReducer.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Point.class);

        // Set the number of reducers to K
        job.setNumReduceTasks(K);

        FileInputFormat.addInputPath(job, input);
        FileOutputFormat.setOutputPath(job, output);
        return job;
    }
}
