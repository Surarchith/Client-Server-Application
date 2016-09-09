import java.io.*;
import java.util.Scanner;
import java.net.*;

public class SocketClient
{
   Socket socket = null;
   static PrintWriter out = null;
   BufferedReader in = null;
   static String cmd="";
   static boolean opValue=true;
   
   public boolean communicate(String cmd)
   {
	   int no_of_users,no_of_msgs;
	   String user;
	   String enter_recipient,recipient_name,mesg,mesg_line;
		Scanner sc=new Scanner(System.in);
		out.println(cmd);
		out.flush();
      //Receive text from server
      try
      {
			String line = in.readLine();
			//System.out.println(line); // just shows the received cmd, can be removed
		
		if(line.equals("Exit received") || line.contains("Error:"))
			 return false;
		 switch(cmd)
		 {
			 case "1":
			 no_of_users=Integer.parseInt(line);
			 System.out.println("\nKnown users: ");
			 for(int i=0;i<no_of_users;i++)
			 {
				 user = in.readLine();
				 if(i<(no_of_users-1))
				 System.out.println((i+1)+". "+user);
			 }
			 break;
			 
			 case "2":
			 no_of_users=Integer.parseInt(line);
			 System.out.println("\nConnected users: ");
			 for(int i=0;i<no_of_users;i++)
			 {
				 user = in.readLine();
				 if(i<(no_of_users-1))
				 System.out.println((i+1)+". "+user);
			 }
			 break;
			 
			 case "3":
			 enter_recipient=(line);
			 System.out.println(enter_recipient);
			 recipient_name = sc.nextLine();
			 out.println(recipient_name); // sending the recipient name
			 mesg_line=in.readLine();
			 System.out.println(mesg_line);
			 mesg=sc.nextLine();
			 out.println(mesg); // sending the message
			 break;				 
			 
			 case "4":
			 case "5":
				 mesg_line=line;
				 System.out.println(mesg_line);
				 mesg=sc.nextLine();
				 out.println(mesg); // sending the message
			 break;
			 
			 case "6":
				 no_of_msgs=Integer.parseInt(line);
				 System.out.println("\nYour messages: ");
				 for(int i=0;i<no_of_msgs;i++)
				 {
					 mesg = in.readLine();
					 if(i<(no_of_msgs-1))
					 System.out.println((i+1)+". "+mesg);
				 }
			 break;
			 
			 default:
			 
			 break;
		 }
      } 
      catch (IOException e)
      {
         System.out.println("Read failed");
         System.exit(1);
      }
      return true;
   }
  
   public void listenSocket(String host, int port)
   {
      //Create socket connection
      try
      {
	 socket = new Socket(host, port);
	 out = new PrintWriter(socket.getOutputStream(), true);
	 in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      } 
      catch (UnknownHostException e) 
      {
	 System.out.println("Unknown host");
	 System.exit(1);
      } 
      catch (IOException e) 
      {
	 System.out.println("No I/O");
	 System.exit(1);
      }
   }

   public void addUser(String user)
   {
	   try
	   {
	   out.println(user);
	   String addedAck= in.readLine();
	      
		  if(addedAck.contains("Error: User already connected"))
		  {
			  opValue=false;
		  }
		  
		  System.out.println("\n"+addedAck); 
   		}catch(IOException e)
   		{
		System.out.println("No I/O");
		System.exit(1);
   		}
   }
   
   public static void main(String[] args)
   {
	   
	   Scanner sc = new Scanner(System.in);
	   opValue=true;
	   
      if (args.length != 2)
      {
         System.out.println("Usage:  client hostname port");
	 System.exit(1);
      }

      SocketClient client = new SocketClient();

      String host = args[0];
      int port = Integer.valueOf(args[1]);
      client.listenSocket(host, port);
      System.out.println("Enter user name");
      String username=sc.nextLine();
      
      client.addUser(username);
	 
	  while((!cmd.equals("7"))&& opValue)
	  {
	  System.out.println();
	  System.out.println("1. Display the names of all known users.");
      System.out.println("2. Display the names of all currently connected users.");
      System.out.println("3. Send a text message to a particular user.");
      System.out.println("4. Send a text message to all currently connected users.");
      System.out.println("5. Send a text message to all known users.");
      System.out.println("6. Get my messages.");
      System.out.println("7. Exit.");
      System.out.println("Enter your command: ");
      cmd = sc.nextLine();
	  cmd=cmd.trim();
      opValue=client.communicate(cmd);
	  }
	  
   }
}