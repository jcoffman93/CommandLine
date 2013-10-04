import java.util.concurrent.*;

public class Grep extends Filter{
	String searchString;

	public Grep (LinkedBlockingQueue<String[]> in, LinkedBlockingQueue<String[]> out, String searchString) {
		super(in, out);
		this.searchString = searchString;
	}

	public String[] transform(String[] data) {
		super.done = data[1].equals("done");
		String matchedString = data[0].contains(searchString) ? data[0] : "";
		String[] result = { matchedString, data[1] };
		return result;
	}
}