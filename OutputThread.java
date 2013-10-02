import java.util.concurrent.*;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.File;

public class OutputThread extends EndFilter {
	private PrintStream myOutputStream;

	public OutputThread(LinkedBlockingQueue<String[]> in, String filename) throws FileNotFoundException {
		super(in);
		myOutputStream = new PrintStream(new File(filename));
	}

	public OutputThread(LinkedBlockingQueue<String[]> in) {
		super(in);
		myOutputStream = new PrintStream(System.out);
	}

	public void transform(String[] data) {
		myOutputStream.println(data[0]);
		super.done = data[1].equals("done");
	}
}