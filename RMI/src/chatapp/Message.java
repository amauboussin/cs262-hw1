package chatapp;

/** 
 * Messages are sent between {@link ChatClient}s by the
 * {@link ChatServer}.
 * <p>
 * Messages are simple, serializable objects. They contain private
 * fields for the sender and recipient’s usernames, the text of the 
 * message, and the time at which the message was sent, and methods
 * to return the value of these fields. Because this class 
 * serializable, it also has the requisite version number, so that 
 * if this class were changed beyond backwards compatibility, RMI 
 * would automatically detect version clash issues.
 */
public interface Message {
	/** 
	 * @return sender's username
	 */
	String fromUser();
	
	/** 
	 * @return recipient's username
	 */
	String toUser();
	
	/** 
	 * @return text of message
	 */
	String msgText();
	
	/** 
	 * @return timestamp of message
	 */
	Long msgTime();
}
