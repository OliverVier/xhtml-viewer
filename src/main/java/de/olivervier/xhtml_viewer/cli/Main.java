package de.olivervier.xhtml_viewer.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String[] args) {

		//Just for testing
		//"/home/test-oli/eclipse-workspace/xhtml-viewer-test-webapp/src/main/webapp/test
		args = new String[]{"/home/test-oli/eclipse-workspace/xhtml-viewer-test-webapp/src/main/webapp"};
		
		boolean recursive = args[args.length-1].equals("-r");
		List<String> filepaths = new ArrayList<>();

		File file = new File(args[args.length-1]);
		if(recursive==false && !file.isDirectory()) {
			System.err.println("Path at " + args[args.length-1] + " is not a directory");
			return;
		}

		int lastFilepathIndex = recursive == false ? args.length: args.length -1;

		for(int i = 0; i < lastFilepathIndex; i++) {
			filepaths.add(args[i]);
		}

		String[] filepathArray = new String[filepaths.size()];
		for(int i = 0; i < filepaths.size(); i++) {
			filepathArray[i] = filepaths.get(i);
		}

		new CLI().run(filepathArray, recursive);
	}
}