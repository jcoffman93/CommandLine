import java.util.concurrent.*;
import java.util.ArrayList;
import java.util.Arrays;

public class History extends StartFilter {
	private ArrayList<String> commandHistory;
	private int currentCommand;

	public History (LinkedBlockingQueue<String[]> out, ArrayList<String> commandHistory) {
		super(out);
		this.commandHistory = commandHistory;
		currentCommand = 0;
	}

	public String[] transform () {
		if (currentCommand >= commandHistory.size() - 1) {
			super.done = true;
			String[] result = { commandHistory.get(currentCommand), "END" };
			currentCommand++;
			return result;
		} else {
			String[] result = { commandHistory.get(currentCommand), "" };
			currentCommand++;
			return result;
		}
		
	}
}