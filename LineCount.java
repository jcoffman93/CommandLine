import java.util.concurrent.*;
import java.util.Scanner;

public class LineCount extends Filter {
	private int numLines;
	private Scanner myScanner;

	public LineCount(LinkedBlockingQueue<String[]> in, LinkedBlockingQueue<String[]> out) {
		super(in, out);
		numLines = 0;
	}

	public String[] transform(String[] data) {
		myScanner = new Scanner(data[0]);
		while (myScanner.hasNextLine()) {
			myScanner.nextLine();
			numLines++;
		}
		if (data[1].equals("END")) {
			super.done = true;
			String[] result = { String.valueOf(numLines), "END" };
			return result;
		} else {
			String [] result = { "", "" };
			return result;
		}
	}
}