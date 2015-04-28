import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
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
		if (commandName.equals("cat")) {
			System.out.println("Command - cat\n" +
				"Usage - cat takes at least one filename and outputs its contents, line by line.\n" +
				"Example - cat filename1 [filename2, filename3...]");
		} else if (commandName.equals("lc")) {
			System.out.println("Command - lc\n" +
				"Usage - lc takes no arguments. It counts the numberof lines it receives as input.\n" +
				"It cannot be the first command in a sequence of commands\n" +
				"Example - lc");
		} else if (commandName.equals("grep")) {
			System.out.println("Command - grep\n" +
				"Usage - grep takes a string and returns each line containing that string.\n" +
				"Example - grep hello world");
		} else if (commandName.equals("!n")) {
			System.out.println("Command - !n\n" +
				"Usage - !n takes no arguments. It executes the nth command executed during a session.\n" +
				"Example - !2");
		} else if (commandName.equals("history")) {
			System.out.println("Command - history\n" +
				"Usage - history takes no arguments, and must appear first in a sequence of piped commands.\n" + 
				"It lists the commands executed during the current session.\n" +
				"Example - history");
		} else if (commandName.equals("pwd")) {
			System.out.println("Command - pwd\n" +
				"Usage - pwd takes no arguments, and must appear first in a sequence of piped commands.\n" + 
				"It outputs the current working directory.\n" +
				"Example - pwd");
		} else if (commandName.equals("ls")) {
			System.out.println("Command - ls\n" +
				"Usage - ls takes no arguments, and must appear first in a sequence of piped commands.\n" + 
				"It lists the contents of the current working directory.\n" +
				"Example - ls");
		} else if (commandName.equals("cd")) {
			System.out.println("Command - cd\n" +
				"Usage - cd takes one argument, and must appear alone. It cannot participate in a sequence of piped commands.\n" + 
				"cd changes the current working directory to the path given to it. '.' may be used to refer to the current directory.\n" +
				"'..' may be used to refer to the parent directory. cd only accepts relative paths." +
				"Example -\n" + "> cd exampledir\n" + "> pwd\n" + "home/pa1/exampledir");
		}
	}

	private void handleException(Exception e, String commandName, String[] commandArgs) {
		// do something
	}

	/** Changes currentDirectory to the relative path given to it.
		*Note* On Windows, file.isDirectory() works perfectly. However,
		on Ubuntu it always returns false. I can not figure out why this happens.
		@param String path  A relative path to a directory.
	*/
	private void changeDirectory (String path) {
		if (path.equals("..")) {
			currentDirectory = currentDirectory.getParent();
		} else if (path.equals(".")) {
			return;
		} else {
			if ((new File(path)).isDirectory()) {
				currentDirectory = currentDirectory.resolve(path);
			} else {
				System.out.printf("%s is not a directory.\n", path);
			}
		}
	}
	/** Splits user input about pipe characters.
		@return String[] The split string.
	*/
	private String[] parseLine () {
		return myScanner.nextLine().split(" *\\| *");
	}

	/** Constructs and starts each individual command. Also handles improper use of commands.
		@param String[]                      nameAndArgs A two element array. The first element is the command, 
		                                                 the second is the command's arguments.
		@param LinkedBlockingQueue<String[]> input       The input queue for the command to be constructed.
		@param LinkedBlockingQueue<String[]> output      The output queue for the command to be constructed.
		@return                                          Returns a reference to the thread that was constructed.
	*/
	private Thread startThread(String[] nameAndArgs, LinkedBlockingQueue<String[]> input, 
		LinkedBlockingQueue<String[]> output, int currentIndex) {
		Thread commandThread = null;
		if (nameAndArgs[0].equals("cat")) {
			if (currentIndex != 0 || nameAndArgs.length < 2) {
				commandUsage("cat");
			} else {
				try {
					commandThread = new Thread(new Cat(output, currentDirectory, nameAndArgs[1].split(" ")));
				} catch (FileNotFoundException e) {
					System.out.println(e.getMessage());
					// do nothing, will return null at end.
				}
			}
		} else if (nameAndArgs[0].equals("lc")) {
			if (nameAndArgs.length > 1 || currentIndex == 0) {
				commandUsage("lc");
			} else {
				commandThread = new Thread(new LineCount(input, output));
			}
		} else if (nameAndArgs[0].equals("grep")) {
			if (nameAndArgs.length == 1) {
				commandUsage("grep");
			} else {
				commandThread = new Thread(new Grep(input, output, nameAndArgs[1]));
			}
		} else if (nameAndArgs[0].contains("!")) {
			int commandIndex = Integer.parseInt(nameAndArgs[0].substring(1));
			LinkedBlockingQueue<String[]> out = startAllThreads(commandHistory.get(commandIndex - 1).split(" *\\| *"));
			commandThread = new Thread(new PrevCommand(out, output));
		} else if (nameAndArgs[0].equals("history")) {
			if (currentIndex != 0) {
				commandUsage("history");
			} else {
				commandThread = new Thread(new History(output, commandHistory));
			}
		} else if (nameAndArgs[0].equals("pwd")) {
			if (currentIndex != 0) {
				commandUsage("pwd");
			}else {
				commandThread = new Thread(new Pwd(output, currentDirectory.toString()));
			}
		} else if (nameAndArgs[0].equals("ls")) {
			if (currentIndex != 0) {
				commandUsage("ls");
			} else {
				commandThread = new Thread(new Ls(output, currentDirectory.toString()));
			}
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

	/** Creates the input and output queues for each command, and initiates their construction.
		@param String[] userCommands The user's input, split along pipe characters.
		@return                      The final output queue, to be used to construct the output handler.
	*/
	private LinkedBlockingQueue<String[]> startAllThreads(String[] userCommands) {
		LinkedBlockingQueue<String[]> in = new LinkedBlockingQueue<String[]>();
		LinkedBlockingQueue<String[]> out = new LinkedBlockingQueue<String[]>();
		for (int i = 0; i < userCommands.length; i++) {
			in = out;
			out = new LinkedBlockingQueue<String[]>();
			Thread commandThread = startThread(userCommands[i].split(" ", 2), in, out, i);
			if (commandThread != null) {
				commandList.add(commandThread);
			} else {
				return null;
			}
		}
		return out;
	}
	/** Returns output filename and removes output information from last command.
		Modifies userCommand in place.
		@param String[] userCommands The user's input, split along pipe characters.
		@return                      Returns the name of the file to write output to.
	*/
	private String getFile (String[] userCommands) {
		String[] commandAndFile = userCommands[userCommands.length-1].split(" *\\> *");
		userCommands[userCommands.length-1] = commandAndFile[0];
		if (commandAndFile.length > 1) return commandAndFile[1] + ".txt";
		return null;
	}

	public static void main (String[] args) {
		String commandString = "";
		CommandLine myCmd = new CommandLine();
		while(!myCmd.exitRepl) {
			System.out.print("> ");
			String[] userCommands = myCmd.parseLine();
			String outputFile = myCmd.getFile(userCommands);
			LinkedBlockingQueue<String[]> output = myCmd.startAllThreads(userCommands);
			if (output != null) {
				Thread myOutputHandler = null;
				if (outputFile != null) {
					try {
						// OutputHandler writes to System.out or a file
						myOutputHandler = new Thread(new OutputHandler(output, outputFile));
					} catch (FileNotFoundException e) {
						System.out.printf("Unable to create file: %s\n", outputFile);
					}
				} else {
					myOutputHandler = new Thread(new OutputHandler(output));
				}
				myOutputHandler.start();
				// Wait for threads to finish
				try {
					myOutputHandler.join();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					break;
				}
			}
			myCmd.commandHistory.add(commandString);
			myCmd.commandList.clear();
		}
	}
}