package chatapp;

import java.io.Serializable;
import java.util.UUID;

public class MessageImpl implements Message, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private UUID fromID;
	private UUID toID;
	private String msgStr;
	private Long msgTime;
	
	public MessageImpl(UUID fromUser, UUID toUser, String msgText, Long msgT) {
		fromID = fromUser;
		toID = toUser;
		msgStr = msgText;
		msgTime = msgT;
	}
	
	@Override
	public UUID fromUser() {
		return fromID;
	}
	
	@Override
	public UUID toUser() {
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
