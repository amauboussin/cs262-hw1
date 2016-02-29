package chatapp;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Set;
import java.util.Date;

public class ChatClientImpl implements ChatClient {
	
	private String serverHost;
	private String myID;
	private ChatServer server = null;
	
	public ChatClientImpl(String serverHostName, String loginID){
		serverHost = serverHostName;
		myID = loginID;
		server = getServer(serverHost);
		System.out.println("Connected");
	}
	
	public void createAccount() {
		try{
			if(server != null){
				if (server.createAccount(myID)){
					System.out.printf("Created new account %s.\n",myID);
				} else {
					System.out.printf("Account %s already exists.\n",myID);
				}
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
				System.out.printf("Account %s deleted. \n",myID);
			}
		} catch (RemoteException e) {
			System.out.println("Could not delete account.");
			e.printStackTrace();
		}
	}

	public void listAccounts(String regexp) {
		try{
			String toMatch = regexp;
			if (toMatch.equals("")) {
				toMatch = "(.*)";
			}
			if(server != null){
				Set<String> accounts = server.getAccounts();
				System.out.printf("Accounts matching input %s: \n", regexp);
				for (String a : accounts){
					if (a.matches(toMatch)) {
						System.out.printf("%s \n",a);
					}
				}	
			}
		} catch (RemoteException e) {
			System.out.println("Could not list accounts.");
			e.printStackTrace();
		}
	}

	public void createGroup(Set<String> members, String groupID){
		try{
			if(server != null){
				server.createGroup(members, groupID);
				System.out.printf("Group %s created. \n",groupID);
			}
		} catch (RemoteException e) {
			System.out.println("Could not create group.");
			e.printStackTrace();
		}
	}
	
	public void listGroups(String regexp) {
		try{
			String toMatch = regexp;
			if (toMatch.equals("")) {
				toMatch = "(.*)";
			}
			if(server != null){
				Set<String> groups = server.getGroups();
				System.out.printf("Accounts matching input %s: \n", regexp);
				for (String g : groups){
					if (g.matches(toMatch)) {
						System.out.printf("%s \n",g);
					}
				}	
			}
		} catch (RemoteException e) {
			System.out.println("Could not list accounts.");
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String toUser, String msgText){
		try{
			if(server != null){
				Message newMsg = new MessageImpl(myID,toUser,msgText,System.currentTimeMillis());
				server.sendMessage(newMsg);
				System.out.println("Message sent.");
			}
		} catch (RemoteException e) {
			System.out.printf("Could not send message \"%s\" to user %s. \n",msgText,toUser);
			e.printStackTrace();
		}
	}
	
	
	public void readMessages() {
		try{
			if(server != null){
				Set<Message> msgs = server.deliverMessages(myID);
				for (Message m : msgs){
					Date mdate = new Date(m.msgTime());
					System.out.printf("At %s , user %s said: %s \n",mdate.toString(), m.fromUser(),m.msgText());
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
