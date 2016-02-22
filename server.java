// File Name server.java

import java.net.*;
import java.io.*;
import java.util.*;


public class server extends Thread
{
   
  
   Socket server;

   ServerSocket serverSocket;

   
   // constructor
   public server(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(10000);
   }


   /* start connection and listen
   Make account: make_account accountname
   List accounts: list_accounts
   Create group: create_group groupname
   List groups: list_groups
   Receive personal message: personal_message account message
   Receive group message: group_message group message
   Delete account: delete_account account
   */

   public void run()
   {
      while(true)
      {
         try
         {
            System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
            server = serverSocket.accept();
            System.out.println("Just connected to " + server.getRemoteSocketAddress());
            
            DataInputStream in = new DataInputStream(server.getInputStream());
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            
            // get string from client and split into array
            String message_string = in.readUTF();
            String[] message_array = message_string.split(" ");

                  
            // make account?
            if (message_array[0].equals("make_account")){
               if (message_array.length == 2) {
                  make_account(message_array[1]);
               }
               else out.writeUTF("Specify account name.");
            }

            // list accounts?
            if (message_array[0].equals("list_accounts")){
               if (message_array.length == 1) {
                  list_accounts();
               }
               else out.writeUTF("Unknown parameter.");
            }

            // logout
            if (message_string.equals("logout")){
               System.out.println("closing");
               server.close();
            }

            else out.writeUTF("Unknown command.");

         }
         catch
         (SocketTimeoutException s)
         {
            System.out.println("Socket timed out!");
            break;
         }
         catch(IOException e)
         {
            e.printStackTrace();
            break;
         }
      }
   }

   
   // make account
   public void make_account(String arg2) throws IOException{

      // load account list
      String new_account = arg2;
      String account = "";
      Scanner infile = new Scanner(new File("accounts.txt")).useDelimiter(",");
      List<String> account_list_temp = new ArrayList<String>();
      
      while (infile.hasNext())
      {
         account = infile.next();
         account_list_temp.add(account);
      }
      infile.close();

      String[] account_list = account_list_temp.toArray(new String[0]);

      DataOutputStream out = new DataOutputStream(server.getOutputStream());

      // check if account already exists and if not add it to the file
      if (Arrays.asList(account_list).contains(new_account)) out.writeUTF("Account already exists.");
      else {
         try {
            account_list_temp.add(new_account);
            FileWriter fw = new FileWriter("accounts.txt", true);
            fw.write("," + new_account);
            fw.close();
            System.out.println("New account created.");
         }
         catch(IOException ioe) {
            System.err.println("IOException: " + ioe.getMessage());
         }
         out.writeUTF("Account created.");
      }

   }




   // list accounts
   public void list_accounts() throws IOException{

      // load account list
      String account = "";
      Scanner infile = new Scanner(new File("accounts.txt")).useDelimiter(",");
      List<String> account_list_temp = new ArrayList<String>();
      
      while (infile.hasNext())
      {
         account = infile.next();
         account_list_temp.add(account);
      }
      infile.close();

      String[] account_list = account_list_temp.toArray(new String[0]);

      DataOutputStream out = new DataOutputStream(server.getOutputStream());

      String t = "";

      for (String s : account_list)
      {
         System.out.println(s);
         t += s;
         t += "\n";
      }
      out.writeUTF(t);

  
   }


   // main method
   public static void main(String [] args)
   {
      int port = Integer.parseInt(args[0]);
      try
      {
         Thread t = new server(port);
         t.start();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}