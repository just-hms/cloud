package it.unipi.hadoop;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;

import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import it.unipi.hadoop.KMeansHadoop.KMeansMapper;
import it.unipi.hadoop.KMeansHadoop.KMeansReducer;

public class Main {
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
