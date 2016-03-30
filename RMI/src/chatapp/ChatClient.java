package chatapp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * An interface for clients that send {@link Message}s to each other via
 * remote calls to a {@link ChatServer}.
 * 
 * The client holds as private fields:
 * <ul>
 * <li> the IP address of the server serverHost (a string), 
 * <li> its own username myID (a string), 
 * <li> and the connected server server (a ChatServer), 
 * to which it connects by calling the private method 
 * getServer on the server’s IP address upon instantiation, 
 * when serverHost and myID are also specified. 
 * </ul>
 * 
 * Each of the public methods then remotely calls the corresponding server 
 * method for the client’s username, displaying brief error messages on 
 * RemoteException. Account administration is handled by the methods:
 * <ul>
 * <li> createAccount and deleteMyAccount (corresponding methods called 
 * on the server with myID as the argument), 
 * <li> login and logout (calls corresponding methods on the server, 
 * with login first exporting the client and supported by getID which 
 * the server can remotely call to retrieve myID), and 
 * <li> createGroup, listGroups, and listAccounts (called on the server, 
 * with string arguments described above). 
 * </ul>
 *
 * Messages are sent with the sendMessage method (remotely calling the 
 * server’s method of the same name), and received either by a local 
 * call to readMessages, which remotely calls the server’s deliverMessages 
 * method for queued messages, or by the server’s remote call to the 
 * client’s deliverMessage method when logged in.
*/
public interface ChatClient extends Remote {
	
	/**
	 * Return client's username, remotely called by {@link ChatServer}
	 * 
	 * @return username of this client
	 */
	String getID()
			throws RemoteException;
	
	/**
	 * Add client's username to server's list of accounts
	 * with a remote call to the {@link ChatServer#createAccount}
	 * method. Prints message on success or failure.
	 */
	void createAccount()
			throws RemoteException;
	
	/**
	 * Remove client's username from server's list of accounts
	 * with a remote call to the {@link ChatServer#deleteAccount}
	 * method. Prints message on success or failure.
	 */
	void deleteMyAccount()
			throws RemoteException;

	/**
	 * Exports client stub and adds it to server's table of 
	 * logged-in user stubs with a remote call to the
	 * {@link ChatServer#login} method.
	 */
        void login() 
    		throws RemoteException;
	/**
	 * Removes client stub from server's table of 
	 * logged-in user stubs with a remote call to the
	 * {@link ChatServer#logout} method, results in
	 * messages being queued until read request
	 * rather than immediately pushed to client stub.
	 */
        void logout() 
    		throws RemoteException;	

        /** Search for existing usernames matching a pattern
         * with a remote call to the {@link ChatServer#getAccounts} 
         * method. Matching names are printed.
         * 
         * Matching is performed on the server rather than the client
         * to preserve bandwidth.
         * 
         * @param regexp the regular expression used filter usernames
         */
	void listAccounts(String regexp)
		throws RemoteException;
	
	/**
	 * Make a group account with a remote call to the
	 * {@link ChatServer#createGroup} method.
	 * 
	 * @param members the set of group members' usernames
	 * @param groupID the name of the group
	 */
        void createGroup(Set<String> members, String groupID)
			throws RemoteException;
	
	/** Search for existing groups matching a pattern
         * with a remote call to {@link ChatServer#getGroups} 
         * method. Matching group names are printed.
         * 
         * Matching is performed on the server rather than the client
         * to preserve bandwidth.
         * 
         * @param regexp the regular expression used filter usernames
         */
	void listGroups(String regexp)
			throws RemoteException;
	
	/**
	 * Creates a {@link Message} object and sends it with a remote 
	 * call to the {@link ChatServer#sendMessage} method. Prints 
	 * warning if not logged in (so that user can log in for 
	 * immediate message delivery), prints message indicating 
	 * success or failure.
	 * 
	 * @param toUser  recipient's username (can be group name)
	 * @param msgText text of message
	 */
	void sendMessage(String toUser, String msgText)
			throws RemoteException;
	
	/**
	 * Displays messaged queued for this user while logged off,
	 * using a remote call to the {@link ChatServer#deliverMessages}
	 * method.
	 */
	void readMessages()
			throws RemoteException;
	
	/**
	 * Remotely called by the server when a message is sent
	 * to this logged-in client, this method displays the
	 * text, timestamp, and sender of a message.
	 * 
	 * @param msg the {@link Message} object being received
	 */
	void deliverMessage(Message msg)
			throws RemoteException;
}
