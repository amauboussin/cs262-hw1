'''utils.py implements important functionality shared between the chat_server and chat_client, namely that having to do with the custom wire protocol, serialization, and message processing.'''

VERSION = '1.00'
HEADER_SIZE = 16
MAX_CLIENTS = 10

#  list of commands and their arguments
command_args = {
    'login': 'username',
    'logout': 'No Arguments',
    'create_account': 'username',
    'create_group': 'group_name',
    'message': 'username message',
    'message_group': 'group_name message',
    'list_groups': 'query(optional)',
    'list_accounts': 'query(optional)',
    'delete_account': 'username',
}

commands = command_args.keys()


def get_help():
    """
    Return a string with a list of all available commands and their arguments
    """
    s = 'Function: Arguments\n'
    for f, args in command_args.items():
        s += '%s: %s\n' % (f, args)
    return s


def parse_body(message):
    """
    Takes a given message string from the body of a request and deserializes it by parsing the custom wire protocol and extracting the message components, namely the function being requested and its arguments.
    See serializer for information on wire protocol serialization format parsed here.

    Args:
        message (string): the serialized message body to parse
    Returns:
        The deserialized command and arguments extracted from the message.
    """
    pieces = map(lambda s: s.strip(), message.split('|'))
    command = pieces[0].strip()
    args = pieces[1:] if len(pieces) > 1 else ()
    return command, args


def parse_header(header):
    """
    Return the wire protocol version and payload size given a header string.

    Args:
        header (string): the message header to parse
    Returns:
        The version number and size of the payload to follow extracted from the message header.
    """
    version, payload_size = header.split('|')
    payload_size = int(float(payload_size))
    return version, payload_size


def serialize_header(payload_size):
    """
    Create the header for a request in the format of the custom wire protocol.  The protocol for the header separates header components with pipes "|" to parse between in deserialization.

    Args:
        payload_size (int): Payload size of the request in characters
    Returns:
        header (str): Header with current version and payload_size to be sent to server
    """
    header = '%s|%d.' % (VERSION, payload_size)
    padding_length = HEADER_SIZE - len(header)
    return header + '0' * padding_length


def serialize_request(command):
    """
    Create a request from a string command using the custom wire protocol.  
    First decompose the input string into the command and arguments, and then compose message body by separating components on the wire with pipes "|" for deserializer to parse between.
    To this end, pipes are not permitted in input strings and will be rejected. 

    Args:
        command (str): A space-delimitedstring with the name of the function
            being called followed by the arguments (e.g. "message andrew hi")
    Returns:
        header (str): Header of the request to be sent to the server
        body (str): Body of the request to be sent to the server

    If the given command is invalid (not a function or wrong number of arguments,
    return (None, None)
    """

    if command.find('|') != -1:
        print 'Illegal character |'
        return None, None

    pieces = command.split(' ', 1)
    comm_string = pieces[0].strip()
    args = pieces[1] if len(pieces) > 1 else None

    if comm_string not in commands:
        print 'Invalid command %s. Type "help" to see options.' % comm_string
        return None, None

    if comm_string in ('message', 'message_group'):
        if not ' ' in args:
            print 'Not enough arguments'
            return None, None

        recipient, message = args.split(' ', 1)

        body = comm_string + '|' + recipient + '|' + message
        header = serialize_header(len(body))
        return header, body

    body = comm_string
    if args is not None:
        args = args.split(' ')
        for a in args:
            body += '|' + a

    header = serialize_header(len(body))
    return header, body


def run_tests():
    """Run all utility function tests"""
    test_serialize_request()


def test_serialize_request():
    """Print the result of serializing a couple messages to stdout"""
    print serialize_request('message andrew hi')
    print serialize_request('create_group my_group andrew alex eric emma dennis')

if __name__ == '__main__':
    run_tests()
