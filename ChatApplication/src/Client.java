import java.io.*;
import java.net.*;
import java.util.*;
public class Client implements Serializable {

	String name;
	String IP;
	String portNo;
	String status;
	Client()
	{}
	Client(String Name,String IPAddress, String port, String Stat)
	{
		name=Name;
		portNo=port;
		IP=IPAddress;
		status=Stat;
	}
	String getName()
	{
		return name;
	}
	String getIP()
	{
		return IP;
		
	}
	String getPort()
	{
		return portNo;
		
	}
	
}
