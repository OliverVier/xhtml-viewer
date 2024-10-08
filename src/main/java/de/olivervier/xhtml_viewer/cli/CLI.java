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

	public void run(String[] filepaths, boolean recursive) {

		// TODO: recursive file lookup
		scanner = new Scanner(System.in);
		pages = loadPages(filepaths);

		printHelp();

		while (true) {

			print();

			String userInput = scanner.nextLine();

			Command cmd = null;
			try {
				cmd = new Command(userInput);
			} catch (IllegalArgumentException e) {
				System.out.println(e.getMessage());
				continue;
			}

			if (context == null) {

				if (cmd.getAction() != null) {
					String splitContext[];
					if ((splitContext = cmd.getAction().split(" ")).length == 2) {
						if (splitContext[0].equals("set")) {
							Page page = searchForPage(splitContext[1]);
							if(page == null) {
								System.out.println("Page not found!");
							} else {
								context = page;
							}
						} else {
							System.out.println("Unexpected input!");
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
									System.out.println("Parameter " + param + "used in wrong context");
									break;
							}
						}
					}
				}

			} else { // context is set

				String splitContext[];
				if (cmd.getAction() != null) {
					if ((splitContext = cmd.getAction().split(" ")).length == 2) {
						if (splitContext[0].equals("set")) {
							Page page = searchForPage(splitContext[1]);
							if(page == null) {
								System.out.println("Page not found!");
							} else {
								context = page;
							}
						} else {
							System.out.println("Unexpected input!");
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
		}
	}

	private List<Page> loadPages(String... dirPaths) {
		List<File> files = new PageReader().filterPages(dirPaths);
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

	public void print() {
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