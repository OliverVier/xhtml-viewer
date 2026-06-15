package de.olivervier.xhtml_viewer.pages;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.olivervier.xhtml_viewer.model.InputOption;
import de.olivervier.xhtml_viewer.model.Page;

public class JavaPageReader implements PageReader {

	List<Page> pages = new ArrayList<Page>();
	
	@Override
	public void init(Path path) {
		var finder = new FileFinder(path, InputOption.JAVA);
		List<File> files = finder.read();
		this.pages = readPages(path, files);
	}
	
	public read
	
	
	private List<Page> readPages(Path basePath, List<File> files) {
		
		List<Page> pages = new ArrayList<Page>();
		//Map<String, Page> pageMap = prefillPages(files);
		
		// Initilize classes
		URL[] arrayOfUrls = new URL[files.size()]; 
		for(int i = 0; i < arrayOfUrls.length; i++) {
			try {
				arrayOfUrls[i] = files.get(i).toURI().toURL();
			} catch (MalformedURLException e) {
				System.err.println(files.get(i).toPath().toString() + " could not be converted to URL");
			}
		}
		
		List<Class<?>> classes = new ArrayList<Class<?>>(); 
		
			
			for(File file : files) {
				try (URLClassLoader classLoader = new URLClassLoader(arrayOfUrls)) {
				
				// Get class name without file extension
				String classPath = basePath.relativize(file.toPath()).toString();
				int endIndex = classPath.indexOf('.');
				classPath = classPath.substring(0, endIndex)
									 .replace("\\", ".");
				
				Class<?> clazz = classLoader.loadClass(classPath);
				classes.add(clazz);	
				}			
			catch (ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		} 
		return pages;
	}
	
	private Map<String, Page> prefillPages(List<File> files) {
		Map<String, Page> pagesMap = new HashMap<>();
		for(File currentFile : files) {
			Path absPath = currentFile.toPath();
			pagesMap.put(absPath.toString(),new Page(absPath, new ArrayList<>(), new ArrayList<>()));
		}
		return pagesMap;
	}

	@Override
	public List<Page> getPages() {
		return pages;
	}

}
