package chatapp;

import java.util.concurrent.TimeUnit;

public class TestClient2 {
	private static String hostname = "Erics-MacBook-Pro-2.local";
	public static void main(String[] args) {
		if (args.length > 0) {
			hostname = args[0];
		}
		ChatClient client = new ChatClientImpl(hostname, "");	
		client.createAccount("Client2");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.readMessages();
	}	
}