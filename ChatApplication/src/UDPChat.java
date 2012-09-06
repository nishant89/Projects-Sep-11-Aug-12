
public class UDPChat {

	static UDPServer server=null;
	static UDPClient client=null;
	public static void main(String[] args) {
		if(args[0].equalsIgnoreCase("-s"))
			server = new UDPServer("chatServer");
		if(args[0].equalsIgnoreCase("-c"))
		{
			if(args[2]!=null)
			client = new UDPClient("chatClient",args[1],args[2],args[4]);
		}
		}
	}

