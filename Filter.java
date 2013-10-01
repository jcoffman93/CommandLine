import java.util.concurrent.*;

public abstract class Filter implements Runnable {

    protected LinkedBlockingQueue<String[]> in;
    protected LinkedBlockingQueue<String[]> out;
    protected volatile boolean done;
    
    public Filter(LinkedBlockingQueue<String[]> in, LinkedBlockingQueue<String[]> out) {
        this.in = in;
        this.out = out;
        this.done = false;
    }
    
    public void run() {
        String[] data;
        while(! this.done) {
            data = in.take().clone();  // read from input queue, may block
            data = transform(data).clone(); // allow filter to change message
            out.put(data.clone());       // forward to output queue
        }
    }
    
    protected abstract String[] transform(String[] data);
}
