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

		private LinkedBlockingQueue<String[]> parseLine () {
			LinkedBlockingQueue<String[]> in = new LinkedBlockingQueue<String[]>();
			LinkedBlockingQueue<String[]> out = new LinkedBlockingQueue<String[]>();
			String[] userCommands = myScanner.nextLine().split(" *\\| *");
			for (int i = 0; i < userCommands.length; i++) {
				in = out;
				out = new LinkedBlockingQueue<String[]>();
				String[] nameAndArgs = userCommands[i].split(" ");
				if (nameAndArgs[0].equals("cat")) {
					String[] catArgs = new String[nameAndArgs.length-1];
					//System.out.println(catArgs.length);
					for (int j = 1; j < nameAndArgs.length; j++) {
						//System.out.println(j);
						catArgs[j-1] = nameAndArgs[j];
					}
					(new Thread(new Cat(out, catArgs))).start();
				} else if (nameAndArgs[0].equals("lc")) {
					if (nameAndArgs.length > 1) {
						System.out.println("Error: lc does not take any arguments.");
						break; // replace with something more substantial
					} else {
						(new Thread(new LineCount(in, out))).start();
					}
					// do stuff with other commands
				} else {
					System.out.printf("Command not recognized: %s\n", nameAndArgs[0]);
				}
			}
			/*while (myScanner.hasNext()) {
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
			}*/
			return out;
		}

	}

	public static void main (String[] args) {
		String[] commands = {"cat", "lc"};
		CommandLine.Parser myParser = new CommandLine.Parser(commands);
		System.out.print("> ");
		LinkedBlockingQueue<String[]> output = myParser.parseLine();
		Boolean outputDone = false;
		while(!outputDone) {
			System.out.print("> ");
			try {
				String[] data = output.take();
				System.out.println(data[0]);
				outputDone = data[1].equals("done");
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				// do nothing for now
			}
		}

	}
}