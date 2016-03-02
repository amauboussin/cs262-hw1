package chatapp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Set;

/**
 *  An interface for chat application server that can create
 *  and list and delete accounts, create and list groups,
 *  and send messages to be delivered on command.
*/ 
public interface ChatServer extends Remote {

	Boolean createAccount(String loginID)
			throws RemoteException;
	
	void deleteAccount(String userID)
			throws RemoteException;

    void login(ChatClient client) 
    		throws RemoteException;

    void logout(String clientID) 
    		throws RemoteException;	
	
	Set<String> getAccounts(String regexp)
			throws RemoteException;
	
	void createGroup(Set<String> members, String groupID)
			throws RemoteException;
	
	Hashtable<String, Set<String>> getGroups(String regexp)
			throws RemoteException;
	
	String sendMessage(Message msg)
			throws RemoteException;
	
	Set<Message> deliverMessages(String toUser)
			throws RemoteException;
	
	String hostname()
			throws RemoteException;
}
