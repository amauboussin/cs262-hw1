package chatapp;

import java.rmi.RemoteException;

public class TestServer {

	public static void main(String[] args) {
		try{
			ChatServer myserver = new ChatServerImpl();
			System.out.printf("hostname: %s",myserver.hostname());
			System.out.println();
		} catch (RemoteException e) {
			System.out.println("Could not start server.");
			e.printStackTrace();
		}
	}	
}