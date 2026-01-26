package de.olivervier.xhtml_viewer.model;

import java.util.List;

public class Page {
	private String name;
	private List<Param> parameters;
	private List<Relation> relations;
	public Page(String name, List<Param> parameters, List<Relation> relations) {
		this.name = name;
		this.parameters = parameters;
		this.relations = relations;
	}
	public String getName() {
		return name;
	}
	public List<Param> getParameters() {
		return parameters;
	}
	public List<Relation> getRelations() {
		return relations;
	}
}