import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
public class UDPClientCheckInput {
	String IPAdress;
	int portNo;
	boolean IPcheck=true;
	boolean portCheck=true;
UDPClientCheckInput()
	{}
UDPClientCheckInput(String IP,String port)
	{
		IPAdress = IP;
		portNo = Integer.parseInt(port);
		
	}
boolean IPcheck()
	{	
	Pattern p=Pattern.compile("\\d+.\\d+.\\d+.\\d+");
	Matcher m=p.matcher(IPAdress.subSequence(0,IPAdress.length()));
	IPcheck = m.matches();
	String [] temp = IPAdress.split("\\.");
	for(int i=0; i<temp.length;i++)
		{
		if(Integer.parseInt(temp[i])>255||Integer.parseInt(temp[i])<0)
			{
			IPcheck=false;	
			break;
			}
		}
	return IPcheck;
	}
boolean portCheck()
	{
		if(portNo>65535||portNo<1024)
			portCheck=false;
		return portCheck;
	}
}


