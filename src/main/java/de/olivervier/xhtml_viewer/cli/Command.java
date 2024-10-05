package de.olivervier.xhtml_viewer.cli;

import java.util.ArrayList;
import java.util.List;

import de.olivervier.xhtml_viewer.model.Page;

//vhtmlv [filepath] -r=RECURSIVE
//vhtmlv -p 			//get every parameter
//vhtmlv -r 			//get every relation
//vhtmlv -f 			//get every reference for the page
//vhtmlv -f -l 			//get every reference for the page, including filename and line
//vhtmlv -h     		//help

public class Command {

	private Page context;
	private String action;
	private CommandParam[] params;

	public Command(String userInput, Page context) {
		try {
			createCommand(userInput, context);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	private void createCommand(String userInput, Page context) throws IllegalArgumentException {

		if (userInput == null || userInput.isBlank()) {
			throw new IllegalArgumentException("No user input for command!");
		}

		String[] splitUserInput = userInput.split(" ");
		if (splitUserInput.length == 0) {
			throw new IllegalArgumentException("No command");
		}

		// set -p -p -p
		// " "
		// set name

		// action word or parameter/s
		if (splitUserInput.length == 1) {

			String action = splitUserInput[0];

			if (isParameter(action)) {

				if (action.length() - 1 > CommandParam.values().length) {
					throw new IllegalArgumentException("Too many parameters");
				}

				if (action.charAt(0) == '-') {

					action = action.substring(1, action.length());
					List<CommandParam> params = new ArrayList<CommandParam>();
					for (char c : action.toCharArray()) {

						CommandParam param = findParam(c);

						if (param == null) {
							System.err.println("Parameter " + c + "does not exist");
							continue;
						}
						
						params.add(param);
					}

				}

				String keyword = splitUserInput[0];

			} else {

			}

		}

	}

	/**
	 * String consisting of a dash and letters (example: -f or -rf)
	 * 
	 * @param parameterString
	 * @return list of parameters
	 */
	private List<CommandParam> extractCommandParams(String parameterString) {
		return null;
	}

	/**
	 * Is the given string a parameter?
	 * 
	 * @return
	 */
	private boolean isParameter(String input) {

		if (input == null) {
			return false;
		}

		input = input.strip();
		if (input.charAt(0) == '-') {
			return true;
		}

		return false;
	}


	private CommandParam findParam(char param) {
		for (CommandParam possibleParam : CommandParam.values()) {
			if (possibleParam.asChar() == param) {
				return possibleParam;
			}
		}
		return null;
	}
}