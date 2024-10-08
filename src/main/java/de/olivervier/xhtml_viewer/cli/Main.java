package de.olivervier.xhtml_viewer.cli;

public class Main {
	public static void main(String[] args) {

		//Just for testing
		args = new String[]{"src", "true"};

		String[] filepaths = new String[args.length - 1];
		for(int i = 0; i < args.length-1; i++) {
			filepaths[i] = args[i];
		}
		boolean recursive = args[args.length-1].equals("-r");

		new CLI().run(filepaths, recursive);
	}
}