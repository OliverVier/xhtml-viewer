package de.olivervier.xhtml_viewer.cli;

import java.util.HashSet;
import java.util.Set;

//vhtmlv [filepath] -r  //recursive
//vhtmlv set page
//vhtmlv -p 			//get every parameter
//vhtmlv -r 			//get every relation
//vhtmlv -f 			//get every reference for the page
//vhtmlv -f -l 			//get every reference for the page, including filename and line
//vhtmlv -h     		//help

public class Command {

	private String action;
	private Set<CommandParam> params;

	public Command(String userInput) {
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
			if(isParameter(userInput)) {
				action = null;
				params = extractCommandParams(userInput);
			} else {
				throw new IllegalArgumentException("Unexpected keyword without specifier");
			}
		}

		if (splitUserInput.length == 2) {

			if (isParameter(splitUserInput[0])) {
				throw new IllegalArgumentException(
						"Parameters are not allowed at first position, when another keyword is included");
			}

			action = splitUserInput[0];

			if(isParameter(splitUserInput[1])) {
				this.params = extractCommandParams(splitUserInput[1]);
			} else {
				action = action.trim();
				action += " "+splitUserInput[1];
			}
		}

		if(splitUserInput.length > 2) {
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

		if(useString.charAt(0)=='-') {
			useString = useString.substring(1,useString.length());
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

	public String getAction() {
		return action;
	}

	public Set<CommandParam> getParams() {
		return params;
	}

	public static void main(String[] args) {
		Command command = new Command("test-test");
		System.out.println(command.action);

		for(CommandParam param : command.params) {
			System.out.println(param);
		}
	}
}