import java.io.*;
import java.net.*;
public class ClientConnect extends Thread{
ServerSocket c;

	ClientConnect()
	{
		try{
			c= new ServerSocket(10000);
			this.start();
			}
			catch(Exception e)
			{
				System.out.println("Creating the client receive connection error"+e);
				
			}
	}
	
	public void run()
	{
		while(true) 
		{
		       try {
		        System.out.println("Client Waiting for connections.");
		        Socket client = c.accept();
		        System.out.println("Accepted a connection from: "+ client.getInetAddress());
		         //new UDPServerReceive(client);
		       } catch(Exception e) 
		       {
		    	   System.out.println("Error while invoking receive thread"+e);
		       }
		}
	}

	
}