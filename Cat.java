import java.util.concurrent.*;
import java.io.*;
import java.util.Scanner;
import java.nio.file.Path;
public class Cat extends StartFilter {
	private int currentFile;
	private String[] myFileNames;
	private Scanner myScanner;
	private Path workingDirectory;

	public Cat(LinkedBlockingQueue<String[]> out, Path workingDirectory, String[] filenames) 
		throws FileNotFoundException {
		super(out);
		myFileNames = filenames;
		currentFile = 0;
		this.workingDirectory = workingDirectory;
		try {
			myScanner = new Scanner(new File((workingDirectory.resolve(myFileNames[currentFile])).toString()));
		} catch (FileNotFoundException e) {
			System.out.printf("Could not open file: %s", myFileNames[currentFile]);
			throw new RuntimeException();
		}
	}

	public String[] transform() {
		if (myScanner.hasNextLine()) {
			String[] temp = { myScanner.nextLine(), "" };
			return temp;
		} else {
			if (currentFile < myFileNames.length-1) {
				currentFile++;
				try {
					myScanner = new Scanner(new File((workingDirectory.resolve(myFileNames[currentFile])).toString()));
				} catch (FileNotFoundException e) {
					System.out.printf("Could not open file: %s", myFileNames[currentFile]);
					throw new RuntimeException();
				}
			} else {
				super.done = true;
				String[] temp = { "", "END" };
				return temp;
			}
		}
		String[] temp = { "", "ERROR" };
		return temp;

	}

}