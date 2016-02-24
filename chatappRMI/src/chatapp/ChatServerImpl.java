package chatapp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Set;
import java.util.UUID;
import java.util.HashSet;
import java.util.Hashtable;

public class ChatServerImpl implements ChatServer {
	
	private Set<UUID> userList = new HashSet<UUID>();
	private Hashtable<UUID, Set<UUID>> groupTable = new Hashtable<UUID, Set<UUID>>();
	private Hashtable<UUID, Set<Message>> msgQueues = new Hashtable<UUID, Set<Message>>();
	private Registry registry;
	private ChatServer myStub;
	
	/*public ChatServerImpl(Set<UUID> initUserList, Hashtable<UUID, Set<UUID>> initGroupTable,
			Hashtable<UUID, Set<Message>> initMsgQueues){
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
	public UUID createAccount() throws RemoteException {
		UUID newID = UUID.randomUUID();
		userList.add(newID);
		System.out.println("Added new account");
		return(newID);
	}
			
	@Override
	public void deleteAccount(UUID userID) throws RemoteException{
		userList.remove(userID);
		msgQueues.remove(userID);
		// also remove from all groups?
	}

	
	@Override
	public Set<UUID> listAccounts() throws RemoteException{
			return userList;
	}
	
	@Override
	public UUID createGroup(Set<UUID> members) throws RemoteException{
		UUID groupID = UUID.randomUUID();
		groupTable.put(groupID,members); // any point in checking that all members are in userList?
		return groupID;
	}
	
	@Override
	public Set<UUID> listGroups() throws RemoteException{
		return groupTable.keySet(); // do we also need to list members?
	}
	
	
	@Override
	public void sendMessage(Message newMsg) throws RemoteException{
		UUID toUser = newMsg.toUser();
		if (userList.contains(toUser)) {
			Set<Message> Msgs = msgQueues.get(toUser);
			Msgs.add(newMsg);
			msgQueues.put(toUser, Msgs);	
		} else if (groupTable.containsKey(toUser)) {
			Set<UUID> members = groupTable.get(toUser);
			for (UUID m : members){
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
	public Set<Message> deliverMessages(UUID toUser) throws RemoteException{
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
