import java.util.concurrent.*;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.File;

public class OutputHandler extends EndFilter {
	private PrintStream myOutputStream;

	public OutputHandler(LinkedBlockingQueue<String[]> in, String filename) throws FileNotFoundException {
		super(in);
		myOutputStream = new PrintStream(new File(filename));
	}

	public OutputHandler(LinkedBlockingQueue<String[]> in) {
		super(in);
		myOutputStream = new PrintStream(System.out);
	}

	public void transform(String[] data) {
		if (!data[0].equals("")) {
			myOutputStream.println(data[0]);
		}
		if (data[1].equals("END")) {
			super.done = true;
		}
	}
}