package de.olivervier.xhtml_viewer.cli;

import java.nio.file.InvalidPathException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import de.olivervier.xhtml_viewer.export.DiagramExport;
import de.olivervier.xhtml_viewer.model.InputOption;
import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.pages.JavaPageReader;
import de.olivervier.xhtml_viewer.pages.PageReader;
import de.olivervier.xhtml_viewer.pages.XHTMLPageReader;

public class CLI {

	public CLI() {
	}

	public void run(String type, String filePath) {
		
		if(Objects.isNull(type)) {
			UserInteraction.sendMessage("Type must not be empty");
			return;
		}
		if(Objects.isNull(filePath)) {
			UserInteraction.sendMessage("Filepath must not be empty");
			return;
		}
		
		Path pathToFile;
		try {
			pathToFile = Path.of(filePath);
		} catch (InvalidPathException e) {
			UserInteraction.sendMessage("Path is invalid");
			return;
		} catch (Exception e) {
			UserInteraction.sendMessage("Unknown exception");
			return;
		}
		
		redirectToHandler(type, pathToFile);
	}

	
	public void redirectToHandler(String type, Path directoryPath) {
		
		List<Page> pages = new ArrayList<Page>();
		InputOption option = InputOption.valueOf(type.toUpperCase());
		
		try {			
			switch (option) {
			case XHTML: {
				PageReader reader = new XHTMLPageReader();
				reader.init(directoryPath);
				pages = reader.getPages();	
				break;
			}
			case JAVA: {
				PageReader reader = new JavaPageReader();
				reader.init(directoryPath);
				pages = reader.getPages();	
				break;
			}
			default:
				throw new IllegalArgumentException("Option: " + option.getName() + " is not supported");
			}
			
			if(pages == null || pages.isEmpty()) {
				UserInteraction.sendMessage("No pages found");
				return;
			} 
			
			new DiagramExport().handleExport(pages, null);
			
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public void printHelp() {
		UserInteraction.sendMessage("viewer [FILE_TYPE] [FILE_PATH]");
	}
}