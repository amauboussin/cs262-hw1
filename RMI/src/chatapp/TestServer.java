package chatapp;

import java.rmi.RemoteException;

/**
 * test class for ChatServer
 * <p>
 * This class has a main method that starts a ChatServer on the
 * specified port and prints it hostname and port if successful.
 */
public class TestServer {
    /**
     * hardcoded port number the server will attempt to use
     */
    private static int port = 1358;
   
    /**
     * Attempt to start a ChatServer on the specified port.
     * On success prints hostname and port, on failure
     * throws RemoteException.
     * 
     * @param args ignored
     */
    public static void main(String[] args) {
        try{
            ChatServer myserver = new ChatServerImpl(port);
            System.out.printf("hostname: %s, port: %d",myserver.hostname(), port);
            System.out.println();
        } catch (RemoteException e) {
            System.out.println("Could not start server.");
            e.printStackTrace();
        }
    }   
}
