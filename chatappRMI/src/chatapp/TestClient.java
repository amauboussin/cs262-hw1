package chatapp;

import java.util.UUID;

public class TestClient {
	private static String hostname = "Erics-MacBook-Pro-2.local";
	public static void main(String[] args) {
		if (args.length > 0) {
			hostname = args[0];
		}
		ChatClient client = new ChatClientImpl(hostname, UUID.randomUUID());	
		client.createAccount();
	}	
}