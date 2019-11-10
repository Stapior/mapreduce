import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class Count implements WritableComparable<Count> {
    private IntWritable actor;
    private IntWritable director;

    public Count() {
        set(new IntWritable(0), new IntWritable(0));
    }

    public Count(int actor, int director) {
        set(new IntWritable(actor), new IntWritable(director));
    }

    public void set(IntWritable actor, IntWritable director) {
        this.actor = actor;
        this.director = director;
    }


    public void addCounts(Count count) {
        actor.set(actor.get() + count.getActor().get());
        director.set(director.get() + count.getDirector().get());
    }

    public IntWritable getActor() {
        return actor;
    }

    public void setActor(IntWritable actor) {
        this.actor = actor;
    }

    public IntWritable getDirector() {
        return director;
    }

    public void setDirector(IntWritable director) {
        this.director = director;
    }

    @Override
    public int compareTo(Count o) {
        int comparison = actor.compareTo(o.actor);
        if (comparison != 0) {
            return comparison;
        }
        return director.compareTo(o.director);
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        actor.write(dataOutput);
        director.write(dataOutput);
    }



    @Override
    public void readFields(DataInput dataInput) throws IOException {
        actor.readFields(dataInput);
        director.readFields(dataInput);
    }

    @Override
    public String toString() {
        return  actor.toString() +
                "\t" + director.toString() ;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Count count = (Count) o;
        return Objects.equals(actor, count.actor) &&
                Objects.equals(director, count.director);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actor, director);
    }
}