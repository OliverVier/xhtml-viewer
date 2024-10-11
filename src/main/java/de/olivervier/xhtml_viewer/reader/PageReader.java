package de.olivervier.xhtml_viewer.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PageReader {

	public List<File> filterPages(String[] dirPath, boolean recursively) throws IllegalArgumentException{
		return recursively ? filterPagesRecursive(dirPath) : filterPagesNormal(dirPath);
	}

	/**
	 * Reads in xhtml pages in the given directory paths
	 * @param dirPath absolute directory paths
	 * @return list of xhtml files as List<File>
	 */
	public List<File> filterPagesNormal(String ...dirPath) throws IllegalArgumentException {
		
		if(dirPath == null || dirPath.length == 0) {
			throw new IllegalArgumentException("No directories given. Check filepath/s!");
		}
		
		List<File> directories = new ArrayList<>();
		for(String fp : dirPath) {
			File dir = new File(fp);
			if(!dir.isDirectory()) {
				throw new IllegalArgumentException("File is not a directory");
			}

			if(dir.exists()) {
				directories.add(new File(fp));
			}
			else {				
				System.err.println("Filepath " + dir + " does not exist!");
			}			
		}
		
		//return null, if no filepath is found
		if(directories.isEmpty()) {
			throw new IllegalArgumentException("No directories with given filepaths found!");
		}

		List<File> files = new ArrayList<File>();
		for(File dir : directories) {
			if(dir.listFiles()==null) {
				continue;
			}
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
	public List<File> filterPagesRecursive(String ...dirPath) throws IllegalArgumentException {
		if(dirPath == null) {
			System.err.println("No paths given");
			return null;
		}
		
		List<File> directories = new ArrayList<>();
		for(String fp : dirPath) {
			if(!new File(fp).isDirectory()) {
				throw new IllegalArgumentException("Path at " + fp + " is not a directory!");
			}
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