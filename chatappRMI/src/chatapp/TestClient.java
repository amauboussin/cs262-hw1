package chatapp;

import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.HashSet;

public class TestClient {
	private static String hostname = "Erics-MBP-2.westell.com";
	public static void main(String[] args) {
		if (args.length > 0) {
			hostname = args[0];
		}
		
		ChatClient client = new ChatClientImpl(hostname, "Client1");	
		client.createAccount();
		client.listAccounts("");
		client.listAccounts("Cl.*");
		client.createGroup(new HashSet<String>(Arrays.asList("Client1")), "Group1");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.sendMessage("Client2", "Hi");
	}	
}