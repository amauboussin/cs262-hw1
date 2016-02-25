package chatapp;

import java.io.Serializable;

public class MessageImpl implements Message, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String fromID;
	private String toID;
	private String msgStr;
	private Long msgTime;
	
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
