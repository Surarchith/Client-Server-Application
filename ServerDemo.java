import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;



class ServerThread implements Runnable
{

	   private Socket client;
	   
	   ServerThread(Socket client) 
	   {
	      this.client = client;
	   }
	   
	@Override
	   public void run()
	   {
	      String name;
	      BufferedReader in = null;
	      PrintWriter out = null;
		  int count=0;
	      try 
	      {
		 in = new BufferedReader(new InputStreamReader(client.getInputStream()));
		 out = new PrintWriter(client.getOutputStream(), true);
	      } 
	      catch (IOException e) 
	      {
	    	  System.out.println("in or out failed");
	    	  System.exit(-1);
	      }

	      try 
	      {
		 // Receive text from client
		 name = in.readLine();
		 
		 if(ServerDemo.addUser(name.trim()))
		 {
		 //Send response back to client saying user is not already connected and welcome him with a Hi
		 //String HiMsg = "Hi " + name;
		 //out.println(HiMsg);
		 //out.flush();
		 out.println("user added");
		 out.flush();
		 
		 while(true)
		 {
			 String option=in.readLine();
			 //System.out.println("option is "+option);
			 option=option.trim();
			 switch(option)
			 {
				 case "1":
					 System.out.println(name+" displays all known users.");
					 ArrayList<String> sd=ServerDemo.getUsers();//create an instance of Sckt Thread Server and store the values
					 String text="";
					 count=sd.size()+1;
					 out.println(count); //send size of the arraylist as the first argument for count
					 out.flush();
					 for(String user: sd)
					 {
						 text=text+user+"\n";
					 }
					 out.println(text);
					 
					 break;
				 
				 case "2":
					 System.out.println(name+" displays all connected users.");
					//create an instance of Sckt Thread Server and store the values
					 ArrayList<String> cnusers=ServerDemo.getConnectedUsers();
					 String text2="";
					 System.out.println("count is "+cnusers.size());
					 count=cnusers.size()+1;
					 out.println(count); //send size of the arraylist as the first argument for count
					 out.flush();
					 for(String user: cnusers)
					 {
						 text2=text2+user+"\n";
					 }
					 out.println(text2);
					 
					 break;
				 
				 case "3":
					//create an instance of Sckt Thread Server and store the values
					 out.println("Enter recipient's name: ");
					 String recipient= in.readLine(); 
					 out.println("Enter a message: ");
					 String msg= in.readLine();
					 System.out.println(name+" posts a message for "+recipient);
					 ServerDemo.postMessage(name, recipient, msg);
					 break;
				 
				 case "4":
					//create an instance of Sckt Thread Server and store the values
					 out.println("Enter a message: ");
					 String msgToConnUsers= in.readLine();
					 System.out.println(name+" posts a message to all currently connected users");
					 ServerDemo.postMessagetoConnectedUsers(name, msgToConnUsers);
				 break;
				 
				 case "5":
					//create an instance of Sckt Thread Server and store the values
					 out.println("Enter a message: ");
					 String msgToKnownUsers= in.readLine();
					 System.out.println(name+" posts a message to all known users");
					 ServerDemo.postMessagetoKnownUsers(name, msgToKnownUsers);
				 break;
				 
				 case "6":
					//create an instance of Sckt Thread Server and store the values
					 ArrayList<String> myMsgs=ServerDemo.displayAllMessages(name);
					 
					 String text6="";
					 count=myMsgs.size()+1;
					 out.println(count); //send size of the arraylist as the first argument for count
					 out.flush();
					 // out.println(myMsgs.size());   //send length as a precursor
					 for(String currMsg:myMsgs)
					 {
						 text6=text6+currMsg+"\n";
					 }
					 out.println(text6);
					 System.out.println(name+" gets messages");
				 break;
				 
				 case "7":
					//create an instance of Sckt Thread Server and store the values
					 System.out.println(name+" exits");
				 break;
				 
				 default:
				 System.out.println("Invalid option received");
				 break;
				 
			 }
			 if(option.equals("7"))
			 {
				 out.println("Exit received");
				 ServerDemo.removeConnectedUser(name);
				 break;
			 }
		   }
	       } 
		 
		 else
	      {
			 out.println("Error: User already connected, cannot have multiple connections");
	      }
		 
	     }catch (IOException e) 
	      {
		 System.out.println("Read failed");
		 System.exit(-1);
	      }

	      try 
	      {
		 client.close();
	      } 
	      catch (IOException e) 
	      {
		 System.out.println("Close failed");
		 System.exit(-1);
	      }
	   }
	
}

public class ServerDemo {

