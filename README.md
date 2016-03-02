# cs262-hw1

## Usage 

### chatappWire (Custom wire protocol implementation)

Run the server using `python chat_server.py`
The server will run on port 5000 by default, this option can be changed by modifying the constant at the top of chat_server.py.

Run the client using `python chat_client.py hostname port username`
where hostname and port specify the connection settings and username specifies which account to login as. If no account with the given username exists, it will be automatically created. Once in the client, type help to see a list of commands. 

### chatappRMI (RMI implementation)

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
