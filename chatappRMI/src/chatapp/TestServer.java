package chatapp;

import java.rmi.RemoteException;

public class TestServer {
	private static int port = 3550;
	public static void main(String[] args) {
		try{
			ChatServer myserver = new ChatServerImpl(port);
			System.out.printf("hostname: %s, port: %d",myserver.hostname(), port);
			System.out.println();
		} catch (RemoteException e) {
			System.out.println("Could not start server.");
			e.printStackTrace();
		}
	}	
}