package de.olivervier.xhtml_viewer.pages;

import java.nio.file.Path;
import java.util.List;

import de.olivervier.xhtml_viewer.model.Page;

public interface PageReader {
	public void init(Path path);
	public List<Page> getPages();
}