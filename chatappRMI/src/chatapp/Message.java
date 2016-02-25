package chatapp;


public interface Message {
	String fromUser();
	String toUser();
	String msgText();
	Long msgTime();
}
