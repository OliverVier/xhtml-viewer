package de.olivervier.xhtml_viewer.model;

import java.nio.file.Path;
import java.util.List;

public class Page {
	private String name;
	private Path filePath;
	private List<Param> parameters;
	private List<Relation> relations;
	public Page(String name, Path filePath, List<Param> parameters, List<Relation> relations) {
		this.name = name;
		this.filePath = filePath;
		this.parameters = parameters;
		this.relations = relations;
	}
	public String getName() {
		return name;
	}
	public Path getFilePath() {
		return filePath;
	}
	public List<Param> getParameters() {
		return parameters;
	}
	public List<Relation> getRelations() {
		return relations;
	}
}