import socket
import select

from constants import *
from utils import *

all_sockets = []
accounts = set()
groups = {}
socket_username = {}
queued_messages = {}

commands = {
    'create_account': create_account,
    'create_group': create_group,
    'message_user': message_user,
    'message_group': message_group,
    'list_groups': list_groups,
    'list_accounts': list_accounts,
    'delete_account': delete_account,
}

def log(message):
    print message

def login(socket, username):
    socket_username[str(socket)] = username
    if len(queued_messages[username]):
        for message in queued_messages[username]:
            message_user(username, message)


def create_account(socket, name):
    '''Create an account with the given name and login'''
    if not name in accounts:
        accounts.add(name)
        login(socket, name)
        return True
    return False


def create_group(name, members):
    if not name in groups:
        groups[name] = members

def message_user(user, message):
    username_socket = {v: k for k, v in socket_username.items()}
    if user in username_socket:
        send(username_socket[user], message)
    elif user in queued_messages:
        queued_messages.append(message)
    else:
        pass


def message_group(group, message):
    if group in groups:
        for user in groups[group]:
            message_user(user, message)
    else:
        pass


def get_ranking(query, name):
    '''Get key to rank by first occurence of the query'''
    first_occurence = name.find(query)
    if first_occurence == -1:
        first_occurence = len(name)
    return first_occurence

def list_groups(query, recipient_socket):
    group_names = sorted(groups.keys(), key=ranking)
    send(recipient, ', '.join(group_names))


def list_accounts(query, recipient):
    account_names = sorted(list(accounts), key=ranking)
    send(recipient, ', '.join(account_names))


def delete_account(to_delete, recipient):
    if to_delete in accounts:
        accounts.remove(to_delete)
        queued_messages.pop(to_delete)
        send(recipient, 'Account %s was deleted.')
    else:
        send(recipient, 'Account %s does not exist.')


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


def main():
    
    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind(('0.0.0.0', PORT))
    server_socket.listen(MAX_CLIENTS)
 
    all_sockets.append(server_socket)

    log('Server started on port %s' % PORT)

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
                    version, payload_size = parse_header(sock.recv(HEADER_SIZE))
                    received_message = s.recv(payload_size)
                    if received_message:
                        parse_message(s, received_message)
                except socket.error:
                    handle_disconnect(s)

if __name__ == '__main__':
    main()