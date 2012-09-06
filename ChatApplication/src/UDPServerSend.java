import java.io.*;
import java.net.*;
public class UDPServerSend implements Runnable {
	
		Thread t;
		String name;
		boolean  terminateFlag=true; 
		
		UDPServerSend (String name)
		{
			t= new Thread(this,name);
			t.start();
		}
		
		public void run()
		{	
			byte[] sendData = new byte[1024];
			String sentence="";
			BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
			try
			{
				
				DatagramSocket serverSocket = new DatagramSocket(9877);
			while (terminateFlag)
			{	
				sentence=br.readLine();
				ByteArrayOutputStream baos= new ByteArrayOutputStream();
				ObjectOutputStream oos= new ObjectOutputStream (baos);
				oos.writeObject(sentence);
				sendData=baos.toByteArray();
				DatagramPacket packet= new DatagramPacket(sendData,sendData.length,InetAddress.getLocalHost(),9877);
				serverSocket.send(packet);				
				oos.flush();
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

