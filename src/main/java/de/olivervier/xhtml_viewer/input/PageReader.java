package de.olivervier.xhtml_viewer.input;

import java.util.List;

import de.olivervier.xhtml_viewer.model.Page;

public interface PageReader {
	public void init(String basepath);
	public List<Page> getPages();
}