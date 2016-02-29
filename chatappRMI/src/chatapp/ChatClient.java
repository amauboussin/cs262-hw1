package chatapp;

import java.util.Set;

public interface ChatClient {
	
	void createAccount();
	void deleteMyAccount();
	void listAccounts(String regexp);
	void createGroup(Set<String> members, String groupID);
	void listGroups(String regexp);
	void sendMessage(String toUser, String msgText);
	void readMessages();
}
