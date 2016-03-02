package chatapp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface ChatClient extends Remote {
	String getID()
			throws RemoteException;
	
	void createAccount()
			throws RemoteException;
	
	void deleteMyAccount()
			throws RemoteException;

    void login() 
    		throws RemoteException;

    void logout() 
    		throws RemoteException;	
    
	void listAccounts(String regexp)
			throws RemoteException;
	
	void createGroup(Set<String> members, String groupID)
			throws RemoteException;
	
	void listGroups(String regexp)
			throws RemoteException;
	
	void sendMessage(String toUser, String msgText)
			throws RemoteException;
	
	void readMessages()
			throws RemoteException;
	
	void deliverMessage(Message msg)
			throws RemoteException;
}
