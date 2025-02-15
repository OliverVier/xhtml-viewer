package de.olivervier.xhtml_viewer.util;

import java.io.File;

public class FileUtil {

    /**
     * Reads filepath independent from
     * OS folder separator ('/' or '\')
     * @return absolute file path
     */
    public static String getFilePath(String absolutePath) {
        return new File(absolutePath).getAbsolutePath();
    }

    /**
     * Read file extension of absolutePath
     * 
     * Examples
     * 
     * <p>- test.pdf -> pdf</p>
     * <p>- test.p -> p </p>
     * <p>- test. -> null </p>
     * <p>- test -> null </p>
     * 
     * @param absolutePath
     * @return file-extension when exists, returns null when extension length is 
     * 0 or file has no extension
     */
    public static String getFileExtension(String absolutePath) {
        return readFileNameExtension(absolutePath);
    }

    /**
     * Read file extension of file
     * 
     * Examples
     * 
     * <p>- test.pdf -> pdf</p>
     * <p>- test.p -> p </p>
     * <p>- test. -> null </p>
     * <p>- test -> null </p>
     * 
     * @param file
     * @return file-extension when exists, returns null when extension length is 
     * 0 or file has no extension
     */
    public static String getFileExtension(File file) {
        return readFileNameExtension(file.getName());
    }

    private static String readFileNameExtension(String name) {
        int lastDotIndex = name.lastIndexOf('.');
        if(lastDotIndex == -1) {
            return null;
        }

        String extension = name.substring(lastDotIndex, name.length());
        return extension.length() > 0 ? extension : null;
    }

    /**
     * Finds the relative path between a base and <b>known</b> subfolder-path.
     * </br>
     * <br> - basepath:      C:\\Homework\ </br>
     * <br> - subfolder:     C:\\Homework\Math\homework1.pdf </br>
     * <br> - relative path: Math\homework1.pdf </br>
     *
     * @return relative path
     */
    public static String getRelativePath(String basepath, String subfolderPath) {
        String relativePathName = subfolderPath.replace(basepath, "");
		if(relativePathName.charAt(0) == File.separatorChar) {
			relativePathName = relativePathName.substring(1, relativePathName.length());
		}
        return relativePathName;
    }
}