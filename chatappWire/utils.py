
VERSION = '1.00'
PORT = 5000
HEADER_SIZE = 16
MAX_CLIENTS = 10


commands = [
    'login',
    'create_account',
    'create_group',
    'message_user',
    'message_group',
    'list_groups',
    'list_accounts',
    'delete_account',
]

help = {
    'login': 'username',
    'create_account': 'username',
    'create_group': 'group_name',
    'message_user': 'username message',
    'message_group': 'group_name message',
    'list_groups': 'query(optional)',
    'list_accounts': 'query(optional)',
    'delete_account': 'username',
}


def parse_body(message):
    pieces = message.split('|')
    command = pieces[0].strip()
    args = pieces[1:] if len(pieces) > 1 else ()
    return command, args

def parse_header(header):
    version, payload_size = header.split('|')
    payload_size = int(float(payload_size))
    return version, payload_size


def serialize_header(payload_size):
    header = '%s|%d.' % (VERSION, payload_size)
    padding_length = HEADER_SIZE - len(header)
    return header + '0' * padding_length

def serialize_request(command):
    pieces = command.split(' ', 1)
    comm_string = pieces[0].strip()
    args = pieces[1] if len(pieces) > 1 else None  
    
    if comm_string not in commands:
        print 'Invalid command %s' % comm_string
        return None, None
    
    if comm_string in ('message_user', 'message_group'):
        recipient, message = args.split(' ', 1);

        if (message.find('|') != -1):
            print 'Illegal message character |'
            return None, None

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
    test_serialize_request()

def test_serialize():
    print serialize_request('message_user andrew hi') 
    print serialize_request('create_group my_group andrew alex eric emma dennis') 

if __name__ == '__main__':
    run_tests()
