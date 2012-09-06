import java.io.*;
import java.net.*;
public class UDPClientSend implements Runnable {
	
	Thread t;
	String name;
	boolean  terminateFlag=true; 
	static int portNo;
	
	UDPClientSend (String name, int port)
	{
		portNo=port;
		t= new Thread(this,name);
		t.start();
	}
	
	public void run()
	{	
		
		try
		{
			byte[] sendData = new byte[1024];
			String sentence="";
			BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
			DatagramSocket clientSocket = new DatagramSocket(portNo);
		while (terminateFlag)
		{	
			System.out.print(">>> ");
			sentence=br.readLine();
			String [] sentenceTemp = sentence.split(" ");
			if(sentenceTemp[0].trim().equalsIgnoreCase("dereg")&&sentenceTemp[1].trim().equalsIgnoreCase(UDPClient.clientName))
			{
				UDPClient.setServerAckCount(1);
				int i=1;
				while(!(UDPClient.getServerAck())&&(UDPClient.getServerAckCount()<=5))
				{
				i++;
				ByteArrayOutputStream baos= new ByteArrayOutputStream();
				ObjectOutputStream oos= new ObjectOutputStream (baos);
				oos.writeObject(sentence);
				sendData = baos.toByteArray();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(UDPClient.serverIP),9876);				
				clientSocket.send(sendPacket);
				UDPClient.setServerAckCount(i);
				Thread.sleep(5);
				oos.flush();
				}
				if(!UDPClient.getServerAck())
					{	
					System.out.println(">>> [Server not responding]");	
					System.out.println(">>> [Exiting]");
					}
			}
			if((sentenceTemp[0].trim()).equalsIgnoreCase("send"))
			{
				Client cr=new Client();
				cr=UDPClient.searchClientInfoName((sentenceTemp[1]).trim());
				if(cr==null)
				System.out.println(">>> Sorry no match found to communicate");
				else
					if((cr.status).equalsIgnoreCase("online"))
					{	
						int k=0; String send=UDPClient.clientName+" ";
						for(String i:sentenceTemp)
						{
							if(k>1)
								send+=i.trim()+" ";
							k++;
						}
						byte temp[]= new byte[1024];
						ByteArrayOutputStream baos= new ByteArrayOutputStream();
						ObjectOutputStream oos= new ObjectOutputStream (baos);
						oos.writeObject(send);
						sendData = baos.toByteArray();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(cr.IP.trim()),Integer.parseInt(cr.portNo));				
						clientSocket.send(sendPacket);
						oos.flush();
					}
					else
						System.out.println(">>> Client offline");
			}

		}
		}
		catch (Exception e)
		{
			System.out.println("Check Error:"+e);
		}
	}

	
	public void terminateThread()
	{
		terminateFlag=false;
	}

}
