package de.olivervier.xhtml_viewer.cli;

public class Main {
	public static void main(String[] args) {

		//basepath
		//basepath ...relative_path

		//Just for testing
		//"/home/test-oli/eclipse-workspace/xhtml-viewer-test-webapp/src/main/webapp/test
		args = new String[]{"/home/test-oli/eclipse-workspace/xhtml-viewer-test-webapp/src/main/webapp"};
		
		String basepath = args[0];
		String[] filepaths = new String[args.length-1];
		
		if(filepaths.length == 0) {
			filepaths = new String[]{""};
		} else {
			for(int i = 0; i < filepaths.length; i++) {
				filepaths[i] = args[i+1];
			}
		}

		try {
			new CLI().run(basepath, filepaths);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}