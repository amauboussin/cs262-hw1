# cs262-hw1

## Usage 

### chatappWire (Custom wire protocol implementation)

Run the server using `python chat_server.py`
The server will run on port 5000 by default, this option can be changed by modifying the constant at the top of chat_server.py.

Run the client using `python chat_client.py hostname port username`
where hostname and port specify the connection settings and username specifies which account to login as. If no account with the given username exists, it will be automatically created. Once in the client, type help to see a list of commands. 

####Implmentation details
The protocol consisted of two packets per messages, both sent over the wire as
strings. First, a header packet that contains a version number and the size of the
payload packet. Next, a payload packet that contains a string denoting which command
is being requested followed by a delimited list of arguments. Each string is delimited
usings pipes, and we remove any pipe characters that the client typed before parsing
their message to prevent any sql­injection style attacks. Once the server receives a
message, it always sends a return back ­ either a confirmation or an error message
(which is printed on the client’s terminal).

Overview of files:
* utils.py​: This file contains functions that are used by both the server and the
client. This includes the list of commands and functionality to serialize/deserialize
messages.
* chat_client.py​: The mainfunction of the chat client initiates a server connection
using the Python socket package, checks whether client and server versions
match, and listen for input from the server and the keyboard (standard in). When
the user presses enter, the send_to_serverfunction serializes their message as
described . The send_to_serverfunction calls the serialize_requestfunction
in utils.py. The serialize_requestfunction splits the received string by spaces,
extracts the command from the string and checks its validity. Finally, the
message is reassembled using pipes to separate the command, recipient and
message body, and sent to the server.
* chat_server.py​: The chat server utilizes the Python socket package to establish
a socket connection. Its mainfunction starts the server and listens for messages
from clients. When the server receives a message it invokes the
parse_client_messagefunction, which checks the validity of the command,
invokes the appropriate function, and then returns the response back to the
recipient client.

For more details, see the docstrings or HTML files in the chatappWire directory. 

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
