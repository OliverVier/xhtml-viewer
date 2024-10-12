package de.olivervier.xhtml_viewer.cli;

import java.util.List;

import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.model.Param;

public class PagePrinter {

	List<Page> pages;
	Page context;

	boolean showParameters;
	boolean showReferences;
	boolean showRelations;

	public PagePrinter(List<Page> pages, Page context) {
		this.pages = pages;
		this.context = context;
	}

	public void print() {

		if (showParameters) {
			System.out.println("Own parameters: ");
			if (context.getParameters() == null || context.getParameters().isEmpty()) {
				System.out.println(">None");
			} else {
				for (Param param : context.getParameters()) {
					System.out.println(">" + param);
				}
			}
		}

		if (showRelations) {
			System.out.println("Relations:");
			if (context.getRelations() == null || context.getRelations().isEmpty()) {
				System.out.println(">None");
			} else {
				for (Page relation : context.getRelations()) {
					System.out.println(">Relation: " + relation.getName());
					if(showParameters) {
						if (relation.getParameters() == null || relation.getParameters().isEmpty()) {
							System.out.println(">None");
						} else {
							for (Param param : relation.getParameters()) {
								System.out.println(">>" + param);
							}
						}
					}
				}
			}
		}
	}

	public PagePrinter showParameters() {
		showParameters = true;
		return this;
	}

	public PagePrinter showReferences() {
		showReferences = true;
		return this;
	}

	public PagePrinter showRelations() {
		showRelations = true;
		return this;
	}
}
