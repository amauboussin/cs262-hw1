'''chat_client.py implements functionality allowing users to connect to a server, login, and send it commands.'''

# implements cleanup functions; used to logout from server upon client script termination
import atexit
# package that allows implementation of TCP/IP sockets
import socket
# implementation of the Unix select() system call; used to get a list of sockets sending data
import select
# implements command line arguments; used to start client with parameters
import sys

# Wire protocol encoding and parsing
from utils import *


def send_to_server(client_socket, command):
    """
    Send the given message to the given recipient.  Serialization breaks the command message into two parts.
    The first part is a header specifying only the version number of the protocol used to confirm compatability, as well as the size of the rest of the message.
    The body of the request follows of size specified in the header so that the server knows how much to expect.
    See utils module docs for information on wire protocol serialization.

    Args:
        client_socket (socket.socket): socket of the message receiver
        command (str): message contents

    No return value. Prints error message if can not connect to server.
    """
    header, request = serialize_request(command)

    if header and request:
        try:
            client_socket.send(header)
            client_socket.send(request)
        except socket.error:
            print 'Server disconnected'


def prompt():
    """
    Prints out ">>" to make the prompt look nice.
    """
    sys.stdout.write('>> ')
    sys.stdout.flush()

if __name__ == "__main__":
    """
    Setup and listener for the client.  First establish a socket based on startup args, and then attempt to connect to server.
    Once connected, utilize select() function to block simultaneously on stdin and the socket.  If message is received from the server over the socket, print it.  If command is read from stdin, send it to the server over socket.
    
    Args (recv from command line):
        argv[1] (string): hostname of server
        argv[2] (int): port to connect over
        argv[3] (string): username to connect to server with
    """

    if (len(sys.argv) < 4):
        print 'Incorrect syntax: python chat_client.py hostname port username'
        sys.exit()

    host = sys.argv[1]
    port = int(sys.argv[2])
    username = sys.argv[3]

    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    client_socket.settimeout(5)

    try:
        client_socket.connect((host, port))
    except:
        print 'Failed to connect'
        sys.exit()

    send_to_server(client_socket, 'create_account %s' % username)
    send_to_server(client_socket, 'login %s' % username)
    print 'Welcome ' + username + '.  Type "help" for help.'

    #  send a logout message to the server if the program exits for any reason
    @atexit.register
    def logout():
        send_to_server(client_socket, 'logout')

    while True:

        sock_list = [sys.stdin, client_socket]

        read_sockets, _, _ = select.select(sock_list, [], [])
        for s in read_sockets:
            if s == client_socket:  # data from server, parse and display
                data = s.recv(HEADER_SIZE)
                if data:
                    server_version, payload_size = parse_header(data)
                    if server_version != VERSION:
                        raise Exception('Client version %s does not match server version %s' % (VERSION, server_version))
                    received_message = s.recv(payload_size)
                    print received_message
                    prompt()
                else:
                    print 'Server disconnected'
                    sys.exit()

            else:  # data from stdin, send to server if it is not a request for help
                message = sys.stdin.readline()
                if message.startswith('help'):
                    print get_help()
                else:
                    send_to_server(client_socket, message)
                prompt()
