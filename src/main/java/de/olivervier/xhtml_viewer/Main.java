package de.olivervier.xhtml_viewer;

import de.olivervier.xhtml_viewer.cli.CLI;

public class Main {

	public static void main(String[] args) {
		new Main().run(args);
	}

	public void run(String[] args) {
		if(args.length == 0) {
			System.err.println("Program needs a directory path to operate");
			return;
		}
		new CLI().run(args[0]);
	}
}