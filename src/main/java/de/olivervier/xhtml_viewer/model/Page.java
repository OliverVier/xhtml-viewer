package de.olivervier.xhtml_viewer.model;

import java.util.List;

public class Page {
	private String name;
	private List<Param> parameters;
	private List<Page> relations;
	public Page(String name, List<Param> parameters, List<Page> relations) {
		this.name = name;
		this.parameters = parameters;
		this.relations = relations;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Param> getParameters() {
		return parameters;
	}
	public void setParameters(List<Param> parameters) {
		this.parameters = parameters;
	}
	public List<Page> getRelations() {
		return relations;
	}
	public void setRelations(List<Page> relations) {
		this.relations = relations;
	}
}