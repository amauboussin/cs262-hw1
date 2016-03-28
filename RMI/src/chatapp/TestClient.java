package chatapp;

import java.util.concurrent.TimeUnit;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * test class for chatapp, client 1
 * <p>
 * This class has a main method that starts a two ChatClients and tests 
 * the specified functionality: creates accounts, logs in, lists accounts
 * (tests wildcard), creates and lists groups, sends and checks group 
 * messages, logs out, and deletes accounts.
 */
public class TestClient {
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
     	 * at hardcoded IP address and port. Test functionality.
     	 * 
     	 * @param args ignored
     	 */
	public static void main(String[] args) throws RemoteException {
		if (args.length > 0) {
			hostname = args[0];
		}
		
		ChatClient eric = new ChatClientImpl(hostname, port, "Eric");
		ChatClient erik = new ChatClientImpl(hostname, port, "Erik");	
		eric.createAccount();
		erik.createAccount();
		eric.login();
		eric.listAccounts("");
		eric.listAccounts(".*c");
		eric.createGroup(new HashSet<String>(Arrays.asList("Eric","Erik")), "MyGroup");
		eric.listGroups("");
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		eric.sendMessage("MyGroup", "Hi Eric and Erik");
		try {
			TimeUnit.SECONDS.sleep(30);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		erik.readMessages();
		eric.logout();
		eric.deleteMyAccount();
		erik.deleteMyAccount();
	}	
}
