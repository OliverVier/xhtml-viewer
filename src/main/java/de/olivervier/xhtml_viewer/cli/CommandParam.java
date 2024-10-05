package de.olivervier.xhtml_viewer.cli;

public enum CommandParam {
	PARAM('p'), REC('R'), REL('r'), REF('f'), HELP('h'), QUIT('q');

	private char character;

	CommandParam(char character) {
		this.character = character;
	}

	public char asChar() {
		return character;
	}
}
