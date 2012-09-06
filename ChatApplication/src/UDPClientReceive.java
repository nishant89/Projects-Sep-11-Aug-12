import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
public class UDPClientReceive implements Runnable{
	Socket clientSocket;
	ObjectInputStream  ois;
	Thread t;
	String name;
	boolean  terminateFlag=true; 
	static int portNo;
	static int auxillaryPort;
	
	UDPClientReceive (String name, int port,int aux)
	{
		portNo=port;
		auxillaryPort=aux;
		t= new Thread(this, name);
		t.start();
	}
	public void run()
	{	
		byte[] receiveData = new byte[1024];
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try
		{
			DatagramSocket clientSocket = new DatagramSocket(portNo);
			DatagramSocket clientSocketACK = new DatagramSocket(auxillaryPort);
			while(terminateFlag)
				{
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
				clientSocket.receive(receivePacket);
				Date date = new Date();
				ByteArrayInputStream bais=new ByteArrayInputStream(receiveData);
				ois=new ObjectInputStream(bais);
				String sentence=(String)ois.readObject();
				InetAddress IPAddress = receivePacket.getAddress();
				int port = receivePacket.getPort();
				Client cr= new Client();
				String [] temp=sentence.split(":");
				String clientSentence[]= sentence.split(" ");
				if(clientSentence[0].trim()!=null)
				cr=UDPClient.searchClientInfoName(clientSentence[0].trim());
				if(clientSentence[0].trim().equalsIgnoreCase("you")&&(IPAddress.getHostAddress().equalsIgnoreCase(UDPClient.serverIP)&&port==9878))
					UDPClient.setServerAck(true);
				if((cr!=null||(IPAddress.getHostAddress().equalsIgnoreCase(UDPClient.serverIP)&&port==9878))&&(!temp[0].trim().equalsIgnoreCase("*")&&!temp[0].trim().equalsIgnoreCase("~")))
					{
					if(cr!=null&&port!=9878)
							{
								String out="";int k=0;
								for(String i:clientSentence)
								{
									if(k!=0)
										out+=i.trim()+" ";
									k++;
								}
								System.out.println(">>> "+dateFormat.format(date)+", Received from "+cr.name+": "+out);
							}
					else 	
						System.out.println(">>> "+dateFormat.format(date)+", Received from server: "+sentence);
					}
					
					if(cr!=null&&!(clientSentence[1].trim()).equalsIgnoreCase("ack"))
					{	
					byte []acktemp= new byte[1024];
					byte [] sendData = new byte[1024];
					String send=UDPClient.clientName+" "+"ACK";
					ByteArrayOutputStream baos= new ByteArrayOutputStream();
					ObjectOutputStream oos= new ObjectOutputStream (baos);
					oos.writeObject(send);
					sendData = baos.toByteArray();
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(cr.IP.trim()),Integer.parseInt(cr.portNo));				
					clientSocketACK.send(sendPacket);
					oos.flush();
				
				}
				if(temp[0].equalsIgnoreCase("welcome"));
					UDPClient.updateRegisterValue(true);
				if(temp[0].equalsIgnoreCase("~")&&Integer.parseInt(temp[5])==0)
					{
						
						UDPClient.clearClientinfo();
						UDPClient.updateClientInfo(temp);
					}
				else
					{
					if(temp[0].equalsIgnoreCase("~")&&Integer.parseInt(temp[5])!=0)
					{
						
						UDPClient.updateClientInfo(temp);
					}
					}
				if(temp[0].equalsIgnoreCase("*"))
					System.out.println(">>> Client Table Updated");
				cr=null;
				}
			
		}
		catch (Exception e)
		{
			System.out.println(">>> Network error: "+e);
		}
		
	}
	
	public void terminateThread()
	{
		terminateFlag=false;
	}

	
}
