import java.util.Scanner;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.io.FileNotFoundException;

class CommandLine {
	private static ArrayList<Thread> commandList;
	private static ArrayList<String[]> commandHistory;
	static class Parser {
		private String[] commandList;
		private Scanner myScanner;

		private Parser (String[] commands) {
			/*COMMAND_LIST_SIZE = commands.length;
			commandList = new String[COMMAND_LIST_SIZE];
			for (int i = 0; i < COMMAND_LIST_SIZE; i++) {
				commandList[i] = commands[i];
			}*/
			myScanner = new Scanner(System.in);		
		}
		// CLEAN THIS UP
		private String[] parseLine () {
			return myScanner.nextLine().split(" *\\| *");
			/*String[] temp = commands[commands.length-1].split(" *> *");
			String[] result = new String[commands.length + 1];
			for (int i = 0; i < commands.length; i++) {
				result[i] = commands[i];
			}
			result[result.length-2] = temp[0];
			result[result.length-1] = temp[1];
			return result;*/
		}
	}

	private void commandUsage(String commandName) {
		// do something
	}

	private static void handleException(Exception e, String commandName, String[] commandArgs) {
		// do something
	}

	private static Thread startThread(String[] nameAndArgs, LinkedBlockingQueue<String[]> input, 
		LinkedBlockingQueue<String[]> output) {
		Thread commandThread = null;
		if (nameAndArgs[0].equals("cat")) {
			try {
				commandThread = new Thread(new Cat(output, nameAndArgs[1].split(" ")));
			} catch (FileNotFoundException e) {
				handleException(e, "cat", nameAndArgs[1].split(" "));
			}
		} else if (nameAndArgs[0].equals("lc")) {
			commandThread = new Thread(new LineCount(input, output));
		} else if (nameAndArgs[0].equals("grep")) {
			commandThread = new Thread(new Grep(input, output, nameAndArgs[1]));
		} else if (nameAndArgs[0].contains("!")) {
			int commandIndex = Integer.parseInt(nameAndArgs[0].substring(1));
			LinkedBlockingQueue<String[]> out = startAllThreads(commandHistory.get(commandIndex));
			commandThread = new Thread(new PrevCommand(out, output));
		} else {
			System.out.printf("Command not recognized: %s\n", nameAndArgs[0]);
		}
		if (commandThread != null) commandThread.start();
		return commandThread;
	}

	private static LinkedBlockingQueue<String[]> startAllThreads(String[] userCommands) {
		LinkedBlockingQueue<String[]> in = new LinkedBlockingQueue<String[]>();
		LinkedBlockingQueue<String[]> out = new LinkedBlockingQueue<String[]>();
		for (int i = 0; i < userCommands.length; i++) {
			in = out;
			out = new LinkedBlockingQueue<String[]>();
			Thread commandThread = startThread(userCommands[i].split(" ", 2), in, out);
			if (commandThread != null) {
				commandList.add(commandThread);
			} else {
				return null;
			}
		}
		return out;
	}

	public static void main (String[] args) {
		String[] commands = {"cat", "lc"};
		commandList = new ArrayList<Thread>();
		commandHistory = new ArrayList<String[]>();
		CommandLine.Parser myParser = new CommandLine.Parser(commands);
		while(true) {
			System.out.print("> ");
			commandHistory.add(myParser.parseLine());
			LinkedBlockingQueue<String[]> output = startAllThreads(
				commandHistory.get(commandHistory.size()-1));
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