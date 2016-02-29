
PORT = 5000
RECV_BUFFER = 4096
MAX_CLIENTS = 10

all_sockets = []
accounts = set{}
socket_username = {}


def login(socket, username):
    socket_username[str(socket)] = username


def create_account(socket, name):
    '''Create an account with the given name and login'''
    if not name in accounts:
        accounts.add(name)
        login(socket, name)
        return True
    return False


def send(socket, message):
    '''Send the given message to the given recipient'''
    try:
        socket.send(message)
    except socket.error:
        handle_disconnect(socket)

def handle_disconnect(socket):
    '''Logs out the user associated with the given socket'''
    socket.close()
    all_sockets.remove(socket)
    socket_username.pop(socket)


def parse_message(from_socket, message):
    pass

def main():
    
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind(('0.0.0.0', PORT))
    server_socket.listen(MAX_CLIENTS)
 
    all_sockets.append(server_socket)

    while True:
        # Get a list of sockets that send data. does not work on winodws
        read_sockets, _, _ = select.select(all_sockets, [], [])
        for s in read_sockets:

            #  data from the server is a new connection
            if s == server_socket:
                new_connection, address = server_socket.accept()
                all_sockets.append(new_connection)
            #  data from another socket is a message
            else:
                try:
                    received_message = s.recv(RECV_BUFFER)
                    if received_message:
                        parse_message(s, received_message)
                except socket.error:
                    handle_disconnect(s)




if __name__ == '__main__':
    main()