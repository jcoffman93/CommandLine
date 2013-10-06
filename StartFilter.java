import java.util.concurrent.*;

public abstract class StartFilter implements Runnable {
    protected LinkedBlockingQueue<String[]> out;
    protected volatile boolean done;
    public StartFilter(LinkedBlockingQueue<String[]> out) {
        this.out = out;
        this.done = false;
    }
    
    public void run() {
        String[] data;
        while(! this.done) {
            data = transform(); // allow filter to change message
            try {
                out.put(data); // forward to output queue
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                return;
            } 
        }
    }
    
    protected abstract String[] transform();
}
