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
        private final static String actor = "1\t0";
        private final static String director = "0\t1";
        private final static String another = "0\t0";
        private Text resultKey = new Text();

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            try {
                if (key.get() == 0) {
                    return;
                }
                String line = value.toString();
                String[] tokens = line.split("\t");
                String result = another;
                if (tokens[3].equals("actor")) {
                    result = actor;
                }
                if (tokens[3].equals("director")) {
                    result = director;
                }
                resultKey.set(tokens[2]);
                context.write(resultKey, new Count());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static class Reduce extends Reducer<Text, Count, Text, Count> {

        public void reduce(Text key, Iterable<Text> values, Context context) {
            try {
                int sum = 0;
                int sum2 = 0;
                for (Text val : values) {
                    try {
                        String value = val.toString();
                        String[] counts = value.split("\t");
                        sum += Integer.getInteger(counts[0]);
                        sum2 += Integer.getInteger(counts[1]);
                    } catch (Exception e) {
                    }

                }
                String result = sum + "\t" + sum2;
                context.write(key, new Count());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();

        Job job = new Job(conf, "persons");

        job.setJarByClass(PersonsMapReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(Map.class);
        job.setReducerClass(Reduce.class);

        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.waitForCompletion(true);
    }

}