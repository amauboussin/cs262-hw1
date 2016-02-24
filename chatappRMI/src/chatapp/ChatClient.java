package chatapp;

import java.util.Set;
import java.util.UUID;

public interface ChatClient {
	
	void createAccount();
	void deleteMyAccount();
	void listAccounts();
	void createGroup(Set<UUID> members);
	void listGroups();
	void sendMessage(UUID toUser, String msgText);
	void readMessages();
}
