package chatapp;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.Date;
import java.util.Hashtable;

public class ChatClientImpl implements ChatClient {
	

	
	private String serverHost;
	private String myID;
	private ChatServer server = null;
	
	public ChatClientImpl(String serverHostName, int port, String loginID){
		serverHost = serverHostName;
		myID = loginID;
		server = getServer(serverHost, port);
		System.out.println("Connected");
	}
	
	public String getID() throws RemoteException{
		return myID;
	}
	
	public void createAccount() throws RemoteException{
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
	
	
	public void deleteMyAccount() throws RemoteException{
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

	@Override
    public void login() throws RemoteException {
	    ChatClient client = (ChatClient) UnicastRemoteObject.exportObject(this,0);
		server.login(client);
	}
	
	@Override
    public void logout() throws RemoteException {
		server.logout(myID);
	}	
	
	public void listAccounts(String regexp) throws RemoteException{
		try{
			if(server != null){
				Set<String> accounts = server.getAccounts(regexp);
				System.out.printf("Accounts matching input %s: \n", regexp);
				System.out.printf("%s \n",accounts.toString());
			}
		} catch (RemoteException e) {
			System.out.println("Could not list accounts.");
			e.printStackTrace();
		}
	}

	public void createGroup(Set<String> members, String groupID)throws RemoteException{
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

	public void listGroups(String regexp) throws RemoteException{
		try{
			if(server != null){
				Hashtable<String, Set<String>> groups = server.getGroups(regexp);
				System.out.printf("Groups matching input %s: \n", regexp);
				System.out.printf("%s \n", groups.toString());
				}	
		} catch (RemoteException e) {
			System.out.println("Could not list accounts.");
			e.printStackTrace();
		}
	}
	
	public void sendMessage(String toUser, String msgText) throws RemoteException{
		try{
			if(server != null){
				Message newMsg = new MessageImpl(myID,toUser,msgText,System.currentTimeMillis());
				String potentialWarning = server.sendMessage(newMsg);
				System.out.printf("%s", potentialWarning);
				System.out.println("Message sent.");
			}
		} catch (RemoteException e) {
			System.out.printf("Could not send message \"%s\" to user %s. \n",msgText,toUser);
			e.printStackTrace();
		}
	}
	
	public void readMessages() throws RemoteException{
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
	
	public void deliverMessage(Message msg) throws RemoteException{
		Date mdate = new Date(msg.msgTime());
		System.out.printf("At %s , user %s said: %s \n",mdate.toString(), msg.fromUser(),msg.msgText());		
	}
	
	private ChatServer getServer(String serverHost, int port) {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		try {
			Registry useRegistry = LocateRegistry.getRegistry(serverHost, port);
			return ((ChatServer) useRegistry.lookup("chatServer"));
		} catch (Exception e) {
			System.out.println("Unable to find chat server");
			e.printStackTrace();
			return null;
		}
	}
}
