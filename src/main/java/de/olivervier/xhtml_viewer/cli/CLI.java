package de.olivervier.xhtml_viewer.cli;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.model.Param;
import de.olivervier.xhtml_viewer.reader.PageReader;
import de.olivervier.xhtml_viewer.reader.XHTMLReader;

public class CLI {

	private Scanner scanner;

	// Current page to look at
	private Page context;
	private List<Page> pages;

	public CLI() {
		scanner = new Scanner(System.in);
	}

	public void run() {
		while (true) {
			String userInput = scanner.next();
			String[] splitUserInput = userInput.split(" ");

			// if(splitUserInput[0].eq)

			// "/home/test-oli/eclipse-workspace/xhtml-viewer-test-webapp/src/main/webapp"
		}
	}

	public void print() {

		for (Page page : pages) {

			System.out.println("> " + page.getName());
			if (!page.getParameters().isEmpty()) {
				System.out.println("  Containing following parameters");
				for (Param param : page.getParameters()) {
					System.out.println("    '%s' as '%s'".formatted(param.getName(), param.getValue()));
				}
			}

			if (!page.getRelations().isEmpty()) {
				System.out.println("  Containg following relations");
				for (Page relation : page.getRelations()) {
					System.out.println("    Name: " + relation.getName());
					if (!relation.getParameters().isEmpty()) {
						System.out.println("      Containing following parameters");
						for (Param param : relation.getParameters()) {
							System.out.println("        '%s' as '%s'".formatted(param.getName(), param.getValue()));
						}
					}
				}
			}
			System.out.println();
		}

	}

	private List<Page> search(String... dirPaths) {
		List<File> files = new PageReader().filterPages(dirPaths);
		return new XHTMLReader().readPages(files);
	}
}