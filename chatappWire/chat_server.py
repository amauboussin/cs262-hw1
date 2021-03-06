'''chat_server.py implements a server that waits for connections from chat_client.py sessions, then accepts and processes requests from connected clients.'''

# package that allows implementation of TCP/IP sockets
import socket
# implementation of the Unix select() system call; used to get a list of sockets sending data
import select
# module providing regular expression; used for message formatting
import re

# Wire protocol encoding and parsing
from utils import *

PORT = 5000

#  List of all open sockets
all_sockets = []

#  Set of all account names
accounts = set()

#  {group name: [group members]}
groups = {}

#  {socket: username}
socket_username = {}

#  username: queued messages
queued_messages = {}


def log(message):
    """
    Log a message. Currently uses stdout but could use a file or other logging system.

    Args:
        message (string): message to log.
    """
    print message


def login(requester, username):
    """
    Login with the given username and send any messages queued while the user was offline.  Notifies of failure if username doesn't exist.

    Args:
        requester(socket.socket): Socket of user to be logged in
        username(str): Username of account to log in on
    Returns:
        String describing the result of the login attempt
    """
    if username not in accounts:
        return 'Account %s does not exist' % username
    socket_username[requester] = username
    response = 'Logged in as %s' % username
    if len(queued_messages[username]):
        response += '\n' + '\n'.join(queued_messages[username])
        queued_messages[username] = []  # reset queue
    return response


def logout(requester):
    """
    Logout the user on the given socket.

    Args:
        requester(socket.socket): Socket of user to be logged out
    """
    handle_disconnect(requester)


def create_account(requester, name):
    """
    Create an account with the given name and log them in on the requester socket.

    Args:
        requester(socket.socket): Socket to be associated with the new account
        name(str): Username of the new account
    Returns:
        String confirming the account creation or describing its failure
    """
    if not name in accounts:
        accounts.add(name)
        queued_messages[name] = []
        return 'Created account %s' % name
    return 'Account %s already exists' % name


def create_group(requester, name, *members):
    """
    Create a group of given name.  Once group is created, iterate through list of given members and add each to the newly created group.  

    Args:
        requester(socket.socket): Socket of user
        name (string): group name
        *members(array): list of usernames to add to group
    Returns:
        String confirming the group creation or describing its failure
    """
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
    """
    Send a message to a user if they are online, otherwise adds it to the queue.

    Args:
        requester(socket.socket): Read socket of the account sending the message
        user(str): Account to send message to
        message(str): Message to send
        from_queue (bool): Whether the message has been previously queued
            (if it is from the queue it has already been formatted)
    Returns:
        String describing the status of the message
    """
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
    """
    If the given group exists, iterate through its members and send each given message "to_send".

    Args:
        requester (socket.socket): Read socket of the accounts sending the given message to the given group
        group (string): Group to send the message to
        to_send (string): Message to send to each member of the group
    Returns:
        If the group doesn't exist, returns string notifying of send failure.
    """
    if group in groups:
        for user in groups[group]:
            message(requester, user, to_send)
    else:
        return 'Group %s does not exist\n' % group


def _filter_names(names, query):
    """
    Filter a list of names by the given wildcard query.

    Args:
        names (str list): list of names to be filtered
        query(str): string that each name must contain
    Returns:
        A list of strings that match the wildcard query.
    """
    check_match = lambda n: re.match(query.replace('*', '.*'), n)
    return sorted(filter(check_match, names))


def list_groups(requester, query='*'):
    """
    List all groups that match the given query.

    Args:
        requester(socket.socket): Socket to send confirmation message to
        query(str): Optional, string that group names must contain
    Returns:
        A string listing all groups that matched query.
    """
    group_names = _filter_names(groups.keys(), query)
    return ', '.join(group_names)


def list_accounts(requester, query='*'):
    """List all accounts that match the given query.

    Args:
        requester(socket.socket): Socket to send confirmation message to
        query(str): Optional, string that account names must contain
    Returns:
        A string listing all accounts that matched query.
    """
    account_names = _filter_names(list(accounts), query)
    return ', '.join(account_names)


def delete_account(requester, to_delete):
    """
    Delete a given accounts if it is not logged in.  Fails if the given accounts is logged in or if given accounts doesn't exist.

    Args:
        requester(socket.socket): Socket to send confirmation message to
        to_delete(str): Username of account to be deleted
    Returns:
        A string specifying status of deletion, ie whether account was successfully deleted, is logged in, or doesn't exist.
    """
    if to_delete in socket_username.values():
        return 'Account %s is currently logged in' % to_delete
    elif to_delete in accounts:
        accounts.remove(to_delete)
        queued_messages.pop(to_delete)
        return 'Account %s was deleted.' % to_delete
    else:
        return 'Account %s does not exist.' % to_delete


def send(socket, message):
    """
    Send a given message over a socket (complete with a header).  Checks if message is valid.  If the given socket is closed, disconnects that user instead.

    Args:
        socket(socket.socket): socket to send the message over
        message (str): message to be sent
    """

    if not len(message):
        log('Tried to send blank message')
    header = serialize_header(len(message))
    try:
        socket.send(header)
        socket.send(message)
    except socket.error:
        handle_disconnect(socket)


def handle_disconnect(socket):
    """
    Clean up data associated with the user on given socket after their disconnect.

    Args:
        socket (socket.socket): socket to clean up corresponding to disconnected user 
    """
    all_sockets.remove(socket)
    disconnected_user = socket_username[socket]
    log(disconnected_user)
    socket_username.pop(socket)
    socket.close()


def get_command(command):
    """
    Try to load the given command from the global namespace

    Args:
        command(str): Name of the command to try to load
    Returns:
        command (function): Function definition of the command
    Raises:
        Custom exception if a command is not defined in the global namespace
    """
    if command in globals():
        return globals()[command]
    else:
        raise Exception('Command %s does not exist' % command)

#  load the function definition for each possible command
commands = {c: get_command(c) for c in commands}


def parse_client_message(requester, message):
    """
    Parse a request from a client.  Extract the command requested and attached arguments.  
    If command exists, execute it with extracted arguments and forward command return value as response to requester.
    If command doesn't exist or errors, respond with information on failure.

    Args:
        requester (socket.socket): socket of the message sender
        message (str): message contents

    No return value. Sends a message to the client confirming
    the command was executed if successful, otherwise logs failure.
    """
    command, args = parse_body(message)
    if command in commands:
        try:
            #  each command takes the requester as the first argument,
            #  followed by any additional arguments
            response = commands[command](requester, *args)
        except TypeError as e:
            response = str(e)
    else:
        response = 'Command %s does not exist.' % command

    if response:
        send(requester, response)


def main():
    """
    Instantiate the server and begin actively listening for clients.
    Utilize the select function to block on activity.
    When data is received, if it is a new socket, initialize new connection.  If it is a message from a connected client, received header first.
    If the versions match, receive the body of the message and pass it off to message parser to deserialize and process message request.
    """

    server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    server_socket.bind(('0.0.0.0', PORT))
    server_socket.listen(MAX_CLIENTS)

    all_sockets.append(server_socket)

    log('Server started on port %s' % PORT)

    while True:
        # Get a list of sockets that send data. does not work on winodws
        read_sockets, _, _ = select.select(all_sockets, [], [])

        #  check for data on the server socket and each of the client sockets
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
