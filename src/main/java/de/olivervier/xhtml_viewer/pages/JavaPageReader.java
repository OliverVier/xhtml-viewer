package de.olivervier.xhtml_viewer.pages;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.model.Param;
import de.olivervier.xhtml_viewer.model.Relation;
import de.olivervier.xhtml_viewer.model.Relation.RelationType;
import de.olivervier.xhtml_viewer.util.FileUtil;

public class JavaPageReader implements PageReader {

	List<Page> pages = new ArrayList<Page>();
	
	@Override
	public void init(Path path) {
		
		File inputFile = path.toFile();
		String fileExtension = FileUtil.getFileExtension(inputFile); 
		
		if(inputFile.isDirectory() || !fileExtension.equals("jar")) {
			throw new IllegalArgumentException("Expected path pointing to a JAR file");
		}
		
		this.pages = readPages(inputFile);
	}
	
	private List<String> findClassBinaryNames(List<File> jarFiles) {
		
		List<String> foundClassFilePaths = new ArrayList<String>();
		
		for (File f : jarFiles) {
			
			try (JarFile jf = new JarFile(f)) {
				
				Enumeration<JarEntry> jarEntries = jf.entries();
				
				while(jarEntries.hasMoreElements()) {
					
					JarEntry jarEntry = jarEntries.nextElement();
					String fileName = jarEntry.getName();
					
					if (fileName.endsWith(".class")) {
						fileName = fileName.replace(".class", "")
										   .replace("/", ".");
						foundClassFilePaths.add(fileName);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
				
		return foundClassFilePaths;
	}
	
	private List<Page> readPages(File inputFile) {
		
		List<File> files = new ArrayList<>();
		files.add(inputFile);
		
		List<String> classBinaryNames = findClassBinaryNames(files);
		
		URLClassLoader classLoader = createClassLoader(inputFile);
		
		List<Class<?>> classes = loadClasses(classLoader, classBinaryNames);
		
		List<Page> pages = createPages(classes, inputFile.toPath());
			
		return pages;
	}
	
	private URLClassLoader createClassLoader(File inputFile) {
		try {	
			URL[] urls = new URL[1]; 
			urls[0] = inputFile.toURI().toURL();
			return new URLClassLoader(urls);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private List<Class<?>> loadClasses (URLClassLoader classLoader, List<String> binaryNames) {
		List<Class<?>> classes = new ArrayList<Class<?>>(); 
		for(String binaryName : binaryNames) {
			try {				
				Class<?> clazz = classLoader.loadClass(binaryName);
				classes.add(clazz);	
			} catch (ClassNotFoundException | NoClassDefFoundError e) {
				System.err.println("Could not load class with binary name " + binaryName);
			}
		}
		return classes;
	}
	
	private List<Page> createPages(List<Class<?>> classes, Path jarPath) {
		
		// Go through each file
		// When page does not exist for file, create file
		
		// When page has superclass,
			// Create page when not exist
			// finally create relation from class before to parent
		
		List<Page> pages = new ArrayList<>();
		
		Map<Class<?>, Page> classMap = new HashMap<>();
		
		for(Class<?> clazz : classes) {
			
			if(!classMap.containsKey(clazz)) {
				classMap.put(clazz, new Page(clazz.getName(), jarPath, new ArrayList<Param>(), new ArrayList<Relation>()));
			}
			
			Class<?> superclazz;
			while((superclazz = clazz.getSuperclass()) != null && !(superclazz.getClass().equals(Object.class))) {
				if(!classMap.containsKey(superclazz)) {
					classMap.put(superclazz, new Page(superclazz.getName(), jarPath, new ArrayList<Param>(), new ArrayList<Relation>()));
				}
				
				classMap.get(clazz).getRelations().add(new Relation(classMap.get(superclazz), RelationType.COMPOSITION));
				clazz = superclazz;
			}
			
		}
		
		pages.addAll(classMap.values());
		return pages ;
	}

	@Override
	public List<Page> getPages() {
		return pages;
	}
}