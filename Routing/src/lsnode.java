import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
public class lsnode {
	static long t1,t2;
	static boolean receiveOnce=false;
	static int receiveCount=0;
	static boolean sendLinkState=false;
	static boolean lastFlag=false;
	static boolean lastFlagSendMsg=false;
	static String nodePort;
	static ArrayList<Node> nodes= new ArrayList<Node>();
	static ArrayList<Xnode> adjMatrix= new ArrayList<Xnode>();
	static Calendar calendar1;
	static Calendar calendar2;
	static int countReceiveLinkState=0;
	static ArrayList<ListNode> qNode = new ArrayList<ListNode>();
	static ArrayList<ListNode> resultSet = new ArrayList<ListNode>();
	static HashMap<Integer,Integer> parent = new HashMap<Integer, Integer>();
	static int makeTable=0;
	static  boolean isUpdated=false;
	static ArrayList<routingTableNode> routingTable= new ArrayList<routingTableNode>();
	static boolean crash=false;
	static int crashNode;
	static String msg="";
	static boolean msgSet=false;
	static int receiveMsgNode;
	static boolean terminateProgram=false;
	static Calendar calendar3;
	//Used by the send thread to get the current time
	static synchronized void setCalendar3Time()
	{
 		calendar3=Calendar.getInstance();
		
	}
	static synchronized void initiateTime()
	{
 		calendar1=Calendar.getInstance();
		t1=calendar1.getTimeInMillis();
	}
	//Used to create the Adjacency Matrix while receiving the Link State information
	static synchronized void updateAdjMatrix(String []splitSentence)
	{
		//Create nodes
		boolean xFlag=false;
		int lastIndex;
		boolean findSource=false;
		for(int f=0;f<adjMatrix.size();f++)
		{
			if(Integer.parseInt(splitSentence[1])==adjMatrix.get(f).x)
				{
					findSource=true;
					break;
				}
		}
		if(!findSource)
		{
			lsnode.adjMatrix.add(new Xnode(Integer.parseInt(splitSentence[1])));
			isUpdated=true;
		}
		for(int s=2;s<splitSentence.length;s=s+2)
		{	
			boolean find=false;
			for(int f=0;f<adjMatrix.size();f++)
			{
				if(Integer.parseInt(splitSentence[s])==adjMatrix.get(f).x)
					{
						find=true; boolean findWithin=false;
						for(int k=0;k<adjMatrix.get(f).Xlist.size();k++)
						{
							if(adjMatrix.get(f).Xlist.get(k).y==Integer.parseInt(splitSentence[s]))
								{
								//adjMatrix.get(f).Xlist.get(k).distance=0;
								findWithin=true;
								}
						}
						if(!findWithin)
						{
							adjMatrix.get(f).Xlist.add(new ListNode(Integer.parseInt(splitSentence[s]),0));
							isUpdated=true;
						}
						break;
					}
			}
			if(!find)
			{
				int adjSize=adjMatrix.size();
				adjMatrix.add(new Xnode(Integer.parseInt(splitSentence[s])));
				adjMatrix.get(adjSize).Xlist.add(new ListNode(Integer.parseInt(splitSentence[s]),0));
				isUpdated=true;
			}
		}
		//Search and update node lists
		for(int i=0;i<lsnode.adjMatrix.size();i++)
		{
			if(splitSentence[1].equalsIgnoreCase(Integer.toString(lsnode.adjMatrix.get(i).x)))
			{
				xFlag=true;
				for(int s=2;s<splitSentence.length;s=s+2)
				{
					boolean sFlag=false;
					for(int j=0;j<lsnode.adjMatrix.get(i).Xlist.size();j++)
					{
						if(splitSentence[s].equalsIgnoreCase(Integer.toString(lsnode.adjMatrix.get(i).Xlist.get(j).y)))
						{
							//lsnode.adjMatrix.get(i).Xlist.get(j).distance=Integer.parseInt(splitSentence[s+1]);
							sFlag=true;
						}
					}
					if(!sFlag)	
					{
						lsnode.adjMatrix.get(i).Xlist.add(new ListNode(Integer.parseInt(splitSentence[s]),Integer.parseInt(splitSentence[s+1])));
						isUpdated=true;
					}
				}
			}
		}
	}
	//Used to display the adjacency matrix
	static synchronized void displayAdjMatrix()
	{	
		System.out.println("Node "+nodePort+" received the following link-state information:");
		for(int i=0;i<lsnode.adjMatrix.size();i++)
		{	
			System.out.print("Source Node: "+lsnode.adjMatrix.get(i).x+"\t");
			for(int j=0;j<lsnode.adjMatrix.get(i).Xlist.size();j++)
			{
				System.out.print("Neighbor Node: "+lsnode.adjMatrix.get(i).Xlist.get(j).y+" Distance: "+lsnode.adjMatrix.get(i).Xlist.get(j).distance+"\t");
			}
			System.out.print("\n");
		}
	}
	//Used to initialize the adjacency matrix
	static synchronized void initializeAdjMatrix()
	{
		adjMatrix.add(new Xnode(Integer.parseInt(nodePort)));
		adjMatrix.get(0).Xlist.add(new ListNode(Integer.parseInt(nodePort),0));
		for(int i=0;i<nodes.size();i++)
		{
			adjMatrix.get(0).Xlist.add(new ListNode(Integer.parseInt(nodes.get(i).portNo),Integer.parseInt(nodes.get(i).distance)));
			adjMatrix.add(new Xnode(Integer.parseInt(nodes.get(i).portNo)));
			adjMatrix.get(i+1).Xlist.add(new ListNode(Integer.parseInt(nodes.get(i).portNo),0));
			adjMatrix.get(i+1).Xlist.add(new ListNode(Integer.parseInt(nodePort),Integer.parseInt(nodes.get(i).distance)));
			
		}
	}
	//Used to update the status of the neighboring nodes during the verification process. Accessed by the receiving thread
	static synchronized int updateNodeStatus(String port, String dis)
	{	
		int flag=0;
		for(int i=0; i<nodes.size();i++)
		{	
			if(nodes.get(i).portNo.equalsIgnoreCase(port) && nodes.get(i).distance.equalsIgnoreCase(dis))
			{
				nodes.get(i).status="verified";
				return 1;
			}
			if(nodes.get(i).portNo.equalsIgnoreCase(port) && !nodes.get(i).distance.equalsIgnoreCase(dis))
			{
				nodes.get(i).status="Verified";
				flag=-1;
			}
		}
		return flag;
	}
	//Finds minimum for the djiktra's algorithm
	static int minimum(ArrayList<ListNode> q)
	{
		int minimum=q.get(0).distance;
		for(int i=0; i<q.size();i++)
		{
			if(q.get(i).distance<minimum)
				return i;
		}
		return 0;
	}
	//Computes the minimum distance from the current node
	static void dijkstra()
	{
		//System.out.println("Entered dijkstra's ");
		int qSize, minimumPosition;
		//resultSet.add(new ListNode(Integer.parseInt(nodePort),0));
		parent.put(Integer.parseInt(nodePort),Integer.parseInt(nodePort));
		for(int i=0;i<adjMatrix.size();i++)
		{
			if(adjMatrix.get(i).x==Integer.parseInt(nodePort))
				qNode.add(new ListNode(adjMatrix.get(i).x,0));
			else
			qNode.add(new ListNode(adjMatrix.get(i).x,1000000));
		}
		qSize=qNode.size();
		while(qSize!=0)
		{
			int i;
			qSize--;
			minimumPosition=minimum(qNode);
			resultSet.add(qNode.get(minimumPosition));
			for(i=0;i<adjMatrix.size();i++)
				if(adjMatrix.get(i).x==qNode.get(minimumPosition).y)
					break;
			for(int j=0;j<adjMatrix.get(i).Xlist.size();j++)
			{
				for(int k=0;k<qNode.size();k++)
				{
					if(adjMatrix.get(i).Xlist.get(j).y==qNode.get(k).y)
					{					
						if(qNode.get(minimumPosition).distance+adjMatrix.get(i).Xlist.get(j).distance<qNode.get(k).distance)
						{
							qNode.get(k).distance=qNode.get(minimumPosition).distance+adjMatrix.get(i).Xlist.get(j).distance;
							parent.put(qNode.get(k).y,qNode.get(minimumPosition).y);
						}
					}
				}		
			}
			qNode.remove(minimumPosition);
		}
//		for(int s=0;s<resultSet.size();s++)
//		{
//			System.out.print("Node: "+resultSet.get(s).y+" Distance: "+resultSet.get(s).distance+"\t");
//		}
		createRoutingTable();
	}
	//Used to recursively find the adjacent node to the source through which the Msg can be routed to the destination node
	static int findAdjacent(int x)
	{
		int temp;
		temp=(int)parent.get(x);
		if(temp==Integer.parseInt(nodePort))
			return x;
		else
			return findAdjacent(temp);
	}
	//Creates the routing table for the current node after Link State Flooding is completed
	static synchronized void createRoutingTable()
	{
		System.out.println("\nNode "+nodePort+" starts routing table construction");
		int adjacentNode=Integer.parseInt(nodePort);
		for(int i=0;i<adjMatrix.size();i++)
		{
		adjacentNode=findAdjacent(adjMatrix.get(i).x);
		routingTable.add(new routingTableNode(adjMatrix.get(i).x,adjacentNode));
		}
		System.out.println("\nNode "+nodePort+" routing table:");
		for(int i=0;i<routingTable.size();i++)
			System.out.println("Destination: "+routingTable.get(i).d+" AdjacentNode: "+routingTable.get(i).a);
	}
	static synchronized boolean getReceiveOnceValue()
	{
		return receiveOnce;
	}
	static synchronized void setReceiveOnceValue(boolean value)
	{
		receiveOnce=value;
	}
	public static void main(String[] args) {
		//Accessing commandline arguments
		for(int i=0;i<args.length;i++)
		{
			if(args[i].equalsIgnoreCase("last"))
				{
					lastFlagSendMsg=true;
					lastFlag=true;
				}
			if(Integer.parseInt(args[0])>1024 && Integer.parseInt(args[0])<65536)
				{
				nodePort=args[0];
				}
			else
				{
				System.out.println("The main node port no. is invalid restart your program");
				System.exit(1);
				}
			if(i%2!=0 && i+1<args.length)
			{
				if(Integer.parseInt(args[i])>1024 && Integer.parseInt(args[i])<65536)
				{
					for(int f=0; f<nodes.size();f++)
					{
						if(nodes.get(f).portNo.equalsIgnoreCase(args[i]))
						{
							System.out.println("Duplicate node, exiting program");
							System.exit(1);
						}
					}
					nodes.add(new Node(args[i],args[i+1]));
				}
				else
				{
					System.out.println("The node port no. is invalid restart your program");
					System.exit(1);
				}
			}
		}
//		for(int i=0;i<nodes.size();i++)
//			System.out.println("portno = "+nodes.get(i).portNo+" distance = "+nodes.get(i).distance);
		initializeAdjMatrix();
		lsnode.crashNode=Integer.parseInt(nodePort);
		receiveMsgNode=Integer.parseInt(nodePort);
		LinkSend ls = new LinkSend("Send");				//Spawning the send thread
		LinkReceive lr= new LinkReceive("Receive");		//Spawning the receive thread
		try
		{
			ls.t.join();
			lr.t.join();
		}
		catch(InterruptedException e)
		{
			System.out.println("Main thread interrupted:");
		}
	}
}
