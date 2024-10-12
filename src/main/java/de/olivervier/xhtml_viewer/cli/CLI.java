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

	public CLI() {
	}

	public void run(String[] filepaths, boolean recursively) {

		scanner = new Scanner(System.in);
		pages = loadPages(filepaths, recursively);

		printHelp();

		while (true) {

			printContext();

			String userInput = scanner.nextLine();

			CommandImpl cmd = null;
			try {
				cmd = new CommandImpl(userInput);
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				continue;
			}

			if (context == null) {
				executeInNoContext(cmd);
			} else {
				executeInContext(cmd);
			}
		}
	}

	public void executeInNoContext(CommandImpl cmd) {

		if (cmd.getAction() != null) {
			switch (cmd.getAction()) {
				case SET:
					Page page = searchForPage(cmd.getActionValue());
					if (page == null) {
						System.out.println("Page not found!");
					} else {
						context = page;
					}
					break;
				default:
					break;
			}
		}

		if (cmd.getParams() != null) {
			for (CommandParam param : cmd.getParams()) {
				switch (param) {
					case HELP:
						printHelp();
						break;
					case LISTALL:
						printAllPageNames();
						break;
					default:
						System.out.println("Parameter " + param + " not available in context");
						break;
				}
			}
		}
	}

	public void executeInContext(CommandImpl cmd) {

		if (cmd.getAction() != null) {
			switch (cmd.getAction()) {
				case SET:
					Page page = searchForPage(cmd.getActionValue());
					if (page == null) {
						System.out.println("Page not found!");
					} else {
						context = page;
					}
					break;
				default:
					break;
			}
		}

		if (cmd.getParams() != null) {
			PagePrinter printer = new PagePrinter(pages, context);
			for (CommandParam commandParam : cmd.getParams()) {
				switch (commandParam) {
					case HELP:
						printHelp();
						break;
					case LISTALL:
						printAllPageNames();
						break;
					case PARAM:
						printer.showParameters();
						break;
					case REC:
						break;
					case REF:
						printer.showReferences();
						break;
					case REL:
						printer.showRelations();
						break;
					default:
						System.out.println("Not implemented");
						break;
				}
			}
			System.out.println("");
			printer.print();
		}
	}

	private List<Page> loadPages(String[] dirPaths, boolean recursively) {
		List<File> files = new PageReader().filterPages(dirPaths, recursively);
		return new XHTMLReader().readPages(files);
	}

	private Page searchForPage(String pageName) {
		for (Page page : pages) {
			if (page.getName().equals(pageName)) {
				return page;
			}
		}
		return null;
	}

	private void printAllPageNames() {
		for (Page page : pages) {
			System.out.println(page.getName());
		}
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

	public void printContext() {
		String contextName = context == null ? "" : context.getName();
		System.out.print("#" + contextName + "   ");
	}

	public void printHelp() {
		System.out.println(
				"\nxhtml viewer started. Possible options:" +
						"\n-----------------" +
						"\nset PAGE_NAME" +
						"\nonly usable when page is set:" +
						"\n-p | get every parameter" +
						"\n-r | get every relation" +
						"\n-f | get every reference" +
						"\n-l | include filename and line" +
						"\n-----------------" +
						"\n-h | get help" +
						"\n-L | list all xhtml pages" +
						"\n");
	}
}