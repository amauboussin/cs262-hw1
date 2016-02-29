

def parse_message(from_socket, message):
    pass

def parse_header(header):
    version, payload_size = header.split('|')
    payload_size = int(payload_size)
    return version, payload_size
