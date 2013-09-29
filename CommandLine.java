class CommandLine {

	static class Parser {
		private String[] commandList;
		private final int COMMAND_LIST_SIZE;

		private Parser (String[] commands) {
			COMMAND_LIST_SIZE = commands.length;
			commandList = new String[COMMAND_LIST_SIZE];
			for(int i = 0; i < COMMAND_LIST_SIZE; i++) {
				commandList[i] = commands[i];
			}

			
		}

		private void parse (String commandString) {
			String[] commands = commandString.split(" *\\| *");
			for(int i =0; i<commands.length; i++) System.out.println(commands[i]);
		}

	}

	public static void main (String[] args) {
		String[] commands = {"hello", "there", "world"};
		CommandLine.Parser myParser = new CommandLine.Parser(commands);
		myParser.parse("hello|there | world > blah.pdf");
	}
}