
VERSION = '1.00'
HEADER_SIZE = 16
MAX_CLIENTS = 10


help = {
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

commands = help.keys()

def get_help():
    s = 'Function: Arguments\n'
    for f, args in help.items():
        s += '%s: %s\n' % (f, args)
    return s

def parse_body(message):
    pieces = map(lambda s:s.strip(), message.split('|'))
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
    test_serialize_request()

def test_serialize_request():
    print serialize_request('message andrew hi') 
    print serialize_request('create_group my_group andrew alex eric emma dennis') 

if __name__ == '__main__':
    run_tests()
