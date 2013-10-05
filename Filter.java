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
            try {
                data = in.take();  // read from input queue, may block
                data = transform(data); // allow filter to change message
                out.put(data);       // forward to output queue
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }
    
    protected abstract String[] transform(String[] data);
}
