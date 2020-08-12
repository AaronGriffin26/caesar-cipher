import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	public static final int LINE_LIMIT = 8;

	public static void main(String[] args) {
		System.out.println("When you put in your message, surround in quotation marks to specify file paths.");
		boolean encrypting = askForEncryption();
		String message = askForMessage();
		int key = askForKey();
		String newMessage;
		if(encrypting) {
			newMessage = encrypt(message, key);
			System.out.println(skimMessage(newMessage));
		}
		else {
			newMessage = decrypt(message, key);
			System.out.println(skimMessage(newMessage));
		}
		while(true) {
			try {
				if(askToSaveToFile()) {
					Path path = askForSavePath();
					Files.writeString(path, newMessage);
				}
				break;
			}
			catch(IOException e) {
				System.out.println("Could not find file.");
			}
			catch(InvalidPathException e) {
				System.out.println("Not a valid path.");
			}
		}
	}

	private static boolean askForEncryption() {
		String input = "";
		while(!input.matches("e|encrypt|d|decrypt")) {
			System.out.println("Do you wish to encrypt or decrypt a message?");
			Scanner scanner = new Scanner(System.in);
			input = scanner.nextLine();
		}
		return input.matches("e|encrypt");
	}

	private static String askForMessage() {
		while(true) {
			try {
				System.out.println("Enter your message:");
				Scanner scanner = new Scanner(System.in);
				String input = scanner.nextLine();
				if(!input.startsWith("\""))
					return input;
				input = input.substring(1);
				if(input.endsWith("\""))
					input = input.substring(0, input.length() - 1);
				Path path = Paths.get(input);
				return Files.readString(path, Charset.defaultCharset());
			}
			catch(IOException e) {
				System.out.println("Could not find file.");
			}
			catch(InvalidPathException e) {
				System.out.println("Not a valid path.");
			}
		}
	}

	private static int askForKey() {
		while(true) {
			try {
				System.out.println("Enter the key number (1-52)");
				Scanner scanner = new Scanner(System.in);
				String input = scanner.nextLine();
				return Integer.parseInt(input);
			}
			catch(NumberFormatException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	private static String encrypt(String input, int key) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < input.length(); i++) {
			int index = LETTERS.indexOf(input.charAt(i));
			if(index != -1) {
				index = ((index + key) % LETTERS.length() + LETTERS.length()) % LETTERS.length();
				builder.append(LETTERS.charAt(index));
			}
			else
				builder.append(input.charAt(i));

		}
		return builder.toString();
	}

	private static String decrypt(String input, int key) {
		return encrypt(input, -key);
	}

	private static String skimMessage(String message) {
		if(message.length() == 0)
			return message;
		int index = 0;
		int count = 0;
		while(index >= 0) {
			index = message.indexOf('\n', index + 1);
			count++;
			if(count >= LINE_LIMIT)
				return message.substring(0, index) + "...";
		}
		return message;
	}

	private static boolean askToSaveToFile() {
		String input = "";
		while(!input.matches("y|yes|n|no")) {
			System.out.println("Would you like to save the new message to a file? (yes or no)");
			Scanner scanner = new Scanner(System.in);
			input = scanner.nextLine();
		}
		return input.matches("y|yes");
	}

	private static Path askForSavePath() {
		System.out.println("Enter the file path:");
		Scanner scanner = new Scanner(System.in);
		String input = scanner.nextLine();
		return Paths.get(input);
	}
}
