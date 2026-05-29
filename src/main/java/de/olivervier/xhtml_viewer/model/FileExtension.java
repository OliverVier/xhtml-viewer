package de.olivervier.xhtml_viewer.model;

public enum FileExtension {
	XHTML("xhtml");

    private String extension;
    private FileExtension(String extension) {
        this.extension = extension;
    }
    public String getExtension() {
        return extension;
    }
}
