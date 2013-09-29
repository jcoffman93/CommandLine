import java.util.Scanner;

class CommandLine {

	static class Parser {
		private String[] commandList;
		private final int COMMAND_LIST_SIZE;
		private Scanner myScanner;

		private Parser (String[] commands) {
			COMMAND_LIST_SIZE = commands.length;
			commandList = new String[COMMAND_LIST_SIZE];
			for(int i = 0; i < COMMAND_LIST_SIZE; i++) {
				commandList[i] = commands[i];
			}
			myScanner = new Scanner(System.in);			
		}

		private void parseLine () {
			String[] commands = myScanner.nextLine().split(" *\\| *");
			for(int i =0; i<commands.length; i++) System.out.println(commands[i]);
		}

	}

	public static void main (String[] args) {
		String[] commands = {"hello", "there", "world"};
		CommandLine.Parser myParser = new CommandLine.Parser(commands);
		while(true) {
			System.out.print("> ");
			myParser.parseLine();
		}
	}
}