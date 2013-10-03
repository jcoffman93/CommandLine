import java.util.Scanner;
import java.util.concurrent.*;

class CommandLine {

	static class Parser {
		private String[] commandList;
		private final int COMMAND_LIST_SIZE;
		private Scanner myScanner;

		private Parser (String[] commands) {
			COMMAND_LIST_SIZE = commands.length;
			commandList = new String[COMMAND_LIST_SIZE];
			for (int i = 0; i < COMMAND_LIST_SIZE; i++) {
				commandList[i] = commands[i];
			}
			myScanner = new Scanner(System.in);		
		}

		private String[] parseLine () {
			return myScanner.nextLine().split(" *\\| *");

		}
	}

	private static LinkedBlockingQueue<String[]> startThreads(String[] userCommands) {
		LinkedBlockingQueue<String[]> in = new LinkedBlockingQueue<String[]>();
		LinkedBlockingQueue<String[]> out = new LinkedBlockingQueue<String[]>();
		for (int i = 0; i < userCommands.length; i++) {
			String[] nameAndArgs = userCommands[i].split(" ", 2);
			in = out;
			out = new LinkedBlockingQueue<String[]>();
			if (nameAndArgs[0].equals("cat")) {
				(new Thread(new Cat(out, nameAndArgs[1].split(" ")))).start();
			} else if (nameAndArgs[0].equals("lc")) {
				if (nameAndArgs.length > 1) {
					System.out.println("Error: lc does not take any arguments.");
					break; // replace with something more substantial
				} else {
					(new Thread(new LineCount(in, out))).start();
				}
				// do stuff with other commands
			} else {
				System.out.printf("Command not recognized: %s\n", userCommands[0]);
				return null;
			}
		}
		return out;
	}

	public static void main (String[] args) {
		String[] commands = {"cat", "lc"};
		CommandLine.Parser myParser = new CommandLine.Parser(commands);
		while(true) {
			System.out.print("> ");
			LinkedBlockingQueue<String[]> output = startThreads(myParser.parseLine());
			if (output != null) {
				Thread myOutputHandler = new Thread(new OutputHandler(output));
				myOutputHandler.start();
				try {
					myOutputHandler.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					break; // Actually handle exception later.
				}
			}
		}
	}
}