import socket
import select
import sys
import string
from utils import *


if __name__ == "__main__":
    
    if (len(sys.argv) < 4):
        print 'Incorrect syntax: python chat_client.py hostname port username'
        sys.exit()

    host = sys.argv[1]
    port = int(sys.argv[2])
    username = sys.argv[3]
    version = Ver1.0

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(5)

    try:
        sock.connect((host, port))
    except:
        print 'Failed to connect'
        sys.exit()

    print 'Welcome ' + username + '.  Type -h for help.'

    while True:
        print '>>'

        sock_list = [sys.stdin, sock]

        reads, _, _ = select.select(sock_list, [], [])
        
        for s in reads:
            if s == sock:
                header = sock.recv(16)
                ver, payload_size = parse_header(header)
                body = sock.recv(payload_size)
                if (ver != version):
                    print 'Bad version'
            
                

