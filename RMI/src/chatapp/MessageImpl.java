package chatapp;

import java.io.Serializable;

public class MessageImpl implements Message, Serializable {
	/**
	 * version number, because Message is Serializable
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * username of sender
	 */
	private String fromID;
	
	/**
	 * username of recipient
	 */
	private String toID;
	
	/**
	 * text of message
	 */
	private String msgStr;
	
	/**
	 * timestamp of message
	 */
	private Long msgTime;
	
	/**
	 * Creates a message
	 * 
	 * @param fromUser username of sender
	 * @param toUser   username of recipient
	 * @param msgText  text of message
	 * @param msgT     timestamp of message
	 */
	public MessageImpl(String fromUser, String toUser, String msgText, Long msgT) {
		fromID = fromUser;
		toID = toUser;
		msgStr = msgText;
		msgTime = msgT;
	}
	
	@Override
	public String fromUser() {
		return fromID;
	}
	
	@Override
	public String toUser() {
		return toID;
	}
	
	@Override
	public String msgText() {
		return msgStr;
	}
	
	@Override
	public Long msgTime() {
		return msgTime;
	}
}
