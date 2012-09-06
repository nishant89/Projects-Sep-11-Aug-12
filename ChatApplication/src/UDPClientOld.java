import java.io.*;
import java.net.*;
public class UDPClientOld {
	
	public static void main(String args[]) throws Exception
	{
		
	while(true)
	{	
		String	sentence="";
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		DatagramSocket clientSocket = new DatagramSocket();
		InetAddress IPAddress =
		InetAddress.getByName("localhost");
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024];
		sentence = inFromUser.readLine();
		if (sentence.equalsIgnoreCase("exit"))
		break;
		else
			{
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 9876);
			clientSocket.send(sendPacket);
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);
			String modifiedSentence = new String (receivePacket.getData());
			System.out.println("Modified Data from Server:"+modifiedSentence);
			receivePacket=null;
			sendPacket=null;
			modifiedSentence="";
			clientSocket.close();
			}
	}
}
}