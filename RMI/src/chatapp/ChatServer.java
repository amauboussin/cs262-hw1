package chatapp;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Set;

/**
 *  An interface for chat application server that can create
 *  and list and delete accounts, create and list groups,
 *  and send messages to be delivered on command.
 * <p>
 * The server does most of the computation, and keeps track of
 * usernames, groups, undelivered messages, and logged in clients 
 * with:
 * <ul>
 * <li> the private fields userList (a hashset of strings), 
 * <li> groupTable (a hashtable keyed on group name strings and 
 * containing sets of member name strings), 
 * <li> msgQueues (a hashtable keyed on recipient name strings 
 * and containing sets of messages), and 
 * <li> loggedInClients (a hashtable keyed on username strings and 
 * containing their ChatClient stub). 
 * </ul>
 * It also holds its registry 
 * stub in private fields and exports itself with the private 
 * method exportServer. 
 * <p>
 * The server has public methods createAccount and deleteAccount
 * that add and remove usernames from the userList (deleting an 
 * account also removes undelivered messages), login and logout 
 * that add and remove clients from loggedInClients, createGroup 
 * that takes in a group name and members’ names to add the 
 * groupTable, and getAccounts and getGroups that both list users 
 * or groups and members with support for regular expressions. 
 * The regular expression matching is performed on the server 
 * rather than the client on the assumption that bandwidth is more 
 * valuable than server compute time. There is also a public method 
 * hostname that returns the server’s hostname as a string to 
 * facilitate connecting.
 * <p>
 * The chatting itself is handled by a pair of methods, sendMessage 
 * and deliverMessages. The sendMessage method takes in a single 
 * Message object and, if the recipient has an account and is logged 
 * in, remotely calls the client to deliver the message. If the 
 * recipient name is a group name rather than a user name, it attempts 
 * to deliver the message to each member in turn. Logged-out recipients 
 * have the message added to their messages in msgQueues. These queued 
 * messages are delivered by a client’s remote call to deliverMessages. 
 * If the server encounters a RemoteException, the corresponding client 
 * assumed to have gone offline, so they are logged out and their 
 * messages retained. In case the client or network is not offline but 
 * merely experienced a temporary error, logged-off users are warned 
 * when attempting to send a message, so that they can call login again 
 * to resume immediate message delivery.
 */ 
public interface ChatServer extends Remote {

    /**
     * Inserts the new username into the server's table of accounts
     * 
     * @param loginID the username to be created
     * @return true if successful, false if ID already in use
     */
    Boolean createAccount(String loginID)
	throws RemoteException;
	
    /**
     * Deletes a username from the server's table of accounts
     * 
     * @param userID the username to be deleted
     */
    void deleteAccount(String userID)
	throws RemoteException;

    /**
     * Adds the user's stub to the server's table of logged-on users,
     * so that messages are delivered immediately rather than 
     * queued for a read request.
     * 
     * @param client the client's stub
     */
    void login(ChatClient client) 
    		throws RemoteException;

    /**
     * Removes the user's stub to the server's table of logged-on users,
     * so that messages are queued for a read request rather than
     * immediately pushed to the client.
     * 
     * @param clientID the client's username
     */
    void logout(String clientID) 
    		throws RemoteException;	

    /**
     * Search for existing usernames matching a pattern.
     * 
     * Matching is performed on the server rather than the client
     * to preserve bandwidth.
     * 
     * @param regexp the regular expression used filter usernames
     * @return the set of existing usernames matching regexp
     */	
    Set<String> getAccounts(String regexp)
	throws RemoteException;
	
    /**
     * Adds a group to the server's groupTable, after which
     * a message can be sent to all group members by sending
     * it to the group name.
     *  
     * @param members a set of usernames for the users in the groups
     * @param groupID the name of the group
     */
    void createGroup(Set<String> members, String groupID)
	throws RemoteException;
	
    /**
     * Search for existing group names matching a pattern.
     * 
     * Matching is performed on the server rather than the client
     * to preserve bandwidth.
     * 
     * @param regexp the regular expression used filter usernames
     * @return the set of existing groups whose names match regexp
     */		
    Hashtable<String, Set<String>> getGroups(String regexp)
	throws RemoteException;
    
    /**
     * This method is remotely called by a client to send a message
     * and performs the bulk of the server's functionality.
     * 
     * If the sender does not have an account, they are asked to 
     * create one and their message is not sent. If the sender is
     * not logged in, their message is sent but they are given a 
     * warning -- this is because on RemoteException, a user is 
     * logged off, so a warning ensures that they are aware and can 
     * log in to resume immediate message delivery if desired.
     * 
     * If the recipient does not exist, an error message is printed. 
     * If the recipient exists and is logged in, the server immediately 
     * pushes the message, otherwise queueing it for future delivery
     * in msgQueues. If the recipient is a group, the above is performed
     * for each member.
     * 
     * @param msg a {@link Message} object to be sent
     */	
    String sendMessage(Message msg)
	throws RemoteException;

    /**
     * Get the set of messages sent to a username while it was logged off.
     * 
     * Called by client, after delivery these messages are removed from
     * the server's msgQueue.
     * 
     * @param toUser the username of the recipient of queued messages
     * @return the set of messags that were sent to toUser while logged off
     */	
    Set<Message> deliverMessages(String toUser)
	throws RemoteException;
    
    /**
     * Get the server's IP address so that clients can connect to it
     * 
     * @return the IP address of the server as a string
     */	
    String hostname()
	throws RemoteException;
}
