package chatapp;

import java.util.Set;

public interface ChatClient {
	
	void createAccount(String loginID);
	void deleteMyAccount();
	void listAccounts();
	void createGroup(Set<String> members, String groupID);
	void listGroups();
	void sendMessage(String toUser, String msgText);
	void readMessages();
}
