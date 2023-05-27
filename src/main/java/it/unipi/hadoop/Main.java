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
        System.out.println("\033[1;96m[KMEANS]\033[0m " + x);
    }

    
    
    public static void main(String[] args) throws Exception{
        // build file writers
        PrintWriter log_out = new PrintWriter(new BufferedWriter(new FileWriter("hadoop.out.csv")));
        PrintWriter log_stats = new PrintWriter(new BufferedWriter(new FileWriter("hadoop.stats")));

        // get centroids
        List<String> lines = Files.readAllLines(Paths.get(args[0]));

        // get starting centroids
        Point[] centroids = lines.stream()
                .map(Point::parsePoint)
                .toArray(Point[]::new);
        
        // set the costants
        final int maxIteration = 5;
        final Double stoppingTreshold = 0.001;
        final int K = centroids.length;

        // start k-means
        Instant start = Instant.now();
        int iterations = 0;
        for (; iterations < maxIteration; iterations++) {
            log("Running iteration " + iterations);
        
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
            Path output = new Path(args[2], "iteration"+(iterations+1));

            Job job = HadoopUtil.createKMeansJob(cfg, input, output, K);

            Boolean res = job.waitForCompletion(true);
            if (!res) {
                System.err.println("Error during iteration " + iterations);
                // error during job
                System.exit(1);
            }

            // read out the output
            Point[] newcentroids = HadoopUtil.extractResult(cfg, output, K);

            // get the distance between the new and the last centroids
            Double distance = 0.0;
            for (int j = 0; j < K; j++) {
                distance+=Math.abs(newcentroids[iterations].distance(centroids[iterations]));
            }
            distance/=K;

            log("Current distance: " + distance);
            log("-------------------------------------------");

            // if it's less then the stoppingTreshold exit gracefully
            if(distance < stoppingTreshold){
                log("StoppingTreshold reached");            
                break;
            }

            // if not do another iteration with the new centroids
            centroids = newcentroids;
        }
        Duration executionTime = Duration.between(start, Instant.now());
        
        // write the centroids to a csv file
        for (Point centroid : centroids) {
            log_out.println(centroid.toString());
        }
        log_out.close();

        // write down some stats
        log_stats.println("Execution Time: " + executionTime.toMillis() + "ms");
        log_stats.println("Number of Iterations: " + iterations);
        log_stats.close();

    }
}
