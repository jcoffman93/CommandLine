import java.util.concurrent.*;

public class PrevCommand extends Filter {

	public PrevCommand (LinkedBlockingQueue<String[]> in, LinkedBlockingQueue<String[]> out) {
		super(in, out);
	}

	public String[] transform(String[] data) {
		super.done = data[1].equals("END");
		return data;
	}
}