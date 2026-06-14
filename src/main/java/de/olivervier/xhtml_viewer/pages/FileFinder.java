package de.olivervier.xhtml_viewer.pages;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import de.olivervier.xhtml_viewer.model.InputOption;
import de.olivervier.xhtml_viewer.util.FileUtil;

public class FileFinder {

    private Path basePath;
	private InputOption extension;
    
    public FileFinder(Path basepath, InputOption extension) {
        if(basepath == null) {
			throw new IllegalArgumentException("No basepath given");
		}

        File file = basepath.toFile();
		if(!file.exists()) {
			throw new IllegalArgumentException("path " + file.getAbsolutePath() + " does not exist");
		}

        if(!basepath.toFile().isDirectory()) {
            throw new IllegalArgumentException("Given path is not a directory");
        }
		if(extension == null) {
			throw new IllegalArgumentException("Extension must not be null");
		}

        this.basePath = basepath;
		this.extension = extension;
    }

    /**
	 * Reads in files in the given directory path
	 * @return list of files
	 */
	public List<File> read() throws IllegalArgumentException {
        
		List<File> files = new ArrayList<File>();
		
		List<File> directories = FileUtil.findSubdirectories(List.of(basePath.toFile()));
		for(File dir : directories) {
			
			File[] resolvedFiles = dir.listFiles();
			
			if(resolvedFiles == null) {
				continue;
			}
			
			for(File file : resolvedFiles) {
				String fileExtension = FileUtil.getFileExtension(file).toUpperCase();
				if(fileExtension != null && fileExtension.equals(extension.getName())) {				
					files.add(file);
				}
			}
		}
		return files;		
	}
}
