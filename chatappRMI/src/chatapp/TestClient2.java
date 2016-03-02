package chatapp;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class TestClient2 {
<<<<<<< HEAD
	private static String hostname = "10.252.192.83";
	private static int port = 1358;
=======
	private static String hostname = "10.252.195.239";
	private static int port = 3550;
>>>>>>> RMI
	public static void main(String[] args) throws RemoteException {
		if (args.length > 0) {
			hostname = args[0];
		}
		ChatClient client = new ChatClientImpl(hostname, port, "Alex");	
		client.createAccount();
		client.login();
		client.sendMessage("Eric", "Hi Eric");
		/* try {
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} */
		return;
	}	
} 