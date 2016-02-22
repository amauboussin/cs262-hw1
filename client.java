// File Name client.java

import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class client {
  
   static String serverName;
   static int port;

   public static String run_client(String serverName, int port) throws InterruptedException {
      try {
         // socket
         Socket client = new Socket(serverName, port);

         // output to server
         OutputStream outToServer = client.getOutputStream();
         DataOutputStream out = new DataOutputStream(outToServer);

         // input from server
         InputStream inFromServer = client.getInputStream();
         DataInputStream in = new DataInputStream(inFromServer);

         // get keyboard input
         Scanner keyboard = new Scanner(System.in);
         String input_string = keyboard.nextLine();
         
         // check for logout command
         if (input_string.equals("logout")) {
            out.writeUTF(input_string);     
            client.close();
            return input_string;
         }

         // send message to server
         else out.writeUTF(input_string);
         System.out.println(in.readUTF());
      }

      catch(IOException e) {
         e.printStackTrace();
      }
      
      // repeat
      TimeUnit.SECONDS.sleep(1);
      return run_client(serverName, port);
   }



   public static void main(String [] args) throws InterruptedException
   {
         serverName = args[0];
         port = Integer.parseInt(args[1]);

         System.out.println("Connected to " + serverName + " on port " + port);
         run_client(serverName, port);
      
   }
}