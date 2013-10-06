import java.util.Scanner;
import java.util.concurrent.*;
import java.util.ArrayList;
import java.io.FileNotFoundException;

class CommandLine {
	private ArrayList<Thread> commandList;
	private ArrayList<String[]> commandHistory;
	private String currentDirectory;
	private boolean exitRepl = false;
	private Scanner myScanner;

	private CommandLine() {
		commandHistory = new ArrayList<String[]>();
		commandList = new ArrayList<Thread>();
		currentDirectory = System.getProperty("user.dir");
		myScanner = new Scanner(System.in);
	}

	private void exit() {
		exitRepl = true;
	}

	public static void commandUsage(String commandName) {
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

	private void handleException(Exception e, String commandName, String[] commandArgs) {
		// do something
	}

	/*private String changeDirectory (String path) {
		// do something
	}*/

	private String[] parseLine () {
		return myScanner.nextLine().split(" *\\| *");
	}

	private Thread startThread(String[] nameAndArgs, LinkedBlockingQueue<String[]> input, 
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
		} else if (nameAndArgs[0].equals("ls")) {
			commandThread = new Thread(new Ls(output, currentDirectory));
		} else if (nameAndArgs[0].equals("exit")) {
			exit();
		} else {
			System.out.printf("Command not recognized: %s\n", nameAndArgs[0]);
		}
		if (commandThread != null) commandThread.start();
		return commandThread;
	}

	private LinkedBlockingQueue<String[]> startAllThreads(String[] userCommands) {
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
		String[] userCommands;
		CommandLine myCmd = new CommandLine();
		while(!myCmd.exitRepl) {
			System.out.print("> ");
			userCommands = myCmd.parseLine();
			LinkedBlockingQueue<String[]> output = myCmd.startAllThreads(userCommands);
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
			myCmd.commandHistory.add(userCommands);
			myCmd.commandList.clear();
		}
	}
}