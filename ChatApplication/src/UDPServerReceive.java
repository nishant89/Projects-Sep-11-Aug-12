import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
public class UDPServerReceive extends Thread {
	Socket serverSocket;
	ObjectInputStream ois;
	Thread t;
	String name;
	boolean  terminateFlag=true; 
	
	UDPServerReceive (String name)
	{
		t= new Thread(this,name);
		t.start();
	}
	
	public int convert(char c[], int n)
	{
		int result=0; int j=1;
		for(int i=n-1;i>=0;i--)
		{
			result+=Character.getNumericValue(c[i])*j;
			j=j*10;
		}
		return result;
	}
	public void run()
	{	
		
		
		try
		{ 
			
			byte[] receiveData = new byte[1024];
			DatagramSocket serverSocket = new DatagramSocket(9876);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			while(terminateFlag)
			{
				
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				serverSocket.receive(receivePacket);
				Date date = new Date();
				ByteArrayInputStream bais= new ByteArrayInputStream(receiveData);
				ObjectInputStream ois= new ObjectInputStream(bais);
				String sentence = (String)ois.readObject();
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				System.out.println(">>> "+dateFormat.format(date)+", Received from client: "+IPAddress+" "+port+" "+sentence);
				String [] sentenceSplit= sentence.split(" ");
				if(!(sentenceSplit[0].trim().equalsIgnoreCase("reg"))&&!(sentenceSplit[0].trim().equalsIgnoreCase("dereg")))
					UDPServer.updateClientInfo(sentenceSplit[0].trim(),IPAddress.getHostName(),sentenceSplit[1].trim());
				if(sentenceSplit[0].trim().equalsIgnoreCase("reg"))
					UDPServer.searchClientInfo(sentenceSplit[1].trim(),IPAddress.getHostName(),sentenceSplit[2].trim());
				if(sentenceSplit[0].trim().equalsIgnoreCase("dereg"))
					UDPServer.deleteClientInfo(sentenceSplit[1].trim());
			}
		}
		catch (Exception e)
		{
			System.out.println("Print the socket error:"+e);
		}
	}
	
	
	public void terminateThread()
	{
		terminateFlag=false;
	}

}

