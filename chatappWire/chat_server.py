

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


def broadcast_data(socket, message):
	'''Send the given message to the given recipient'''
    try :
        socket.send(message)
    except :
        # broken socket connection may be, chat client pressed ctrl+c for example
        socket.close()
        CONNECTION_LIST.remove(socket)