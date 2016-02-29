

accounts = set{}
socket_username = {}

def login(socket, username):
    socket_username[str(socket)] = username


def create_account(socket, name):
    '''Create an account with the given name and login'''
    if not name in accounts:
        accounts.add(name)
        login(socket, name)
        return True
    return False


def send(socket, message):
    '''Send the given message to the given recipient'''
    socket.send(message)
    # try :
    #     socket.send(message)
    # except :
    #     socket.close()
    #     CONNECTION_LIST.remove(socket)


def main():
    pass

if __name__ == '__main__':
    main()