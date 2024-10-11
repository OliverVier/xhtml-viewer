package de.olivervier.xhtml_viewer.cli;

import java.util.ArrayList;
import java.util.List;

public enum Command{
	SET("set");

	private String command;

	 Command(String command) {
		this.command = command;
	}

	public String asString() {
		return command;
	}

	public static Command stringAsCommand(String commandStr) {
		for(Command command : Command.values()) {
			if(command.asString().equals(commandStr)) {
				return command;
			}
		}
		return null; 
	} 

	public static List<String> valuesAsString() {
		List<String> commands = new ArrayList<>();
		for(Command command : Command.values()) {
			commands.add(command.asString());
		}
		return commands;
	}
}