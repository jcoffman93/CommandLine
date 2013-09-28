class CommandLine {

	private class Parser {
		Scanner scanner;
		String[] commandList;
		private final int COMMAND_LIST_SIZE;

		private Parser (String[] commands) {
			scanner = new Scanner(System.in);
			COMMAND_LIST_SIZE = commands.length;
			commandList = new String[COMMAND_LIST_SIZE];
			for(int i = 0; i < COMMAND_LIST_SIZE; i++) {
				commandList[i] = commands[i];
			}

			
		}

		private void parse (String commandString) {
			String[] commands = commandString.split("|");
			System.out.println(commands.toString());
		}

	}

	public static void main (String[] args) {
		String[] commands = {"hello", "there", "world"};
		Parser myParser = new Parser(commands);
		myParser.parse("hello|there | world > blah.pdf");
	}
}