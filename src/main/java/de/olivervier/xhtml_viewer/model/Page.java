package de.olivervier.xhtml_viewer.model;

import java.nio.file.Path;
import java.util.List;

public class Page {
	private Path filePath;
	private List<Param> parameters;
	private List<Relation> relations;
	public Page(Path filePath, List<Param> parameters, List<Relation> relations) {
		this.filePath = filePath;
		this.parameters = parameters;
		this.relations = relations;
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