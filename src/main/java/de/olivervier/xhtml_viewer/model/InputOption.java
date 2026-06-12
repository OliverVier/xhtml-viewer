package de.olivervier.xhtml_viewer.model;

public enum InputOption {
	XHTML("XHTML");

    private String name;
    private InputOption(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}
