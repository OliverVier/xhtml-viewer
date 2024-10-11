package de.olivervier.xhtml_viewer.cli;

import java.util.HashSet;
import java.util.Set;

public class CommandImpl {

	private Command action;
	private String actionValue;
	private Set<CommandParam> params;

	public CommandImpl(String userInput) {
		createCommand(userInput);
	}

	private void createCommand(String userInput) throws IllegalArgumentException {

		if (userInput == null || userInput.isBlank()) {
			throw new IllegalArgumentException("No user input for command!");
		}

		String[] splitUserInput = userInput.split(" ");
		if (splitUserInput.length == 0) {
			throw new IllegalArgumentException("No command");
		}

		if (splitUserInput.length == 1) {
			userInput = splitUserInput[0];
			if (isParameter(userInput)) {
				action = null;
				params = extractCommandParams(userInput);
			} else {
				throw new IllegalArgumentException("Unexpected keyword without specifier");
			}
		}

		if (splitUserInput.length == 2) {

			String commandAction = splitUserInput[0];
			String commandActionValue = splitUserInput[1];

			if (isParameter(commandAction)) {
				throw new IllegalArgumentException(
						"Parameters are not allowed at first position, when another keyword is included");
			}

			Command command;
			if ((command = Command.stringAsCommand(commandAction)) == null) {
				throw new IllegalArgumentException("Command " + commandAction + " does not exist");
			}
			action = command;

			if (isParameter(commandActionValue)) {
				this.params = extractCommandParams(commandActionValue);
			} else {
				actionValue = commandActionValue.trim();
			}
		}

		if (splitUserInput.length > 2) {
			throw new IllegalArgumentException("Wrong command format. Look at 'command -h' for help");
		}
	}

	/**
	 * String consisting of a dash and letters (example: -f or -rf)
	 * 
	 * @param parameterString
	 * @return list of parameters
	 */
	private Set<CommandParam> extractCommandParams(String useString) throws IllegalArgumentException {

		Set<CommandParam> commandParameters = new HashSet<>();

		useString = useString.trim();

		if (useString.charAt(0) == '-') {
			useString = useString.substring(1, useString.length());
		}

		for (char parameter : useString.toCharArray()) {
			CommandParam param = CommandParam.charAsCommandParam(parameter);
			if (param == null) {
				throw new IllegalArgumentException("Invalid parameter: " + parameter);
			}
			commandParameters.add(param);
		}

		return commandParameters;
	}

	private boolean isParameter(String useString) {
		return useString.trim().charAt(0) == '-';
	}

	public Command getAction() {
		return action;
	}

	public String getActionValue() {
		return actionValue;
	}

	public Set<CommandParam> getParams() {
		return params;
	}
}