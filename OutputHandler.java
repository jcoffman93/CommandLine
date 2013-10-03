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
		myOutputStream.println(data[0]);
		super.done = data[1].equals("done");
	}
}