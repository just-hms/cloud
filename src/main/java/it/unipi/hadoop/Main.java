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
        List<String> centroidsString = Files.readAllLines(Paths.get(args[0]));
        
        // get starting centroids
        Point[] centroids = new Point[centroidsString.size()];
        for (int i = 0; i < centroids.length; i++) {
            centroids[i] = Point.fromCSV(centroidsString.get(i));
        }

        // start k-means
        int i = 0;
        for (; i < MAX_ITERATION; i++) {
            
            // infer configuration from centroids
            Configuration conf = new Configuration();
            conf.setStrings("centroids", Point.ClusterToCSV(centroids));
            conf.setInt("dimensions", centroids[0].Size());

            Job job = Job.getInstance(conf, "KMeans");

            job.setJarByClass(KMeansHadoop.class);
            job.setMapperClass(KMeansMapper.class);
            job.setReducerClass(KMeansReducer.class);

            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(Point.class);
            job.setNumReduceTasks(centroidsString.size());

            
            // get the centroids from the last input
            Path input;
            if (i == 0) {
                input = new Path(args[1]) ;
            }
            else  {
                input = new Path(args[1], "iteration"+i) ;
            }

            Path output = new Path(args[2], "iteration"+(i+1));

            // fix me
            FileInputFormat.addInputPath(job, input);
            FileOutputFormat.setOutputPath(job, output);

            Boolean res = job.waitForCompletion(false);
            if (!res) {
                // TODO checke me
                System.err.println("Error during iteration " + i);
                // error during job
                System.exit(1);
            }
            
            FileSystem fs = FileSystem.get(conf);
            Point[] newcentroids = new Point[centroidsString.size()];
            
            for (FileStatus file : fs.listStatus(output)) {
                if (!file.isFile()) {
                    continue;
                }
                if (file.toString().endsWith("ESS")) {
                    continue;
                }
                // Open an input stream for reading the file
                Path filePath = file.getPath();
                BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(filePath)));
                String line = br.readLine();

                // TODO: check me but each file should have one line formatted like this
                //  key [p,o,s,i,t,i,o,n]
                newcentroids[i] = Point.fromCSV(line.split("\t")[1]);

                // Close the input stream
                br.close();
            }

            Double distance = 0.0;
            for (int j = 0; j < newcentroids.length; j++) {
                distance+=Math.abs(newcentroids[i].distance(centroids[i]));
            }

            if(distance < STOP_TRESHOLD){
                break;
            }

            centroids = newcentroids;
        }
        
        System.err.println(centroids);

    }
}
