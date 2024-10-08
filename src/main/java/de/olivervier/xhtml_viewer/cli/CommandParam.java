package de.olivervier.xhtml_viewer.cli;

import java.util.ArrayList;
import java.util.List;

public enum CommandParam {
	PARAM('p'), 
	REC('R'), 
	REL('r'), 
	REF('f'), 
	HELP('h'),
	LISTALL('L');

	private char character;

	CommandParam(char character) {
		this.character = character;
	}

	public char asChar() {
		return character;
	}

	public static CommandParam charAsCommandParam(char character) {
		for(CommandParam param : CommandParam.values()) {
			if(param.asChar() == character) {
				return param;
			}
		}
		return null; 
	} 

	public static List<Character> valuesAsChar() {
		List<Character> characters = new ArrayList<>();
		for(CommandParam param : CommandParam.values()) {
			characters.add(param.asChar());
		}
		return characters;
	}
}
