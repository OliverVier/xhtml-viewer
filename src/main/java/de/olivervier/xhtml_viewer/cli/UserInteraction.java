package de.olivervier.xhtml_viewer.cli;

import java.util.Scanner;

public final class UserInteraction {
	
	private static Scanner scanner = new Scanner(System.in);
	
	public static String promptUser(String promptMessage) {
		sendMessage(promptMessage);
		String userInput = readInput();
		return userInput;
	}
	
	public static String readInput() {
		String userInput = scanner.next();
		return userInput;
	}
	
	public static void sendMessage(String message) {
		System.out.println(message);
	}
}