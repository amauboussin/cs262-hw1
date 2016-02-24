package chatapp;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;
import java.util.UUID;
import java.util.Date;

public class ChatClientImpl implements ChatClient {
	
	private String serverHost;
	private UUID myID;
	private ChatServer server = null;
	
	public ChatClientImpl(String serverHostName, UUID loginID){
		serverHost = serverHostName;
		myID = loginID;
		server = getServer(serverHost);
		System.out.println("Connected");
	}
	
	public void createAccount() {
		try{
			if(server != null){
				myID = server.createAccount();
				System.out.printf("Signed in under new account %s.",myID.toString());
				System.out.println();
			}
		} catch (RemoteException e) {
			System.out.println("Could not create account.");
			e.printStackTrace();
		}
	}
	
	
	public void deleteMyAccount() {
		try{
			if(server != null){
				server.deleteAccount(myID);
				System.out.printf("Account %x deleted.",myID);
			}
		} catch (RemoteException e) {
			System.out.println("Could not delete account.");
			e.printStackTrace();
		}
	}

	public void listAccounts() {
		try{
			if(server != null){
				Set<UUID> accounts = server.listAccounts();
				System.out.println("Current accounts:");
				for (UUID a : accounts){
					System.out.printf("%x",a);
				}	
			}
		} catch (RemoteException e) {
			System.out.println("Could not list accounts.");
			e.printStackTrace();
		}
	}

	public void createGroup(Set<UUID> members){
		try{
			if(server != null){
				UUID groupID = server.createGroup(members);
				System.out.printf("Group %x created.",groupID);
			}
		} catch (RemoteException e) {
			System.out.println("Could not create group.");
			e.printStackTrace();
		}
	}
	
	public void listGroups() {
		try{
			if(server != null){
				Set<UUID> groups = server.listGroups();
				System.out.println("Current groups:");
				for (UUID g : groups){
					System.out.printf("%x",g);
				}	
			}
		} catch (RemoteException e) {
			System.out.println("Could not list groups.");
			e.printStackTrace();
		}
	}
	
	public void sendMessage(UUID toUser, String msgText){
		try{
			if(server != null){
				Message newMsg = new MessageImpl(myID,toUser,msgText,System.currentTimeMillis());
				server.sendMessage(newMsg);
				System.out.println("Message sent.");
			}
		} catch (RemoteException e) {
			System.out.printf("Could not send message \"%s\" to user %x.",msgText,toUser);
			e.printStackTrace();
		}
	}
	
	
	public void readMessages() {
		try{
			if(server != null){
				Set<Message> msgs = server.deliverMessages(myID);
				for (Message m : msgs){
					Date mdate = new Date(m.msgTime());
					System.out.printf("At " + mdate + ", user %x said:%n %s",m.fromUser(),m.msgText());
				}
			}
		} catch (RemoteException e) {
			System.out.println("Could not read messages, possibly lost forever.");
			e.printStackTrace();
		}
	}
	
	private ChatServer getServer(String serverHost) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			Registry useRegistry = LocateRegistry.getRegistry(serverHost);
			return ((ChatServer) useRegistry.lookup("chatServer"));
		} catch (Exception e) {
			System.out.println("Unable to find chat server");
			e.printStackTrace();
			return null;
		}
	}
}
