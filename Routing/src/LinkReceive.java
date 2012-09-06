import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LinkReceive implements Runnable{
	long t1,t2;
	Thread t;
	LinkReceive(String name)
	{
		t= new Thread(this,name);
		t.start();
	}
	
	public void run()
	{
			try
			{	
				DatagramSocket linkSocket = new DatagramSocket(Integer.parseInt(lsnode.nodePort));
				String sentence;
				String splitSentence[];
				int verificationFlag=0;
				while(true)
				{	
					byte[] receiveData = new byte[1024];
					DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					linkSocket.receive(receivePacket);
					ByteArrayInputStream bais=new ByteArrayInputStream(receiveData);
					ObjectInputStream ois=new ObjectInputStream(bais);
					int port = receivePacket.getPort();
					sentence=(String)ois.readObject();
					splitSentence= sentence.split(" ");
					port=port-1;
					if(splitSentence[0]!=null)
						{
							if(splitSentence[0].trim().equalsIgnoreCase("v"))
							{
								System.out.println("Received verification Msg in Node "+lsnode.nodePort+" from Node "+port);
								verificationFlag = lsnode.updateNodeStatus(splitSentence[1].trim(),splitSentence[2].trim());
								lsnode.setReceiveOnceValue(true);
								if(verificationFlag==0)
								{
									System.out.println("Link failure: "+"Node "+lsnode.nodePort+" cannot reach Node "+splitSentence[1]+" exiting program");
									lsnode.crash=true;
								}
								if(verificationFlag==-1)
								{
									int distance=0;
									for(int i=0;i<lsnode.nodes.size();i++)
									{
										if(lsnode.nodes.get(i).portNo.equalsIgnoreCase(splitSentence[1]))
												distance=Integer.parseInt(lsnode.nodes.get(i).distance);
									}
									System.out.println("Link distance from Node "+lsnode.nodePort+" to "+splitSentence[1]+" is "+distance+"\nLink distance from Node "+splitSentence[1]+" to Node "+lsnode.nodePort+" is "+splitSentence[2]+"\nLink distance does not match, exiting program");
									lsnode.crash=true;
								}	
							}
							if(splitSentence[0].trim().equalsIgnoreCase("c"))
							{
								lsnode.crash=true;
								lsnode.crashNode=Integer.parseInt(splitSentence[1]);
							}
							if(splitSentence[0].trim().equalsIgnoreCase("l"))
							{	
								System.out.println("Received Link State in Node "+lsnode.nodePort+" from Node "+port);
								lsnode.countReceiveLinkState=1;
								lsnode.initiateTime();
								lsnode.updateAdjMatrix(splitSentence);
							}
							//Sets parameters for msfg propagation that the Send thread uses for sending 
							if(splitSentence[0].equalsIgnoreCase("m"))
							{
								lsnode.msg=sentence;
								lsnode.msgSet=true;
								lsnode.receiveMsgNode=port;
								sentence="";	
							}
							if(splitSentence[0].equalsIgnoreCase("e"))
							{
								lsnode.terminateProgram=true;
							}
						}
				}
			}
			catch(Exception E)
			{
				System.out.println("Error while receiving"+E);
			}
		}
	}

