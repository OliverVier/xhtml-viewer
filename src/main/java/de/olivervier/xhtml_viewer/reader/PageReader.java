package de.olivervier.xhtml_viewer.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PageReader {

	/**
	 * Reads in xhtml pages in the given directory paths
	 * @param dirPath absolute directory paths
	 * @return list of xhtml files as List<File>
	 */
	public List<File> filterPages(String ...dirPath) {
		
		if(dirPath == null) {
			System.err.println("No paths given");
			return null;
		}
		
		List<File> directories = new ArrayList<>();
		for(String fp : dirPath) {
			directories.add(new File(fp));
		}

		List<File> files = new ArrayList<File>();
		for(File dir : directories) {			
			for(File file : dir.listFiles()) {
				if(file.getName().lastIndexOf(".xhtml") != -1) {				
					files.add(file);
				}
			}
		}
		return files;		
	}
	
	
	/**
	 * Reads in xhtml pages in the given directory paths, 
	 * including the subdirectories of given directory
	 * @param dirPath absolute directory paths
	 * @return list of xhtml files as List<File>
	 */
	public List<File> filterPagesRecursive(String ...dirPath) {
		if(dirPath == null) {
			System.err.println("No paths given");
			return null;
		}
		
		List<File> directories = new ArrayList<>();
		for(String fp : dirPath) {
			directories.addAll(readDirectoriesRec(fp));
		}

		List<File> files = new ArrayList<File>();
		for(File dir : directories) {			
			for(File file : dir.listFiles()) {
				if(file.getName().lastIndexOf(".xhtml") != -1) {				
					files.add(file);
				}
			}
		}
		return files;
	}
	
	/**
	 * Reads directories paths recursively. Helper function for filterPagesRecursive()
	 * @param dirPath
	 * @return list of directories as List<File>
	 */
	private List<File> readDirectoriesRec(String dirPath) {
		List<File> dirs = new ArrayList<>();
		dirs.add(new File(dirPath));
		for(File file : new File(dirPath).listFiles()) {
			if(file.isDirectory()) {
				dirs.addAll(readDirectoriesRec(file.getAbsolutePath()));
			}
		}
		return dirs;
	}	
}