package de.olivervier.xhtml_viewer.reader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PageReader {

	/**
	 * Reads in xhtml pages in the given directory paths
	 * @param dirPaths absolute directory paths
	 * @return list of xhtml files as List<File>
	 */
	public List<File> filterPages(String basepath, String ...dirPaths) throws IllegalArgumentException {
		
		if(basepath == null) {
			throw new IllegalArgumentException("No basepath given");
		}

		if(dirPaths == null || dirPaths.length == 0) {
			throw new IllegalArgumentException("No directories given. Check filepath/s!");
		}

		File basepathFile = new File(basepath);
		if(!basepathFile.exists()) {
			throw new IllegalArgumentException("path " + basepathFile.getAbsolutePath() + " does not exist");
		}
		
		List<File> directories = new ArrayList<>();
		for(String filepath : dirPaths) {

			String path = basepath+filepath;
			File dir = new File(path);
			if(!dir.isDirectory()) {
				throw new IllegalArgumentException("File is not a directory");
			}

			if(dir.exists()) {
				directories.add(new File(path));
			}
			else {				
				System.err.println("Filepath " + path + " does not exist!");
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
}