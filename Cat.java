import java.util.concurrent.*;
import java.io.*;
import java.util.Scanner;

public class Cat extends StartFilter {
	private int currentFile;
	private String[] myFileNames;
	private Scanner myScanner;

	public Cat(LinkedBlockingQueue out, String[] filenames) {
		super(out);
		myFileNames = new String[filenames.length];
		for (int i = 0; i < filenames.length; i++) {
			myFileNames[i] = filenames[i];
		}
		currentFile = 0;
		try {
			myScanner = new Scanner(new File(myFileNames[currentFile]));
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public String[] transform() {
		if (myScanner.hasNextLine()) {
			String[] temp = {myScanner.nextLine(), "going"};
			return temp;
		} else {
			if (currentFile <= myFileNames.length-1) {
				currentFile++;
				try {
					myScanner = new Scanner(new File(myFileNames[currentFile]));
				} catch (FileNotFoundException e) {
					System.out.printf("Could not open file: %s", myFileNames[currentFile]);
				}
			} else {
				super.done = true;
				String[] temp = {"", "done"};
				return temp;
			}
		}
		String[] temp = {"", "error"};
		return temp;

	}

}