package chatapp;

import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

public class TestClient2 {
	private static String hostname = "Erics-MacBook-Pro-2.local";
	public static void main(String[] args) throws RemoteException {
		if (args.length > 0) {
			hostname = args[0];
		}
		ChatClient client = new ChatClientImpl(hostname, "Client2");	
		client.createAccount();
		client.login();
		client.sendMessage("Client1", "Hi");
		client.logout();
	}	
}