package de.olivervier.xhtml_viewer;

import java.io.File;
import java.util.List;

import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.model.Param;
import de.olivervier.xhtml_viewer.reader.PageReader;
import de.olivervier.xhtml_viewer.reader.XHTMLReader;

public class Main {
	public static void main(String [] args) {
		List<File> files = new PageReader().filterPages("path_to_xhtml_pages");
		List<Page> pages = new XHTMLReader().readPages(files);		

		for(Page page : pages) {
			
			System.out.println("> " + page.getName());
			if(!page.getParameters().isEmpty()) {				
				System.out.println("  Containing following parameters");
				for(Param param : page.getParameters()) {
					System.out.println("    '%s' as '%s'".formatted(param.getName(), param.getValue()));
				}
			}
			
			if(!page.getRelations().isEmpty()) {				
				System.out.println("  Containg following relations");
				for(Page relation : page.getRelations()) {
					System.out.println("    Name: " + relation.getName());
					if(!relation.getParameters().isEmpty()) {						
						System.out.println("      Containing following parameters");
						for(Param param : relation.getParameters()) {
							System.out.println("        '%s' as '%s'".formatted(param.getName(), param.getValue()));
						}
					}
				}
			}
			System.out.println();
		}
	}
}
