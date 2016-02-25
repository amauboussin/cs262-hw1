package chatapp;

import java.util.concurrent.TimeUnit;

public class TestClient {
	private static String hostname = "Erics-MacBook-Pro-2.local";
	public static void main(String[] args) {
		if (args.length > 0) {
			hostname = args[0];
		}
		ChatClient client = new ChatClientImpl(hostname, "");	
		client.createAccount("Client1");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.sendMessage("Client2", "Hi");
	}	
}