package chatapp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * Implements the {@link ChatServer} class
 */
public class ChatServerImpl implements ChatServer {
	
	/** 
	 * a list of usernames for which an account had been created and not deleted
	 */
	private Set<String> userList = new HashSet<String>();
	
	/** 
	 * a table of groups for which an account had been created and not deleted.
	 * a group has a name and is a set of usernames.
	 */
	private Hashtable<String, Set<String>> groupTable = new Hashtable<String, Set<String>>();
	
	/** 
	 * a set of undelivered messages for each logged-off recipient user
	 */
	private Hashtable<String, Set<Message>> msgQueues = new Hashtable<String, Set<Message>>();
	
	/** 
	 * the registry by which clients can find this server
	 */
	private Registry registry;
	
	/** 
	 * the server stub, which is exported to clients so that they can
	 * call methods on this server
	 */
	private ChatServer myStub;
	
	/**
	 * a table of logged-in client stubs, allowing the server to immediately deliver messages
	 */
	private Hashtable<String, ChatClient> loggedInClients = new Hashtable<String, ChatClient>();
	
	/**
	 * Creates and exports a ChatServer by calling {@link #exportServer(port)}
	 * 
	 * @param port port on which server is exported
	 */
	public ChatServerImpl(int port) {
		try {
			exportServer(port);
		} catch (RemoteException e) {
			System.out.println(e.getMessage());
			System.out.println("unable to export chat server");
			System.exit(1);
		}
	}
	
	@Override
	public Boolean createAccount(String newID) throws RemoteException {
		if (userList.contains(newID)) {
			return false;
		}
		userList.add(newID);
		msgQueues.put(newID, new HashSet<Message>());
		System.out.printf("Added new account %s.\n",newID);
		return true;
	}
			
	@Override
	public void deleteAccount(String userID) throws RemoteException{
		userList.remove(userID);
		msgQueues.remove(userID);
	}
	
	@Override
    public void login(ChatClient client) throws RemoteException {
		loggedInClients.put(client.getID(), client);
	}
	
	@Override
    public void logout(String clientID) throws RemoteException {
		if (loggedInClients.containsKey(clientID)) {
			loggedInClients.remove(clientID);
		}
	}
	
	@Override
	public Set<String> getAccounts(String regexp) throws RemoteException{
		String toMatch = regexp;
		if (toMatch.equals("")) {
			toMatch = "(.*)";
		}
		Set<String> accounts = new HashSet<String>();
		for (String a : userList){
			if (a.matches(toMatch)) {
				accounts.add(a);
			}
		}	
		return accounts;
	}
	
	@Override
	public void createGroup(Set<String> members, String groupID) throws RemoteException{
		groupTable.put(groupID,members); 
	}
	
 	@Override
	public Hashtable<String, Set<String>> getGroups(String regexp) throws RemoteException{
 		String toMatch = regexp;
		if (toMatch.equals("")) {
			toMatch = "(.*)";
		}
		Hashtable<String, Set<String>> groups = new Hashtable<String, Set<String>>();
		for (String g : groupTable.keySet()){
				if (g.matches(toMatch)) {
					groups.put(g, groupTable.get(g));
				}
			}	
 		return groups; 
	}
	
	
	@Override
	public String sendMessage(Message newMsg) throws RemoteException{
		if (! userList.contains(newMsg.fromUser())) {
			System.out.println("Please create an account before sending a message.");
			return "";
		}
		
		if (! loggedInClients.containsKey(newMsg.fromUser())) {
			return "Warning: you are not logged in. \n";
		}
		
		String toUser = newMsg.toUser();
		if (userList.contains(toUser)) {
			if (loggedInClients.containsKey(toUser)) {
				try {
					loggedInClients.get(toUser).deliverMessage(newMsg);
				} catch  (RemoteException e) {
					// if toUser is offline but did not logout, logout toUser and add it to its queue
					logout(toUser);
					msgQueues.get(toUser).add(newMsg);
				}
			} else {
				msgQueues.get(toUser).add(newMsg);
			}
		} else if (groupTable.containsKey(toUser)) {
			Set<String> members = groupTable.get(toUser);
			for (String m : members){
				if (userList.contains(m)) {
					if (loggedInClients.containsKey(m)) {
						try {
							loggedInClients.get(m).deliverMessage(newMsg);
						} catch  (RemoteException e) {
							// if m is offline but did not logout, logout toUser and add it to its queue
							logout(m);
							msgQueues.get(m).add(newMsg);
						}
					} else {
						msgQueues.get(m).add(newMsg);
					}
				} 
			}
		} else {
			System.out.printf("No account with user name %s. \n", toUser);
		}
		return "";
	}
	
	@Override
	public Set<Message> deliverMessages(String toUser) throws RemoteException{
		Set<Message> msgs = msgQueues.get(toUser);
		msgQueues.put(toUser, new HashSet<Message>());
		return msgs;
	}
	
	@Override
	public String hostname() {
		try{
			InetAddress localhost = java.net.InetAddress.getLocalHost();
			return localhost.getHostName();			
		} catch (UnknownHostException e) {
			System.out.println("Unknown hostname");
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Makes server available for RMI calls
	 * 
	 * @param port port on which server is exported
	 */
	private void exportServer(int port) throws RemoteException {
		if(System.getSecurityManager() == null){
			System.setSecurityManager(new SecurityManager());
		}
		registry = LocateRegistry.createRegistry(port);
		myStub = (ChatServer) UnicastRemoteObject.exportObject(this,port);
		registry.rebind("chatServer",myStub);
	}
}
