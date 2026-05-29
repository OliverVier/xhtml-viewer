package de.olivervier.xhtml_viewer;

import de.olivervier.xhtml_viewer.cli.CLI;

public class Main {
	public static void main(String[] args) {
		try {
			new CLI().run(args[0]);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}