	ServerSocket server = null;
	static HashMap<String, ArrayList<String>> knownUsers= new HashMap<String,ArrayList<String>>();
	static HashMap<String,String> connectedUsers= new HashMap<String,String>();
	static  Semaphore mutexMsg=new Semaphore(1);
	   public static boolean addUser(String user)
	   {
		   boolean userNotConnected=true;
		   if (connectedUsers.containsKey(user))
		   {
			   userNotConnected=false;
			   return userNotConnected;
		   }
		   else
		   {
			   connectedUsers.put(user, "");
		   }
		   if(knownUsers.containsKey(user))
		   {
			 System.out.println("Connection by known user "+user);   
		   }
		   else
		   {
		    System.out.println("Connection by unknown user "+user); 
		    ArrayList<String> messages=new ArrayList<>();
		    knownUsers.put(user, messages);
		   }
		   return userNotConnected;
	   }
	   
	   public static ArrayList<String> getUsers()
	   {
		   ArrayList<String> knownUsersList=new ArrayList<>();
		   for(Entry<String, ArrayList<String>> entry : knownUsers.entrySet()) 
			{
				String key = entry.getKey(); // Get the index key
				//System.out.print(key + " => ");       // Display the index key
				knownUsersList.add(key);
				//String value = entry.getValue();   // Get the list of record addresses
				//System.out.print("[" + value);// Display the first address
				}
		   return knownUsersList;
	   }
	   
	   public static void removeConnectedUser(String user)
	   {
		   connectedUsers.remove(user);
	   }
	   
	   public static ArrayList<String> getConnectedUsers()
	   {
		   ArrayList<String> connUsersList=new ArrayList<>();
		   for(Entry<String, String> entry : connectedUsers.entrySet()) 
			{
				String key = entry.getKey(); // Get the index key
				//System.out.print(key + " => ");       // Display the index key
				connUsersList.add(key);
				//String value = entry.getValue();   // Get the list of record addresses
				//System.out.print("[" + value);// Display the first address
				}
		   return connUsersList;
	   }
	   
	   public static void postMessage(String user, String recipient, String newMsg)
	   {
		  
		   try {
			mutexMsg.acquire(); //bringing a semaphore for mutual exclusion
		
		   ArrayList<String> msgsOfUser;
		   if(knownUsers.containsKey(recipient))
		   {
			msgsOfUser=knownUsers.get(recipient);   
		   }
		   else
		   {
			msgsOfUser=new ArrayList<String>();
		   }
		    Date date=new Date();
			msgsOfUser.add("From "+user+",  "+date+",  "+newMsg);
			knownUsers.put(recipient, msgsOfUser);
			
			mutexMsg.release();
		   } catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }
	   
	   public static void postMessagetoConnectedUsers(String user, String msg)
	   {
		   for(Entry<String, String> entry : connectedUsers.entrySet()) 
			{
				String key = entry.getKey(); // Get the index key
				if(!key.equals(user))//if the value retrieved is not the same user, 
					postMessage(user,key,msg); // post the message to them as recipient
				}
	   }
	   
	   
	   public static void postMessagetoKnownUsers(String user, String msg)
	   {
		   for(Entry<String, ArrayList<String>> entry : knownUsers.entrySet()) 
			{
				String key = entry.getKey(); // Get the index key
				if(!key.equals(user))//if the value retrieved is not the same user, 
					postMessage(user,key,msg); // post the message to them as recipient
				}
	   }
	   
	   
	   public static ArrayList<String> displayAllMessages(String user)
	   {
		  ArrayList<String> msgsOfUser=knownUsers.get(user);
		  return msgsOfUser;
	   }
	   
	   public void listenSocket(int port)
	   {
	      try
	      {
		 server = new ServerSocket(port); 
		 System.out.println("Server running on port " + port + 
		                     "," + " use ctrl-C to end");
	      } 
	      catch (IOException e) 
	      {
		 System.out.println("Error creating socket");
		 System.exit(-1);
	      }
	      while(true)
	      {
	         ServerThread w;
	         try
	         {
	            w = new ServerThread(server.accept());
	            Thread t = new Thread(w);
	            t.start();
			  } 
			catch (IOException e) 
			{
		    System.out.println("Accept failed");
		    System.exit(-1);
			}
	      }
	   }

	   protected void finalize()
	   {
	      try
	      {
	         server.close();
	      } 
	      catch (IOException e) 
	      {
	         System.out.println("Could not close socket");
	         System.exit(-1);
	      }
	   }

	   public static void main(String[] args)
	   {
	      if (args.length != 1)
	      {
	         System.out.println("Usage: java SocketThrdServer port");
		 System.exit(1);
	      }

	      ServerDemo server = new ServerDemo();
	      int port = Integer.valueOf(args[0]);
	      server.listenSocket(port);
	   }
}
