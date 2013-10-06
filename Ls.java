import java.util.concurrent.LinkedBlockingQueue;
import java.io.File;

public class Ls extends StartFilter {
	String[] files;
	int currentIndex = -1;

	public Ls (LinkedBlockingQueue<String[]> out, String path) {
		super(out);
		files = new File(path).list();
	}

	public String[] transform () {
		currentIndex++;
		if (currentIndex < files.length - 1) {
			String[] result = { files[currentIndex], "" };
			return result;
		}
		String[] result = { files[currentIndex], "END" };
		super.done = true;
		return result;
	}
}