import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;

class CommandLine {
	private ArrayList<Thread> commandList;
	private ArrayList<String> commandHistory;
	private Path currentDirectory;
	private boolean exitRepl = false;
	private Scanner myScanner;

	private CommandLine() {
		commandHistory = new ArrayList<String>();
		commandList = new ArrayList<Thread>();
		currentDirectory = Paths.get(System.getProperty("user.dir"));
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

	private void changeDirectory (String path) {
		String temp;
		if (path.equals("..")) {
			currentDirectory = currentDirectory.getParent();
		} else {
			if (new File(path).isDirectory()) {
				currentDirectory = currentDirectory.resolve(path);
			} else {
				System.out.printf("%s is not a directory.\n", path);
			}
		}
	}

	private String parseLine () {
		return myScanner.nextLine();
	}

	private Thread startThread(String[] nameAndArgs, LinkedBlockingQueue<String[]> input, 
		LinkedBlockingQueue<String[]> output) {
		Thread commandThread = null;
		if (nameAndArgs[0].equals("cat")) {
			try {
				commandThread = new Thread(new Cat(output, currentDirectory, nameAndArgs[1].split(" ")));
			} catch (FileNotFoundException e) {
				handleException(e, "cat", nameAndArgs[1].split(" "));
			}
		} else if (nameAndArgs[0].equals("lc")) {
			commandThread = new Thread(new LineCount(input, output));
		} else if (nameAndArgs[0].equals("grep")) {
			commandThread = new Thread(new Grep(input, output, nameAndArgs[1]));
		} else if (nameAndArgs[0].contains("!")) {
			int commandIndex = Integer.parseInt(nameAndArgs[0].substring(1));
			LinkedBlockingQueue<String[]> out = startAllThreads(commandHistory.get(commandIndex - 1).split(" *\\| *"));
			commandThread = new Thread(new PrevCommand(out, output));
		} else if (nameAndArgs[0].equals("history")) {
			commandThread = new Thread(new History(output, commandHistory));
		} else if (nameAndArgs[0].equals("pwd")) {
			commandThread = new Thread(new Pwd(output, currentDirectory.toString()));
		} else if (nameAndArgs[0].equals("ls")) {
			commandThread = new Thread(new Ls(output, currentDirectory.toString()));
		} else if (nameAndArgs[0].equals("exit")) {
			exit();
		} else if (nameAndArgs[0].equals("cd")) {
			changeDirectory(nameAndArgs[1]);
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
	// Returns output filename and removes output information from last command.
	// Example: 
	// userCommands = ['cat foo.txt', 'lc > bar.txt']
	// 
	private String getFile (String[] userCommands) {
		String[] commandAndFile = userCommands[userCommands.length-1].split(" *> *");
		userCommands[userCommands.length-1] = commandAndFile[0];
		return commandAndFile[1];
	}

	public static void main (String[] args) {
		String userCommands;
		CommandLine myCmd = new CommandLine();
		while(!myCmd.exitRepl) {
			System.out.print("> ");
			userCommands = myCmd.parseLine();
			LinkedBlockingQueue<String[]> output = myCmd.startAllThreads(userCommands.split(" *\\| *"));
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