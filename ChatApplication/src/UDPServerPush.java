import java.util.*;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.net.*;
public class UDPServerPush {

	synchronized void push(ArrayList<Client> c)
	{
		int k=0;
		for(Client i:c)
			{
			for(Client j:c)
				{
				try
				{
					DatagramSocket serverSocket= new DatagramSocket(9878);
					byte [] sendData= new byte[1024];
					String sentence = "~"+":"+j.name+":"+j.IP+":"+j.portNo+":"+j.status+":"+k+":";
					ByteArrayOutputStream baos= new ByteArrayOutputStream();
					ObjectOutputStream oos= new ObjectOutputStream(baos);
					oos.writeObject(sentence);
					sendData=baos.toByteArray();
					DatagramPacket packet= new DatagramPacket(sendData,sendData.length,InetAddress.getByName(i.IP),Integer.parseInt(i.portNo));
					serverSocket.send(packet);
					oos.flush();
					serverSocket.close();
				}
				catch(Exception e)
				{
					System.out.println("Problem in sending the clientInfo table:"+e);
					
				}
					k++;
				}
			try
			{
			DatagramSocket serverSocket= new DatagramSocket(9878);
			byte [] sendData= new byte[1024];
			String sentence ="*"+":"; 
			ByteArrayOutputStream baos= new ByteArrayOutputStream();
			ObjectOutputStream oos= new ObjectOutputStream(baos);
			oos.writeObject(sentence);
			sendData=baos.toByteArray();
			DatagramPacket packet= new DatagramPacket(sendData,sendData.length,InetAddress.getByName(i.IP),Integer.parseInt(i.portNo));
			serverSocket.send(packet);
			oos.flush();
			serverSocket.close();
			}
			catch(Exception e)
			{
				System.out.println("While sending the end character of the table "+e);
			}
			}
		}
	
	}

