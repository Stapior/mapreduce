import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import java.io.IOException;

public class PersonsMapReduce {



    public static class Map extends Mapper<LongWritable, Text, Text, Count> {
        private Text resultKey = new Text();
        private Count actor =  new Count(1, 0);
        private Count director = new Count(0, 1);
        private Count other = new Count(0,0);

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
             try {

                 if (key.get() == 0) {
                     return;
                 }
                 String line = value.toString();
                 String[] tokens = line.split("\t");
                 resultKey.set(tokens[2]);
                 if (tokens[3].equals("actor")) {
                     context.write(resultKey,actor);
                 }else  if (tokens[3].equals("director")){
                     context.write(resultKey, director);
                 }else {
                     context.write(resultKey, other);
                 }
             }catch (Exception e){
                 e.printStackTrace();
             }
        }
    }

    public static class Reduce extends Reducer<Text, Count, Text, Count> {
        @Override
        public void reduce(Text key, Iterable<Count> values, Context context) {
            try {
                Count result = new Count();
                for (Count count : values) {
                    try {
                        result.addCounts(count);
                    } catch (Exception e) {
                    }
                }
                context.write(key, result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = new Job(conf, "Persons");

        job.setJarByClass(PersonsMapReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Count.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);
        job.setCombinerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

}