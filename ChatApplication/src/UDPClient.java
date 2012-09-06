import java.io.*;
import java.net.*;
import java.util.ArrayList;
public class UDPClient implements Runnable{
		static boolean register=false;
		static boolean clearClientInfo=true;
		Thread t;
		static String clientName;
		static String serverIP;
		static String clientPort;
		static boolean serverAck=false;
		static int serverAckCount=0;
		static ArrayList<Client> clientInfo = new ArrayList<Client>();
		UDPClient(){}
		UDPClient(String threadName,String name, String IP, String port)
		{
			clientName=name;
			serverIP=IP;
			clientPort=port;
			t= new Thread(this,threadName);
			t.start();
		}
		synchronized static int getServerAckCount()
		{
			return serverAckCount;
		}
		synchronized static void setServerAckCount(int c)
		{
			serverAckCount=c;
		}
		synchronized static void setServerAck(boolean val)
		{
			serverAck=val;
		}
		synchronized static boolean getServerAck()
		{
			return serverAck;
		}
		synchronized static void clearClientinfo()
		{
			clientInfo.clear();
		}
		synchronized static void updateClientInfo(String []s)
		{
			clientInfo.add(new Client(s[1],s[2],s[3],s[4]));
			
		}
		synchronized static Client searchClientInfoName(String Name)
		{
			for(Client i:clientInfo)
			{
				if(Name.equalsIgnoreCase(i.name))
				{
					Client c= new Client();
					c.name=i.name;
					c.IP=i.IP;
					c.portNo=i.portNo;
					c.status=i.status;
					return c;
				}
			}
			return null;
		}
		synchronized static Client searchClientInfoIP(String IP)
		{
			for(Client i:clientInfo)
			{
				if(IP.equalsIgnoreCase(i.IP))
				{
					Client c= new Client();
					c.name=i.name;
					c.IP=i.IP;
					c.portNo=i.portNo;
					c.status=i.status;
					return c;
				}
			}
			return null;
		}
		synchronized static boolean checkRegisterValue()
		{
			return register;
		}
		synchronized static void updateRegisterValue(boolean v)
		{
			register=v;
		}
		public void run() {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String inputPortNo;
			String [] inputPortNoSplit=null;
			try{
				System.out.println("Enter the client send port and 1 auxillary port for communication ");
				inputPortNo=br.readLine();
				inputPortNoSplit=inputPortNo.split(" ");
				}
			catch(Exception e)
			{
				System.out.println("Wrong input "+e);
			}
			if((Integer.parseInt(inputPortNoSplit[0])<65535&&Integer.parseInt(inputPortNoSplit[0])>1024)&&(Integer.parseInt(inputPortNoSplit[1])<65535&&Integer.parseInt(inputPortNoSplit[1])>1024))
			{
				UDPClientReceive RC= new UDPClientReceive("receiveThread",Integer.parseInt(clientPort),Integer.parseInt(inputPortNoSplit[1]));
				(new UDPClientRegister(clientName,serverIP,Integer.parseInt(clientPort),Integer.parseInt(inputPortNoSplit[0]))).register(); //The client is asked to register or sign in
				UDPClientSend SC=new UDPClientSend("sendThread",Integer.parseInt(inputPortNoSplit[0]));
				try
				{
					RC.t.join();
					SC.t.join();
					
				}
			catch(InterruptedException e)
				{
				System.out.println("Main thread interrupted:");
				}
			}
			else
				System.out.println("Wrong input restart client");
			}
		
		}

	


