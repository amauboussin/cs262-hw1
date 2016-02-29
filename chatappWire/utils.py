

def parse_body(from_socket, message):
    pieces = from_socket.split('|')
    command = pieces[0]
    args = pieces[1:] if len(pieces) > 1 else None
    return command, args

def parse_header(header):
    version, payload_size = header.split('|')
    payload_size = int(payload_size)
    return version, payload_size

def serialize(command, version):
    pieces = command.split(' ', 1)
    comm_string = pieces[0]
    args = pieces[2] if len(pieces) > 1 else None  
    
    header = version + '|' + len(command)

    if comm_string not in commands:
        print 'Invalid command' 
        return None, None
    
    if comm_string == ('message_user' or 'message_group'):
        recipient, message = args.split(' ', 1);

        if (message.find('|') != -1):
            print 'Illegal message character |.'
            return None, None

        body = comm_string + '|' + recipient + '|' + message
        return header, body

    body = comm_string
    if (args != None):
        args = args.split('|')
        for a in args:
            body += '|' + a
    return header, body

        
