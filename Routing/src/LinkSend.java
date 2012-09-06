import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Calendar;
public class LinkSend implements Runnable{
	Thread t;
	static boolean waitFlag=true;
LinkSend(String name)
{
	t= new Thread(this,name);
	t.start();
}
//Checks whether the Link Status is changed to verified
static synchronized void verificationCheck()
{
	lsnode.setCalendar3Time();
	long t1=lsnode.calendar3.getTimeInMillis();
	Calendar calendar4=Calendar.getInstance();
	long t2=calendar4.getTimeInMillis();
	while(waitFlag && (lsnode.receiveCount==1 && t2<t1+5000))
	{
		calendar4=Calendar.getInstance();
		t2=calendar4.getTimeInMillis();
		waitFlag=false;
		for(int i=0;i<lsnode.nodes.size();i++)
			if(lsnode.nodes.get(i).status.equalsIgnoreCase("unverified"))
				waitFlag=true;
	}
	if(t2>t1+5000)
		{
		lsnode.crash=true;
		System.out.println("\nFailed to receive UDP verification packet, hence it is a crash and exiting");
		}
	try{
	Thread.sleep(1000); //Brings about the 1 second  wait for Link Status verification to complete
	}
	catch(Exception e)
	{
		System.out.println(e);
	}
	}
public void run()
{
		try{
			DatagramSocket linkSocket = new DatagramSocket(Integer.parseInt(lsnode.nodePort)+1);
			while(true)
			{
			//System.out.println("Entered the send thread");
			byte[] sendData = new byte[1024];
			String sentence;
			//Sending verification msgs to neighbors
				if(lsnode.lastFlag && lsnode.receiveCount==0)
				{
					for(int i=0;i<lsnode.nodes.size();i++)
					{
						sentence="v "+lsnode.nodePort+" "+lsnode.nodes.get(i).distance;
						System.out.println("Sending Verification Msg from Node "+lsnode.nodePort+" to Node "+lsnode.nodes.get(i).portNo);
						ByteArrayOutputStream baos= new ByteArrayOutputStream();
						ObjectOutputStream oos= new ObjectOutputStream (baos);
						oos.writeObject(sentence);
						sendData = baos.toByteArray();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getLocalHost(),Integer.parseInt(lsnode.nodes.get(i).portNo));				
						linkSocket.send(sendPacket);
						oos.flush();
					}
					lsnode.lastFlag=false;
					lsnode.receiveCount=1;
				}
				else
				{	//If it's not the last node to be entered and it sends verification msgs after receiving one from it's neighbor
					if(lsnode.getReceiveOnceValue() && lsnode.receiveCount==0)
					{
						
						for(int i=0;i<lsnode.nodes.size();i++)
							{
								sentence="v "+lsnode.nodePort+" "+lsnode.nodes.get(i).distance;
								if(!lsnode.nodes.get(i).portNo.equalsIgnoreCase(lsnode.nodePort))
								{
									System.out.println("Sending Verification Msg from Node "+lsnode.nodePort+" to Node "+lsnode.nodes.get(i).portNo);
									ByteArrayOutputStream baos= new ByteArrayOutputStream();
									ObjectOutputStream oos= new ObjectOutputStream (baos);
									oos.writeObject(sentence);
									sendData = baos.toByteArray();
									DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getLocalHost(),Integer.parseInt(lsnode.nodes.get(i).portNo));				
									linkSocket.send(sendPacket);
									oos.flush();
								}
							}
						lsnode.receiveCount=1;
					}
				}
				//if a crash occurs the msg is propagated to it's neighbors so that they can exit the network
				if(lsnode.crash)
				{
					sentence="c "+lsnode.crashNode;
					for(int i=0;i<lsnode.nodes.size();i++)
					{
						System.out.println("Sending Crash Msg from Node "+lsnode.nodePort+" to Node "+lsnode.nodes.get(i).portNo);
						ByteArrayOutputStream baos= new ByteArrayOutputStream();
						ObjectOutputStream oos= new ObjectOutputStream (baos);
						oos.writeObject(sentence);
						sendData = baos.toByteArray();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getLocalHost(),Integer.parseInt(lsnode.nodes.get(i).portNo));				
						linkSocket.send(sendPacket);
						oos.flush();
					}
					System.out.println("Exiting due to crash in "+lsnode.crashNode);
					System.exit(1);
				}
				verificationCheck();
				if((!lsnode.sendLinkState && lsnode.receiveCount==1))
						System.out.println("\nMessage flooding started ");
				//Once verification is complete, message flooding starts using the block below
				if((!lsnode.sendLinkState && lsnode.receiveCount==1)||lsnode.isUpdated)
				{
					lsnode.sendLinkState=true;
					lsnode.isUpdated=false;
					for(int k=0;k<lsnode.nodes.size();k++)
					{
						for(int i=0; i<lsnode.adjMatrix.size();i++)
						{
							sentence="l "+Integer.toString(lsnode.adjMatrix.get(i).x)+" ";
							for(int j=0;j<lsnode.adjMatrix.get(i).Xlist.size();j++)
							{
								sentence+=Integer.toString(lsnode.adjMatrix.get(i).Xlist.get(j).y)+" "+Integer.toString(lsnode.adjMatrix.get(i).Xlist.get(j).distance)+" ";
							}	
							if(!(Integer.parseInt(lsnode.nodes.get(k).portNo)==Integer.parseInt(lsnode.nodePort)))
							{
								System.out.println("Sending Link State from Node "+lsnode.nodePort+" to Node "+lsnode.nodes.get(k).portNo);
								ByteArrayOutputStream baos= new ByteArrayOutputStream();
								ObjectOutputStream oos= new ObjectOutputStream (baos);
								oos.writeObject(sentence);
								sendData = baos.toByteArray();
								DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getLocalHost(),Integer.parseInt(lsnode.nodes.get(k).portNo));				
								linkSocket.send(sendPacket);
								oos.flush();
							}
						}
					}	
				}
				lsnode.calendar2=Calendar.getInstance();
				lsnode.t2=lsnode.calendar2.getTimeInMillis();
				//Waits for 5 seconds after receiving the last Link Status information update
				if(lsnode.t2>lsnode.t1+5000 && (lsnode.countReceiveLinkState!=0 && lsnode.makeTable==0))
				{
					System.out.println("\nMessage flooding finished ");
					lsnode.makeTable=1;
					lsnode.displayAdjMatrix();
					lsnode.dijkstra();
					//If it's the last node to be entered then it will send a msg to the farthest node from it in the network as specified in the question
					if(lsnode.lastFlagSendMsg)
					{
						int max=0; int destination=Integer.parseInt(lsnode.nodePort);int adj=Integer.parseInt(lsnode.nodePort);
						for(int i=0;i<lsnode.resultSet.size();i++)
								{
									if(lsnode.resultSet.get(i).distance>max)
										{
										max=lsnode.resultSet.get(i).distance;
										destination=lsnode.resultSet.get(i).y;
										}
								}
						for(int i=0;i<lsnode.routingTable.size();i++)
								{
									if(destination==lsnode.routingTable.get(i).d)
										adj=lsnode.routingTable.get(i).a;	
								}
						sentence="m "+lsnode.nodePort+" "+Integer.toString(destination)+" "+Integer.toString(max);
						String []Temp=sentence.split(" ");
						System.out.println("\nSending Msg to Node "+Temp[2]+" Remaining distance: "+Temp[3]);
						ByteArrayOutputStream baos= new ByteArrayOutputStream();
						ObjectOutputStream oos= new ObjectOutputStream (baos);
						oos.writeObject(sentence);
						sendData = baos.toByteArray();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getLocalHost(),adj);				
						linkSocket.send(sendPacket);
						oos.flush();
					}
				}
				//Once the last msg exchanged between the last Node and the farthest terminate msg sent to all neighbors and program terminates
				if(lsnode.terminateProgram)
				{
					for(int i=0;i<lsnode.nodes.size();i++)
					{
						sentence="e ";
						ByteArrayOutputStream baos= new ByteArrayOutputStream();
						ObjectOutputStream oos= new ObjectOutputStream (baos);
						oos.writeObject(sentence);
						sendData = baos.toByteArray();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getLocalHost(),Integer.parseInt(lsnode.nodes.get(i).portNo));				
						linkSocket.send(sendPacket);
						oos.flush();
					}
					System.out.println("\nTask performed, exiting program, bye");
					System.exit(1);
				}
				//Propagates msg along the shortest path from source node to destination node
				if(lsnode.msgSet)
				{	
					lsnode.msgSet=false;
					String[] splitTemp=lsnode.msg.split(" ");int adj=Integer.parseInt(lsnode.nodePort);
					sentence="m "+splitTemp[1]+" "+splitTemp[2]+" ";
					if(splitTemp[2].equalsIgnoreCase(lsnode.nodePort))
							{
								for(int i=0;i<lsnode.resultSet.size();i++)
								{
									if(lsnode.receiveMsgNode==lsnode.resultSet.get(i).y)
										sentence+=Integer.toString(Integer.parseInt(splitTemp[3])-lsnode.resultSet.get(i).distance);
								}
								splitTemp=sentence.split(" ");
								System.out.println("\nMsg received from Node "+splitTemp[1]+" Remaining distance: "+splitTemp[3]);
								for(int i=0;i<lsnode.nodes.size();i++)
								{
									sentence="e ";
									ByteArrayOutputStream baos= new ByteArrayOutputStream();
									ObjectOutputStream oos= new ObjectOutputStream (baos);
									oos.writeObject(sentence);
									sendData = baos.toByteArray();
									DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getLocalHost(),Integer.parseInt(lsnode.nodes.get(i).portNo));				
									linkSocket.send(sendPacket);
									oos.flush();
								}
								System.out.println("\nTask performed, exiting program, bye");
								System.exit(1);
							}
					else
					{
						for(int i=0;i<lsnode.routingTable.size();i++)
						{
							if(Integer.parseInt(splitTemp[2])==lsnode.routingTable.get(i).d)
								adj=lsnode.routingTable.get(i).a;
						}
						for(int i=0;i<lsnode.resultSet.size();i++)
						{
							if(lsnode.receiveMsgNode==lsnode.resultSet.get(i).y)
								sentence+=Integer.toString(Integer.parseInt(splitTemp[3])-lsnode.resultSet.get(i).distance);
						}
						splitTemp=sentence.split(" ");
						System.out.println("\nMsg received from Node "+splitTemp[1]+" Remaining distance: "+splitTemp[3]);
						System.out.println("\nSending Msg from Node "+lsnode.nodePort+" to Node "+adj);
						ByteArrayOutputStream baos= new ByteArrayOutputStream();
						ObjectOutputStream oos= new ObjectOutputStream (baos);
						oos.writeObject(sentence);
						sendData = baos.toByteArray();
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,InetAddress.getLocalHost(),adj);				
						linkSocket.send(sendPacket);
						oos.flush();
					}
				}
			}
		}
		catch(Exception e)
		{
			System.out.println("Error in send thread: "+e);
		}
	}
}
