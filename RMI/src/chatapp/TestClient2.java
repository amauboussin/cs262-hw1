package chatapp;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * test class for chatapp, client 2
 * <p>
 * This class has a main method that starts a ChatClient at the
 * specified port and hostnames, creates an account, 
 * logs in as Alex, and sends a message to Eric
 */
public class TestClient2 {
	/**
     	 * hardcoded IP address of the server the client will connect to
     	 */
	private static String hostname = "10.252.192.83";
		
	/**
     	 * hardcoded port on the server the client will connect to
     	 */
	private static int port = 1358;
	
	/**
     	 * Attempt to start a ChatClient connected to ChatServer
     	 * at hardcoded IP address and port. Create an account, 
     	 * log in as Alex, say hi to Eric.
     	 * 
     	 * @param args ignored
     	 */
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
