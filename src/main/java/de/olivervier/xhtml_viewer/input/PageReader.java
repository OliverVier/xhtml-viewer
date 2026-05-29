package de.olivervier.xhtml_viewer.input;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.util.FileUtil;

public class PageReader {

	private List<Page> pages;


	public PageReader(String basepath, String[] dirPaths) {
		List<File> files = readFiles(basepath, dirPaths);
		XHTMLReader reader = new XHTMLReader();
		this.pages = reader.readPages(basepath, files);
	}

	public List<Page> getPages() {
		return pages;
	}

	/**
	 * Reads in xhtml pages in the given directory paths
	 * @param dirPaths absolute directory paths
	 * @return list of xhtml files as List<File>
	 */
	private List<File> readFiles(String basepath, String ...dirPaths) throws IllegalArgumentException {
		
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
		
		// Find subfolders
		List<File> dirsWithSubfolders = FileUtil.findDirectoriesRecursive(directories);

		List<File> files = new ArrayList<File>();
		for(File dir : dirsWithSubfolders) {
			if(dir.listFiles()==null) {
				continue;
			}
			for(File file : dir.listFiles()) {
				String fileExtension = FileUtil.getFileExtension(file);
				if(fileExtension != null && FileUtil.getFileExtension(file).equals(".xhtml")) {				
					files.add(file);
				}
			}
		}
		return files;		
	}
}