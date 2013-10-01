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
			myScanner = new Scanner(System.in).useDelimiter(" *\\| *");		
		}

		private LinkedBlockingQueue<String[]> parseLine () {
			LinkedBlockingQueue<String[]> in = new LinkedBlockingQueue<String[]>();
			LinkedBlockingQueue<String[]> out = new LinkedBlockingQueue<String[]>();
			while (myScanner.hasNext()) {
				in = out;
				out = new LinkedBlockingQueue<String[]>();
				String[] nameAndArgs = myScanner.next().split(" ");
				if (nameAndArgs[0].equals("cat")) {
					String[] catArgs = new String[nameAndArgs.length-1];
					for (int i = 1; i < nameAndArgs.length; i++) {
						catArgs[i] = nameAndArgs[i];
					}
					(new Thread(new Cat(out, catArgs))).start();
				} else if (nameAndArgs[0].equals("lc")) {
					// do stuff with other commands
				} else {
					System.out.printf("Command not recognized: %s", nameAndArgs[0]);
				}
			}
			return out;
		}

	}

	public static void main (String[] args) {
		String[] commands = {"cat"};
		CommandLine.Parser myParser = new CommandLine.Parser(commands);
		System.out.print("> ");
		LinkedBlockingQueue<String[]> output = myParser.parseLine();
		while(!output.peek()[1].equals("done")) {
			try {
				System.out.println(output.take());
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				// do nothing for now
			}
		}

	}
}