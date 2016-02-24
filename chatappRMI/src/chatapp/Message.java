package chatapp;

import java.util.UUID;

public interface Message {
	UUID fromUser();
	UUID toUser();
	String msgText();
	Long msgTime();
}
