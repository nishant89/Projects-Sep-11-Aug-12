import java.io.*;
import java.util.*;
import java.net.*;
public class UDPServer implements Runnable{
	static ArrayList<Client> clientInfo= new ArrayList<Client>();
	Thread t;
	public UDPServer()
	{}
	public UDPServer(String name)
	{
		t= new Thread(this,name);
		t.start();
	}
	static synchronized void deleteClientInfo(String name)
	{
		ArrayList<Client> clientInfoCopy = (ArrayList<Client>)clientInfo.clone();
		for(Client i:clientInfoCopy)
		{
			if(name.equalsIgnoreCase(i.getName()))
			{
				try{
					DatagramSocket serverSocket= new DatagramSocket(9878);
					byte [] sendData= new byte[1024];
					String sentence = "you are offline bye";
					ByteArrayOutputStream baos= new ByteArrayOutputStream();
					ObjectOutputStream oos= new ObjectOutputStream(baos);
					oos.writeObject(sentence);
					sendData=baos.toByteArray();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(i.IP),Integer.parseInt(i.portNo));
					serverSocket.send(sendPacket);
					oos.flush();
					oos.close();
					serverSocket.close();
					System.out.println(i.name+" Deregistered");
					clientInfo.remove(i);
					(new UDPServerPush()).push(UDPServer.clientInfo);
					}
				catch(Exception e)
				{
					System.out.println("Dereg problem:"+e);
				}
				
			}
		}
	}
	static synchronized void updateClientInfo(String name,String IPAddress, String port)
	{	
		boolean Flag=false;
		for(Client i: clientInfo)
		{
			if(name.equalsIgnoreCase(i.getName()))
			{
				i.status="Online";
				System.out.println("Client already present");
				Flag=true;
				try
				{
					DatagramSocket serverSocket= new DatagramSocket(9878);
					byte [] sendData= new byte[1024];
					String sentence = "Welcome, you are already registered, changing status to online";
					ByteArrayOutputStream baos= new ByteArrayOutputStream();
					ObjectOutputStream oos= new ObjectOutputStream(baos);
					oos.writeObject(sentence);
					sendData=baos.toByteArray();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(IPAddress),Integer.parseInt(port));
					serverSocket.send(sendPacket);
					oos.flush();
					oos.close();
					serverSocket.close();
					(new UDPServerPush()).push(UDPServer.clientInfo);				
					
				}
				catch(Exception e)
				{
					System.out.println("Network exception while replying to client" + e);
				}
				break;
			}
			
		}
			if(!Flag)
				{
				clientInfo.add(new Client(name,IPAddress,port,"Online"));
				try
				{
					DatagramSocket serverSocket= new DatagramSocket(9878);
					byte [] sendData= new byte[1024];
					String sentence = "Welcome, you are registered";
					ByteArrayOutputStream baos= new ByteArrayOutputStream();
					ObjectOutputStream oos= new ObjectOutputStream(baos);
					oos.writeObject(sentence);
					sendData=baos.toByteArray();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(IPAddress),Integer.parseInt(port));
					serverSocket.send(sendPacket);
					oos.flush();
					oos.close();
					serverSocket.close();
					(new UDPServerPush()).push(UDPServer.clientInfo);
					
				}
				catch(Exception e)
				{
					System.out.println("Network exception while replying to client"+ e);
					
				}
								
				}
	}
	static synchronized void searchClientInfo(String name,String IPAddress, String port)
	{
		boolean Flag=false;
		for(Client i: clientInfo)
		{
			if(name.equalsIgnoreCase(i.getName())&&port.equalsIgnoreCase(i.portNo))
			{
				i.status="Online";
				System.out.println("Client already present status changed to online");
				try
				{	
					DatagramSocket serverSocket= new DatagramSocket(9878);
					byte [] sendData= new byte[1024];
					String sentence = "Welcome, you are already registered";
					ByteArrayOutputStream baos= new ByteArrayOutputStream();
					ObjectOutputStream oos= new ObjectOutputStream(baos);
					oos.writeObject(sentence);
					sendData=baos.toByteArray();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(i.IP),Integer.parseInt(i.portNo));
					serverSocket.send(sendPacket);
					oos.flush();
					oos.close();
					serverSocket.close();
					(new UDPServerPush()).push(UDPServer.clientInfo);

					
				}
				
				catch(Exception e)
				{
					System.out.println("Network exception while replying to client to check sign in status"+e);
					
				}
				Flag=true;
				break;
			}
		}
		if(!Flag)
		{
			try
			{
				DatagramSocket serverSocket= new DatagramSocket(9878);
				byte [] sendData= new byte[1024];
				String sentence = "Sorry, you are not registered, please log in again or register by restarting the client";
				System.out.println("Sorry, you are not registered, please log in again or register by restarting the client");
				ByteArrayOutputStream baos= new ByteArrayOutputStream();
				ObjectOutputStream oos= new ObjectOutputStream(baos);
				oos.writeObject(sentence);
				sendData=baos.toByteArray();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(IPAddress),Integer.parseInt(port));
				serverSocket.send(sendPacket);
				oos.flush();
				oos.close();
				serverSocket.close();
			}
			catch(Exception e)
			{
				System.out.println("Network exception while replying to client that he/she is already registered"+e);
				
			}
		}

	}

	public void run() {
		
	UDPServerReceive RS= new UDPServerReceive("receiveThread");	
		
	
	try
	{
		RS.t.join();
		
	}
	catch(InterruptedException e)
	{
		System.out.println("Main thread interrupted:");
	}
	}
}
	
