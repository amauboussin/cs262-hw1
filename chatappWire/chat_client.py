import socket
import select
import sys
import string

if __name__ == "__main__":
    
    if (len(sys.argv) < 4):
        print 'Incorrect syntax: python chat_client.py hostname port username'
        sys.exit()

    host = sys.argv[1]
    port = int(sys.argv[2])
    username = sys.argv[3]

    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    sock.settimeout(5)

    try:
        sock.connect((host, port))
    except:
        print 'Failed to connect'
        sys.exit()

    

