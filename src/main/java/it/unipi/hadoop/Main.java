package it.unipi.hadoop;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;

public class Main {


    private static void log(String x){
        System.out.println("[\033[1;96mKMEANS\033[0m] " + x);
    }

    private static void logErr(String x){
        System.out.println("[\033[1;91mKMEANS\033[0m] " + x);
    }
    
    public static void main(String[] args) throws Exception{
        // build file writers
        PrintWriter dump = new PrintWriter(new BufferedWriter(new FileWriter("hadoop.out.csv")));
        PrintWriter dump_stats = new PrintWriter(new BufferedWriter(new FileWriter("hadoop.stats")));

        // get centroids
        List<String> lines = Files.readAllLines(Paths.get(args[0]));

        // get starting centroids
        Point[] centroids = lines.stream()
                .map(Point::parsePoint)
                .toArray(Point[]::new);
        
        // set the costants
        final int maxIter = 30;
        final double tol = 0.0001;
        final int K = centroids.length;

        // start k-means
        Instant start = Instant.now();
        int iteration = 0;
        for (; iteration < maxIter; iteration++) {
            log("-------------------------------------------");
            log("Running iteration " + iteration);
        
            // infer configuration from centroids
            Configuration cfg = new Configuration();

            // convert the centroids to string
            String[] centroidsValue = new String[K];
            for (int j = 0; j < K; j++) {
                centroidsValue[j] = centroids[j].toString();
            }  
            // add config to run
            cfg.setStrings("centroids", centroidsValue);

            // set the input as provided by the user
            Path input = new Path(args[1]);
            // each iteration create a subfolder for the output
            Path output = new Path(args[2], "iteration"+(iteration+1));

            Job job = HadoopUtil.createKMeansJob(cfg, input, output, K);

            Boolean res = job.waitForCompletion(true);
            if (!res) {
                logErr("Error during iteration " + iteration);
                dump_stats.close();
                dump.close();
                // error during job
                System.exit(1);
            }

            // read out the output
            Point[] newcentroids = HadoopUtil.extractResult(cfg, output, K);

            // get the distance between the new and the last centroids
            float distance = 0.0f;
            for (int j = 0; j < K; j++) {
                distance+=newcentroids[j].distance(centroids[j]);
            }
            distance/=K;

            log("Current distance: " + distance);

            // if it's less then the stoppingTreshold exit gracefully
            if(distance < tol){
                log("StoppingTreshold reached");            
                break;
            }

            // if not do another iteration with the new centroids
            centroids = newcentroids;
        }
        Duration executionTime = Duration.between(start, Instant.now());
        
        // write the centroids to a csv file
        for (Point centroid : centroids) {
            dump.println(centroid.toString());
        }
        dump.close();

        // write down some stats
        dump_stats.println("Execution Time: " + executionTime.toMillis() + "ms");
        dump_stats.println("Number of Iterations: " + (iteration + 1));
        dump_stats.close();

    }
}
