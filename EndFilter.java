import java.util.concurrent.*;

public abstract class EndFilter implements Runnable {

    protected LinkedBlockingQueue<String[]> in;
    protected volatile boolean done;
    
    public EndFilter(LinkedBlockingQueue<String[]> in) {
        this.in = in;
        this.done = false;
    }
    
    public void run() {
        String[] data;
        while(! this.done) {
            try {
                data = in.take().clone();  // read from input queue, may block
                transform(data);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                break;
                // Actually handle exception
            }
        }
    }
    
    protected abstract void transform(String[] data);
}