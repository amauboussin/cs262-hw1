# cs262-hw1
This chat application uses Java RMI to allow clients to create and send/receive
messages among personal and group accounts. 

To run:
(Must be compiled with the option -Djava.security.policy=server.policy)
First, a server must be started, see TestServer.java for sample code.
Then, clients on the same network can connect using that server's IP address to:
- create (or delete) an account (or group)
- search for users and groups with regular expressions
- log in (or out - messages received while logged out are stored for retrieval)
- send and receive messages.
For examples of these behaviors, see TestClient.java and TestClient2.java.

Contains:
The interfaces ChatServer, ChatClient, and Message, and their implementations.
ChatServer is remote and maintains list of personal and group accounts,
a list of logged-in users, and queued messages for logged out users.
ChatClient is also remote and allows clients to call server methods, while
being called by the server to immediately deliver messages.
Message is serializable and contains the message text as well as the sender
and recipient ID and the time at which it was sent.

More detailed documentation is available in the project report.
