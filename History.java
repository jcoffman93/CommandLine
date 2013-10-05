import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.Arrays;

public class History extends StartFilter {
	private ArrayList<String[]> commandHistory;
	private int currentCommand;

	public History (LinkedBlockingQueue<String[]> out, ArrayList<String[]> commandHistory) {
		super(out);
		this.commandHistory = commandHistory;
		currentCommand = 0;
	}

	public String[] transform () {
		String commandString = makeCommandString();
		if (currentCommand >= commandHistory.size() - 1) {
			super.done = true;
			String[] result = { commandString, "END" };
			currentCommand++;
			return result;
		} else {
			String[] result = { commandString, "" };
			currentCommand++;
			return result;
		}
		
	}

	private String makeCommandString () {
		if (commandHistory.size() ==0) return "";
		String commandString = Integer.toString(currentCommand + 1);
		for (String command : commandHistory.get(currentCommand)) {
			commandString += String.format(" %s", command);
		}
		return commandString;
	}
}