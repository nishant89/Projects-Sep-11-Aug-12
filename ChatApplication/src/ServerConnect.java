import java.io.*;
import java.net.*;
public class ServerConnect extends Thread
{
	ServerSocket ss;
public ServerConnect()
{
	try{
	ss= new ServerSocket(9876);
	this.start();
		}
	catch(Exception e)
	{
		System.out.println("Creating the server connection error"+e);
		
	}
	
}
	public void run()
	{
		while(true) 
		{
		       try {
		        System.out.println("Waiting for connections.");
		        Socket client = ss.accept();
		        System.out.println("Accepted a connection from: "+ client.getInetAddress());
		         //new UDPServerReceive(client);
		       } catch(Exception e) 
		       {
		    	   System.out.println("Error while invoking receive thread"+e);
		       }
		}
	}
}
