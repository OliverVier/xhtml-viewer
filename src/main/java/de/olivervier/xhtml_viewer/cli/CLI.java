package de.olivervier.xhtml_viewer.cli;

import java.io.File;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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
		if (cmd.getAction() == Command.SET) {
			if (cmd.getActionValue() != null) {
				Page page = searchForPage(cmd.getActionValue());
				if (page == null) {
					System.out.println("Page not found!");
				} else {
					context = page;
				}
			} else {
				System.out.println("Unexpected input!");
			}
		} else {
			if (cmd.getParams() != null) {
				for (CommandParam param : cmd.getParams()) {
					switch (param) {
						case CommandParam.HELP:
							printHelp();
							break;
						case CommandParam.LISTALL:
							printAllPageNames();
							break;
						default:
							System.out.println("Parameter " + param + " not available in context");
							break;
					}
				}
			}
		}
	}

	public void executeInContext(CommandImpl cmd) {
		if (cmd.getAction() == Command.SET) {
			if (cmd.getActionValue() != null) {
				Page page = searchForPage(cmd.getActionValue());
				if (page == null) {
					System.out.println("Page not found!");
				} else {
					context = page;
				}
			} else {
				System.out.println("Unexpected input!");
			}
		}

		if (cmd.getAction() == null) {
			for (CommandParam commandParam : cmd.getParams()) {
				switch (commandParam) {
					case CommandParam.HELP:
						printHelp();
						break;
					case CommandParam.LISTALL:
						printAllPageNames();
						break;
					default:
						System.out.println("Not implemented");
						break;
				}
			}
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
						"\n-p | get every parameter" +
						"\n-r | get every relation" +
						"\n-f | get every reference" +
						"\n-l | include filename and line" +
						"\n-h | get help" +
						"\n-L | list all xhtml pages" +
						"\n");
	}
}