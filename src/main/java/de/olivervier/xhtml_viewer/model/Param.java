package de.olivervier.xhtml_viewer.model;

public class Param {
	private String name;
	private String value;
	public Param(String name, String value) {
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString() {
		return "%s - %s".formatted(name, value);
	}
}