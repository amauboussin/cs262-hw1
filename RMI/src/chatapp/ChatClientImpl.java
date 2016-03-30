package chatapp;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.Date;
import java.util.Hashtable;

/**
 * Implements the {@link ChatClient} class
 */
public class ChatClientImpl implements ChatClient {
	
	/**
	 * the IP address of the ChatServer to connect to
	 */
	private String serverHost;
	
	/**
	 * the username associates with this client
	 */
	private String myID;

	/**
	 * the stub of the connected ChatServer
	 */
	private ChatServer server = null;
	
	/**
	 * Creates a chat client, connects to server with call to
	 * {@link #getServer(String serverHostName, int port)}.
	 * 
	 * @param serverHostName IP address of server, as string
	 * @param port           port of server
	 * @param loginID        username for client
	 */
	public ChatClientImpl(String serverHostName, int port, String loginID){
		serverHost = serverHostName;
		myID = loginID;
		server = getServer(serverHost, port);
		System.out.println("Connected");
	}
	
	@Override
	public String getID() throws RemoteException{
		return myID;
	}
	
	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
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

	@Override
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
	
	@Override
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
	
	@Override
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
	
	@Override
	public void deliverMessage(Message msg) throws RemoteException{
		Date mdate = new Date(msg.msgTime());
		System.out.printf("At %s , user %s said: %s \n",mdate.toString(), msg.fromUser(),msg.msgText());		
	}
	
	/**
	 * Connect with server, performed once on initalization rather 
	 * than once per method call for efficiency
	 * 
	 * @param serverHost IP address of chat server, as string
	 * @param port       port of chat server
	 * @return           stub of connected server, stored in 
	 *                   private field on initialization for 
	 *                   remote calls in other methods
	 */
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
