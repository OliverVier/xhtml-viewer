package de.olivervier.xhtml_viewer;

import de.olivervier.xhtml_viewer.cli.CLI;

public class Main {

	public static void main(String[] args) {
		new Main().run(args);
	}

	public void run(String[] args) {
		if(args.length != 2) {
			System.err.println("viewer [TYPE] [DIRECTORY_PATH]");
			return;
		}
		new CLI().run(args[0], args[1]);
	}
}