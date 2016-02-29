package chatapp;

import java.util.concurrent.TimeUnit;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;

public class TestClient {
	private static String hostname = "Erics-MacBook-Pro-2.local";
	public static void main(String[] args) throws RemoteException {
		if (args.length > 0) {
			hostname = args[0];
		}
		
		ChatClient client = new ChatClientImpl(hostname, "Client1");	
		client.createAccount();
		client.login();
		/*
		client.listAccounts("");
		client.listAccounts("Cl.*");
		client.createGroup(new HashSet<String>(Arrays.asList("Client1")), "Group1");
		client.listGroups("");
		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.sendMessage("Client2", "Hi");*/
		try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		client.logout();
	}	
}