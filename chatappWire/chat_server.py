import socket
import select

from utils import *

all_sockets = []
accounts = set()
groups = {}
socket_username = {}
queued_messages = {}


def log(message):
    print message


def login(requester, username):
    if username not in accounts:
        return 'Account %s does not exist' % username
    socket_username[requester] = username
    response = 'Logged in as %s' % username
    if len(queued_messages[username]):
        response += '\n' + '\n'.join(queued_messages[username])
        queued_messages[username] = [] #  reset queue
    return response


def logout(requester):
    handle_disconnect(requester)


def create_account(requester, name):
    '''Create an account with the given name and login'''
    if not name in accounts:
        accounts.add(name)
        queued_messages[name] = []
        return 'Created account %s' % name
    return 'Account %s already exists' % name


def create_group(requester, name, *members):
    return_message = ''
    if not name in groups:
        groups[name] = []
        for member in members:
            if member in accounts:
                groups[name].append(member)
            else:
                return_message += 'Account %s does not exist\n' % member
        return return_message + 'Group %s created' % name
    else:
        return 'Group %s already exists' % name


def message(requester, user, message, from_queue=False):
    from_user = socket_username[requester]
    if not from_queue:
        message = '%s: %s' % (from_user, message)
    username_socket = {v: k for k, v in socket_username.items()}
    if user in username_socket:
        send(username_socket[user], message)
        return 'Message sent'
    elif user in queued_messages:
        queued_messages[user].append(message)
        return 'User %s is offline, message queued' % user
    else:
        return 'User %s does not exist' % user


def message_group(requester, group, to_send):
    if group in groups:
        for user in groups[group]:
            message(requester, user, to_send)
    else:
       return 'Group %s does not exist\n' % group


def _ranking(query, name):
    '''Get key to rank by first occurence of the query'''
    first_occurence = name.find(query)
    if first_occurence == -1:
        first_occurence = len(name)
    return first_occurence

def list_groups(query):
    group_names = sorted(groups.keys())
    return ', '.join(group_names)


def list_accounts(query):
    account_names = sorted(list(accounts))
    return ', '.join(account_names)


def delete_account(requester, to_delete):
    if to_delete in socket_username.values():
        return 'Account %s is currently logged in' % to_delete
    elif to_delete in accounts:
        accounts.remove(to_delete)
        queued_messages.pop(to_delete)
        return 'Account %s was deleted.' % to_delete
    else:
        return 'Account %s does not exist.' % to_delete


def send(socket, message):
    '''Send the given message to the given recipient'''
    if not len(message):
        log('Tried to send blank message')
    header = serialize_header(len(message))
    try:
        socket.send(header)
        socket.send(message)
    except socket.error:
        handle_disconnect(socket)


def handle_disconnect(socket):
    '''Logs out the user associated with the given socket'''
    all_sockets.remove(socket)
    disconnected_user = socket_username[socket]
    log(disconnected_user)
    socket_username.pop(socket)
    socket.close()


def get_command(command):
    if command in globals():
        return globals()[command]
    else:
        raise Exception('Command %s does not exist' % command)

commands = {c: get_command(c) for c in commands}

def parse_client_message(requester, message):
    '''Parse a request from a client and return a response'''
    command, args = parse_body(message)
    if command in commands:
        try:
            response = commands[command](requester, *args)
        except TypeError as e:
            response = str(e) 
    else:
        response = 'Command %s does not exist.' % command
    
    if response:
        send(requester, response)

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
                    data = s.recv(HEADER_SIZE)
                    if data:
                        version, payload_size = parse_header(data)
                        received_message = s.recv(payload_size)
                        if received_message:
                            parse_client_message(s, received_message)
                except socket.error:
                    handle_disconnect(s)

if __name__ == '__main__':
    main()