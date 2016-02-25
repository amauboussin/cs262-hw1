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

public class ChatServerImpl implements ChatServer {
	
	private Set<String> userList = new HashSet<String>();
	private Hashtable<String, Set<String>> groupTable = new Hashtable<String, Set<String>>();
	private Hashtable<String, Set<Message>> msgQueues = new Hashtable<String, Set<Message>>();
	private Registry registry;
	private ChatServer myStub;
	
	/*public ChatServerImpl(Set<String> initUserList, Hashtable<String, Set<String>> initGroupTable,
			Hashtable<String, Set<Message>> initMsgQueues){
		userList = initUserList;
		groupTable = initGroupTable;
		msgQueues = initMsgQueues;*/
	public ChatServerImpl() {
		try {
			exportServer();
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
		System.out.printf("Added new account %s.\n",newID);
		return true;
	}
			
	@Override
	public void deleteAccount(String userID) throws RemoteException{
		userList.remove(userID);
		msgQueues.remove(userID);
		// also remove from all groups?
	}

	
	@Override
	public Set<String> listAccounts() throws RemoteException{
			return userList;
	}
	
	@Override
	public String createGroup(Set<String> members, String groupID) throws RemoteException{
		groupTable.put(groupID,members); // any point in checking that all members are in userList?
		return groupID;
	}
	
	@Override
	public Set<String> listGroups() throws RemoteException{
		return groupTable.keySet(); // do we also need to list members?
	}
	
	
	@Override
	public void sendMessage(Message newMsg) throws RemoteException{
		String toUser = newMsg.toUser();
		if (userList.contains(toUser)) {
			Set<Message> Msgs = new HashSet<Message>();
			if (msgQueues.containsKey(toUser)) { 
				Msgs = msgQueues.get(toUser);
			}
			Msgs.add(newMsg);
			msgQueues.put(toUser, Msgs);
		} else if (groupTable.containsKey(toUser)) {
			Set<String> members = groupTable.get(toUser);
			for (String m : members){
				if (userList.contains(m)) {
					Set<Message> Msgs = msgQueues.get(m);
					Msgs.add(newMsg);
					msgQueues.put(m, Msgs);
				} // else, we don't really care?
			}
		} else {
			// exception: user *** not found
		}
	}
	
	@Override
	public Set<Message> deliverMessages(String toUser) throws RemoteException{
		Set<Message> msgs = msgQueues.get(toUser);
		msgQueues.remove(toUser);
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
	
	private void exportServer() throws RemoteException {
		if(System.getSecurityManager() == null){
			System.setSecurityManager(new SecurityManager());
		}
		registry = LocateRegistry.getRegistry();
		myStub = (ChatServer) UnicastRemoteObject.exportObject(this,0);
		registry.rebind("chatServer",myStub);
	}
}
