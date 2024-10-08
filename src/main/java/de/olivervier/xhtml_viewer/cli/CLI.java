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
	private Page context;
	private List<Page> pages;

	public CLI() {}
	
	public void run(String[] filepaths, boolean recursive) {
		
		//TODO: recursive file lookup
		scanner = new Scanner(System.in);
		pages = search(filepaths);

		printHelp();

		while (true) {

			print();

			String userInput = scanner.nextLine();
			String[] splitUserInput = userInput.split(" ");

			// if(splitUserInput[0].eq)

			// "/home/test-oli/eclipse-workspace/xhtml-viewer-test-webapp/src/main/webapp"
		}
	}

	private List<Page> search(String... dirPaths) {
		List<File> files = new PageReader().filterPages(dirPaths);
		return new XHTMLReader().readPages(files);
	}
	
	public void printPage() {

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

	public void print() {
		String contextName = context == null ? "" : context.getName();
		System.out.print("#"+contextName+"   ");
	}

	public void printHelp() {
		System.out.println(
			"\nxhtml viewer started. Possible options:"+
			"\n-----------------"+
			"\nset PAGE_NAME"+
			"\n-p | get every parameter"+
			"\n-r | get every relation"+ 
			"\n-f | get every reference"+
			"\n-l | include filename and line"+
			"\n-h | get help"+
			"\n"
		);
	}
}