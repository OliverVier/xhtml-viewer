package de.olivervier.xhtml_viewer.model;

public class Relation {
	private Page relation;
	private RelationType type;
	
	public Relation(Page relation, RelationType type) {
		this.relation = relation;
		this.type = type;
	}
	public enum RelationType {
		COMPOSITION,
		INCLUDE
	}
	public Page getRelation() {
		return relation;
	}
	public RelationType getType() {
		return type;
	}
	@Override
	public String toString() {
		return "%s - %s".formatted(relation.getName(), type.name());
	}
}