import java.io.*;
import java.net.*;
public class UDPClientRegister{

	String name;
	int portNo;
	String serverIP;
	int sendPortNo;
	
UDPClientRegister()
{}
UDPClientRegister(String Name,String SIP,int port, int sendPort)
{
	name=Name;
	serverIP=SIP;
	portNo=port;
	sendPortNo=sendPort;
}
void register()
{
	byte [] sendData= new byte[1024];
	byte [] address= new byte[1024];
	String sentence;
	try{
	DatagramSocket clientSocket = new DatagramSocket(sendPortNo);
	System.out.println("Press s to sign in or r to register ");
	BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
	BufferedReader bf= new BufferedReader(new InputStreamReader(System.in));
	String input=br.readLine();
	if(input.equalsIgnoreCase("s"))
	{
		System.out.println("Enter reg space name space the port at which you were originally listening");
		sentence=bf.readLine()+" ";
		String stemp[] = sentence.split("\\s+");
		if(stemp[0].equalsIgnoreCase("reg")&&(stemp[1]!=null&&stemp[2]!=null))
		{
		
			ByteArrayOutputStream baos= new ByteArrayOutputStream();
			ObjectOutputStream oos= new ObjectOutputStream(baos);
			oos.writeObject(sentence);
			sendData = baos.toByteArray();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(serverIP),9876);
			clientSocket.send(sendPacket);
			oos.flush();
			oos.close();
			clientSocket.close();	
		}
		else
			System.out.println("Restart Client");
	}
	else
		if(input.equalsIgnoreCase("r"))
		{
			sentence=name+" "+portNo;
			UDPClientCheckInput CI= new UDPClientCheckInput(serverIP,Integer.toString(portNo));
			boolean checkPort=CI.portCheck();
			boolean checkIP=CI.IPcheck();
				if(!checkPort&&!checkIP)
					System.out.println("Server IP and Client Port number invalid, restart client ");
				else if(!checkIP)
					System.out.println("Invalid IP, restart client ");
				else
					if(!checkPort)
						System.out.println("Invalid Port number, restart client ");
			if(checkPort&&checkIP)
			{
				System.out.println(">>> Input verified");
				ByteArrayOutputStream baos= new ByteArrayOutputStream();
				ObjectOutputStream oos= new ObjectOutputStream(baos);
				oos.writeObject(sentence);
				sendData = baos.toByteArray();
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getByName(serverIP),9876);
				clientSocket.send(sendPacket);
				oos.flush();
				oos.close();
				clientSocket.close();
			}
		}
		if(!input.equalsIgnoreCase("r")&&!input.equalsIgnoreCase("s"))
			System.out.println("wrong input please restart client");
	}	
	catch(Exception e)
	{
		System.out.println("I/O or network exception restart client");
	}
	
	}
	
}

