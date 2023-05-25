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


    public static void main(String[] args) throws Exception {

        // get centroids
        List<String> lines = Files.readAllLines(Paths.get(args[0]));

        // get starting centroids
        Point[] centroids = new Point[lines.size()];
        for (int i = 0; i < centroids.length; i++) {
            centroids[i] = Point.fromCSV(lines.get(i));
        }
        
        // set the costants
        final int maxIteration = 5;
        final Double stoppingTreshold = 0.001;
        final int dimensions = centroids[0].size();
        final int K = centroids.length;

        // start k-means
        int i = 0;
        for (; i < maxIteration; i++) {
            
            // infer configuration from centroids
            Configuration conf = new Configuration();

            // convert the centroids to string
            String[] centroidCfg = new String[K];
            for (int j = 0; j < K; j++) {
                centroidCfg[j] = centroids[j].toCSV();
            }  

            // add config to run
            conf.setStrings("centroids", centroidCfg);
            conf.setInt("dimensions", dimensions);

            Job job = Job.getInstance(conf, "KMeans");

            job.setJarByClass(KMeansHadoop.class);
            job.setMapperClass(KMeansMapper.class);
            job.setReducerClass(KMeansReducer.class);

            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(Point.class);

            // set the number of reducer to K
            job.setNumReduceTasks(K);


            // set the input as provided by the user
            Path input = new Path(args[1]);
            // each iteration create a subfolder for the output
            Path output = new Path(args[2], "iteration"+(i+1));

            FileInputFormat.addInputPath(job, input);
            FileOutputFormat.setOutputPath(job, output);

            Boolean res = job.waitForCompletion(true);
            if (!res) {
                System.err.println("Error during iteration " + i);
                // error during job
                System.exit(1);
            }

            // extract the hadoop's run outputs
            FileSystem fs = FileSystem.get(conf);
            Point[] newcentroids = new Point[K];
            
            for (FileStatus file : fs.listStatus(output)) {
                // if not a file skip it
                if (!file.isFile()) {
                    continue;
                }
                // skip the success file
                if (file.toString() == "_SUCCESS") {
                    continue;
                }

                // Open an input stream for reading the file
                Path filePath = file.getPath();
                BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(filePath)));
                String line = br.readLine();

                // each file should have one line formatted like this
                //  ```
                //  key1    1.01,23.31,-12
                //  key2    ...
                //  ```

                newcentroids[i] = Point.fromCSV(line.split("\t")[1]);

                // Close the input stream
                br.close();
            }

            // get the distance between the new and the last centroids
            Double distance = 0.0;
            for (int j = 0; j < K; j++) {
                distance+=Math.abs(newcentroids[i].distance2(centroids[i]));
            }

            // if it's less exit gracefully
            if((distance / K) < stoppingTreshold){
                break;
            }

            // if not do another iteration with the new centroids
            centroids = newcentroids;
        }
        
        System.out.println(centroids);            
    }
}
