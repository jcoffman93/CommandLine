import java.util.Scanner;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.io.FileNotFoundException;

class CommandLine {
	private static ArrayList<Thread> commandList;
	private static ArrayList<String[]> commandHistory;
	private static String currentDirectory;
	private static boolean exitRepl = false;

	private String[] parseLine () {
		return myScanner.nextLine().split(" *\\| *");
	}
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

	private static void exit() {
		exitRepl = true;
	}

	private void commandUsage(String commandName) {
		// do something
		/*if (commandName.equals("cat")) {
			System.out.println("Command - cat\n
				Usage - cat takes at least one filename and outputs its contents, line by line.\n
				Example - cat filename1 [filename2, filename3...]");
		} else if (commandName.equals("lc")) {
			System.out.println("Command - lc\n
				Usage - lc takes no arguments. It counts the numberof lines it receives as input.\n
				Example - lc");
		} else if (commandName.equals("grep")) {
			System.out.println("Command - grep\n
				Usage - grep takes a string and returns each line containing that string.\n 
				Example - grep hello world");
		} else if (commandName.equals("!n")) {
			System.out.println("Command - !n\n
				Usage - !n takes no arguments. It executes the nth command executed during a session.\n
				Example - !2");
		}*/
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
			LinkedBlockingQueue<String[]> out = startAllThreads(commandHistory.get(commandIndex - 1));
			commandThread = new Thread(new PrevCommand(out, output));
		} else if (nameAndArgs[0].equals("history")) {
			commandThread = new Thread(new History(output, commandHistory));
		} else if (nameAndArgs[0].equals("pwd")) {
			commandThread = new Thread(new Pwd(output, currentDirectory));
		} else if (nameAndArgs[0].equals("exit")) {
			exit();
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
		String[] commands = {""};
		commandHistory = new ArrayList<String[]>();
		commandList = new ArrayList<Thread>();
		currentDirectory = System.getProperty("user.dir");
		String[] userCommands;
		CommandLine.Parser myParser = new CommandLine.Parser(commands);
		while(!exitRepl) {
			System.out.print("> ");
			userCommands = myParser.parseLine();
			LinkedBlockingQueue<String[]> output = startAllThreads(userCommands);
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
			commandHistory.add(userCommands);
			commandList.clear();
		}
	}
}