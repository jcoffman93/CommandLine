import java.util.Scanner;

public class LineCount extends Filter {
	private int numLines;
	private Scanner myScanner;

	public LineCount(LinkedBlockingQueue in,  LinkedBlockingQueue out) {
		super(in, out);
		numLines = 0;
	}

	public Object transform(Object o) {
		String data = (String) o;
		myScanner = new Scanner(data);
		myScanner
	}
}