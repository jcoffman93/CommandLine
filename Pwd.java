import java.util.concurrent.*;

public class Pwd extends StartFilter {
	private String currentDirectory;

	public Pwd (LinkedBlockingQueue<String[]> out, String currentDirectory) {
		super(out);
		this.currentDirectory = currentDirectory;
	}

	public String[] transform () {
		String[] result = { currentDirectory, "END" };
		super.done = true;
		return result;
	}
}