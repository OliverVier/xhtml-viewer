package de.olivervier.xhtml_viewer.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {

		//Just for testing
		args = new String[]{"/home/test-oli/eclipse-workspace/xhtml-viewer-test-webapp/src/main/webapp", "/home/test-oli/eclipse-workspace/xhtml-viewer-test-webapp/src/main/webapp/test", "-r"};
		
		boolean recursive = args[args.length-1].equals("-r");
		List<String> filepaths = new ArrayList<>();
		File testFile = null;

		if(recursive == false) {
			testFile = new File(args[args.length-1]);
			
			if(testFile.isDirectory())  {
				for(int i = 0; i < args.length; i++) {
					filepaths.add(args[i]);
				}
			} else {
				for(int i = 0; i < args.length-1; i++) {
					filepaths.add(args[i]);
				}
			}
		} else {
			for(int i = 0; i < args.length-1; i++) {
				filepaths.add(args[i]);
			}
		}

		String[] filepathArray = new String[filepaths.size()];
		for(int i = 0; i < filepaths.size(); i++) {
			filepathArray[i] = filepaths.get(i);
		}

		new CLI().run(filepathArray, recursive);
	}
}