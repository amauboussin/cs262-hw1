package chatapp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;
import java.util.UUID;

/**
 *  An interface for chat application server that can create
 *  and list and delete accounts, create and list groups,
 *  and send messages to be delivered on command.
*/ 
public interface ChatServer extends Remote {

	UUID createAccount()
			throws RemoteException;
	
	void deleteAccount(UUID userID)
			throws RemoteException;

	Set<UUID> listAccounts()
			throws RemoteException;
	
	UUID createGroup(Set<UUID> members)
			throws RemoteException;
	
	Set<UUID> listGroups()
			throws RemoteException;
	
	void sendMessage(Message msg)
			throws RemoteException;
	
	Set<Message> deliverMessages(UUID toUser)
			throws RemoteException;
	
	String hostname()
			throws RemoteException;
}
