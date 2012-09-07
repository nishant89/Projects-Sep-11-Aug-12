import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.HashMap;

public class HeapFile {
	static HashMap<String,Write> hm=new HashMap<String,Write>();		//used for writing
	static HashMap<String,Read> hr=new HashMap<String,Read>();			//used for reading
	static HashMap<String,Convert> hc=new HashMap<String,Convert>();	//used for general conversion
	static HashMap<String,Compare> hcom=new HashMap<String,Compare>();	//used for comparing
	static HashMap<String,Hash> hh = new HashMap<String,Hash>();
	static long startTime;
	static long endTime;
	// Initializing the HashMaps to provide indirection rather than using nested if-else to determine data type
	static void initialize ()
	{
		startTime = (Calendar.getInstance()).getTimeInMillis();
		hm.put("i1",new MyByte());
		hm.put("i2",new MyShort());
		hm.put("i4", new MyInt());
		hm.put("i8",new MyLong());
		hm.put("r4",new MyFloat());
		hm.put("r8",new MyDouble());
		hm.put("c", new MyChar());
		hr.put("i1",new MyByte());
		hr.put("i2",new MyShort());
		hr.put("i4", new MyInt());
		hr.put("i8",new MyLong());
		hr.put("r4",new MyFloat());
		hr.put("r8",new MyDouble());
		hr.put("c", new MyChar());
		hc.put("i1",new MyByte());
		hc.put("i2",new MyShort());
		hc.put("i4", new MyInt());
		hc.put("i8",new MyLong());
		hc.put("r4",new MyFloat());
		hc.put("r8",new MyDouble());
		hc.put("c", new MyChar());
		hcom.put("i1",new MyByte());
		hcom.put("i2",new MyShort());
		hcom.put("i4", new MyInt());
		hcom.put("i8",new MyLong());
		hcom.put("r4",new MyFloat());
		hcom.put("r8",new MyDouble());
		hcom.put("c", new MyChar());
		hh.put("i1",new MyByte());
		hh.put("i2",new MyShort());
		hh.put("i4", new MyInt());
		hh.put("i8",new MyLong());
		hh.put("r4",new MyFloat());
		hh.put("r8",new MyDouble());
		hh.put("c", new MyChar());
	}
	
	// This function is used to insert a single record in an existing heap file
	
	long insertRecord(File heapFile, String []inputHeader, String []value)
	{
		long recordID;
		try{
			RandomAccessFile raf=new RandomAccessFile("heapFile","rw");
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			long noOfRecords;
			int fieldTypeSize;
			int noFieldsHeapFile;
			String fieldType="";
			noOfRecords=Long.parseLong(hr.get("i8").read(raf,0,8));
			noFieldsHeapFile=Integer.parseInt(hr.get("i4").read(raf,8,4));
			fieldTypeSize=Integer.parseInt(hr.get("i4").read(raf,12,4));
			fieldType=fieldType+hr.get("c").read(raf,16,fieldTypeSize);
			String []heapFileHeader=fieldType.split(",");
			boolean schema=checkSchema(inputHeader,heapFileHeader);
			if(schema && inputHeader.length==value.length)
			{	
				StringBuilder hold=new StringBuilder();
				recordID=raf.length();
				long length=raf.length();
				raf.seek(length);
				// length=length+hm.get("i8").write(baos, Long.toString(recordID),length);
				for(int j=0;j<inputHeader.length;j++)
				{
					// if-else only used for char data type since the size of the header character field not decided while initializing the database
					if((inputHeader[j]).charAt(0)=='c')
					{	
						int count=Integer.parseInt((inputHeader[j].substring(1)));
						if(value[j].length()>count)
						{
							char ch[]=value[j].toCharArray();
							for(int c=0;c<count;c++)
							{
								hold.append(ch[c]);
							}
							String finder=inputHeader[j].substring(0, 1);
							length=length+hm.get(finder).write(baos,hold.toString(),length);
						}
						//  Padding or trimming the string to fit into the char data type
						if(value[j].length()<count)
						{
							char ch[]=new char[count];
							int c=0;
							for(c=0;c<value[j].length();c++)
							{
								ch[c]=value[j].charAt(c);
							}
							while(c<count)
							{
								ch[c]=' ';
								c=c+1;
							}
							String finder=inputHeader[j].substring(0, 1);
							length=length+hm.get(finder).write(baos,new String(ch),length);
						}

					}
					else
					{
						length=length+hm.get(inputHeader[j]).write(baos, value[j], length);
					}
				}
				recordID=recordID+length;
				raf.write(baos.toByteArray());	//writing the entire record once the record formed
				baos.close();
				return recordID;
			}
			else
			{
				return -1;
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
			return -1;
		}

	}
	
	// This function is used to perform bulk inserts in a new heap file
	
	void createAndInsert(File outputFile, int []fieldNos)
	{	
		try{
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			RandomAccessFile raf=new RandomAccessFile(outputFile,"rw");
			StringBuilder sb1=new StringBuilder();
			StringBuilder sb2=new StringBuilder();
			long counter = 0;
			Index index=new Index();
			String temp,fieldType;
			long noOfRecords = 0;
			long recordID = 0;
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			String [] inputHeader = null;
			int noFields = 0;
			while((temp=br.readLine())!=null)
			{
				//sb1.append(temp);
				//sb2.append(temp+",");
				counter = counter+1;
//			temp=sb1.toString();
//			String []inputLineSplit=temp.split("\n");
//			temp=sb2.toString();
				if(counter == 1)
				{
					inputHeader = temp.split(",");
					noFields = inputHeader.length;
					//noOfRecords=(inputLineSplit.length-1);
					fieldType = temp;
					recordID=16+fieldType.getBytes().length;
					hm.get("i8").write(baos,Long.toString(noOfRecords),0);							// 1st field in header: no of records
					hm.get("i4").write(baos,Integer.toString(noFields),8);							// 2nd field in header: no of fields in the header
					hm.get("i4").write(baos, Integer.toString(fieldType.getBytes().length),12);		// 3rd field in the header is the column header length
					hm.get("c").write(baos,fieldType,16);
					raf.write(baos.toByteArray());
					baos = new ByteArrayOutputStream();
					
					if(fieldNos!=null && fieldNos.length>0)
					{
						for(int f=0;f<fieldNos.length;f++)
						index.createIndexFiles(outputFile.getName().split("\\.")[0], inputHeader[fieldNos[f]-1], fieldNos[f]);
					}
					
					continue;
				}
				
				String []inputCommaSplit=temp.split(",");
				long length = 0;
				StringBuilder hold=new StringBuilder();
				
				// Create index files
				
				
				
				
				// Inserting records in the heapFile
				
	//			for(int i=noFields;i<=inputCommaSplit.length-noFields;i=i+noFields)
	//			{	
	
					//length=hm.get("i8").write(baos, Long.toString(recordID),length);
					length = 0;
					
					for(int j=0;j<noFields;j++)
					{
						// if-else only for char data type to obtain the length
						
						if((inputHeader[j]).charAt(0)=='c')
						{	
							int count=Integer.parseInt((inputHeader[j].substring(1)));
							
							if(inputCommaSplit[j].length()>=count)
							{
								char ch[]=inputCommaSplit[j].toCharArray();
								for(int c=0;c<count;c++)
								{
									hold.append(ch[c]);
								}
								String finder=inputHeader[j].substring(0, 1);
								length=length+hm.get(finder).write(baos,hold.toString(),length);
								
								for(int f=0; f<fieldNos.length;f++)
								{
									if(j==fieldNos[f]-1)
									{
										index.insertRecord(outputFile.getName().split("\\.")[0], hold.toString(), inputHeader[j],fieldNos[f],recordID);
									}
								}
								
								hold=new StringBuilder();
							}
							
							// padding or trimming the string for using the char data type of limited size
							
							if(inputCommaSplit[j].length()<count)
							{
								char ch[]=new char[count];
								int c=0;
								for(c=0;c<inputCommaSplit[j].length();c++)
								{
									ch[c]=inputCommaSplit[j].charAt(c);
								}
								while(c<count)
								{
									ch[c]=' ';
									c=c+1;
								}
								String finder=inputHeader[j].substring(0, 1);
								length=length+hm.get(finder).write(baos,new String(ch),length);
								for(int f=0; f<fieldNos.length;f++)
								{
									if(j==fieldNos[f]-1)
									{
										index.insertRecord(outputFile.getName().split("\\.")[0], new String(ch), inputHeader[j],fieldNos[f],recordID);
									}
								}
							}
	
						}
						else
						{
							length=length+hm.get(inputHeader[j]).write(baos, inputCommaSplit[j], length);
							for(int f=0; f<fieldNos.length;f++)
							{
								if(j==fieldNos[f]-1)
								{
									index.insertRecord(outputFile.getName().split("\\.")[0], inputCommaSplit[j], inputHeader[j],fieldNos[f],recordID);
								}
							}
						}
					}
					raf.write(baos.toByteArray());		// Writes the entire record
					recordID=recordID+length;
					baos = new ByteArrayOutputStream();
				}
				
				raf.seek(0);
				raf.write(MyLong.toByteArray(counter-1));
				raf.close();				
				baos.close();
				//index.displayIndex(new File("output.3.lht"), new File("output.3.lho"));
		}
		catch(Exception e)
		{
			System.out.println("I am in create and insert");
			System.out.println(e+" Restart program");
			System.exit(1);
		}

	}
	
	// appends records in bulk to an existing heap file
	
	void append(File outputFile, int []fieldNos)
	{
		try{
			Index index = new Index();
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			FileInputStream fis=new FileInputStream(outputFile);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			int isEmpty=fis.read();
			if(isEmpty==-1)
			{
				//createAndInsert(inputFile,outputFile);
				System.out.println("The file is empty");
				fis.close();
				return;
			}
			else
			{	
				fis.close();
				RandomAccessFile raf=new RandomAccessFile(outputFile,"rw");
				String temp,fieldType="";
				int noFieldsHeapFile;
				long noOfRecords=0;
				int fieldTypeSize=0;
				int a = 0;
				raf.seek(0);
				noOfRecords = Long.parseLong(hr.get("i8").read(raf,0,8));
				noFieldsHeapFile = Integer.parseInt(hr.get("i4").read(raf,8,4));
				fieldTypeSize=Integer.parseInt(hr.get("i4").read(raf,12,4));
				fieldType = fieldType+hr.get("c").read(raf,16,fieldTypeSize);
//				System.out.println("Number of records "+noOfRecords+" noOfFields "+noFieldsHeapFile+" fieldType "+fieldType +"fieldTypeSize "+fieldTypeSize);
				String []outputHeader = fieldType.split(",");
				StringBuilder sb1=new StringBuilder();
				StringBuilder sb2=new StringBuilder();
				int indexFields[] = new int [outputHeader.length];
				String []inputHeader = null;
				int counter = 0;
				int noFields = 0;
				long recordID=raf.length();
				boolean schema = false;
				
				while((temp=br.readLine())!=null)
				{
					//sb1.append(temp+"\n");
					//sb2.append(temp+",");
					counter = counter + 1;
				//temp=sb1.toString();
				//String []inputLineSplit=temp.split("\n");
				//temp=sb2.toString();
					
					if(counter == 1)
					{
						inputHeader = temp.split(",");
						noFields=inputHeader.length;
						schema = checkSchema(inputHeader,outputHeader);		// performs schema matching
						raf.seek(raf.length());
						
						createHeapIndex(outputFile,fieldNos);
						
						for(int i = 1; i<=outputHeader.length; i++)
						{
							if(new File(outputFile.getName().split("\\.")[0]+"."+i+".lht").exists())
							{
								indexFields[a++] = i;
							}
						}	
						
						continue;
					}
					
					String []inputCommaSplit=temp.split(",");
					
					if(schema)														
					{	
						baos = new ByteArrayOutputStream();
						long length = 0;
						StringBuilder hold = new StringBuilder();						
						
							length = 0;
							for(int j=0;j<noFields;j++)
							{
								// if-else only for char data type
								if((inputHeader[j]).charAt(0)=='c')
								{	
									int count=Integer.parseInt((inputHeader[j].substring(1)));
									
									if(inputCommaSplit[j].length()>=count)
									{
										char ch[]=inputCommaSplit[j].toCharArray();
										for(int c=0;c<count;c++)
										{
											hold.append(ch[c]);
										}
										String finder=inputHeader[j].substring(0, 1);
										length=length+hm.get(finder).write(baos,hold.toString(),length);
										
										for(int f=0; f<indexFields.length;f++)
										{
											if(j==indexFields[f]-1)
											{
												index.insertRecord(outputFile.getName().split("\\.")[0], hold.toString(), inputHeader[j],indexFields[f],recordID);
											}
										}
										
										hold=new StringBuilder();
									}
									
									// Padding and trimming of string to be used in the char data type of limited size
									if(inputCommaSplit[j].length()<count)
									{
										char ch[]=new char[count];
										int c=0;
										for(c=0;c<inputCommaSplit[j].length();c++)
										{
											ch[c]=inputCommaSplit[j].charAt(c);
										}
										while(c<count)
										{
											ch[c]=' ';
											c=c+1;
										}
										String finder=inputHeader[j].substring(0, 1);
										length=length+hm.get(finder).write(baos,new String(ch),length);
										for(int f=0; f<indexFields.length;f++)
										{
											if(j==indexFields[f]-1)
											{
												index.insertRecord(outputFile.getName().split("\\.")[0], new String(ch), inputHeader[j],indexFields[f],recordID);
											}
										}
									}
	
								}
								else
								{
									length=length+hm.get(inputHeader[j]).write(baos, inputCommaSplit[j], length);
									for(int f=0; f<indexFields.length;f++)
									{
										if(j==indexFields[f]-1)
										{
											index.insertRecord(outputFile.getName().split("\\.")[0], inputCommaSplit[j], inputHeader[j],indexFields[f],recordID);
										}
									}
								}
							}
							raf.write(baos.toByteArray());					//Writes the entire record
							recordID=recordID+length;									
						}
						
					else
					{
						System.out.println("The Schema information does not match restart program with valid file details");
						System.exit(1);
					}
					
				}
				baos.close();
				raf.seek(0);
				raf.write(MyLong.toByteArray(noOfRecords+counter-1));
			}
		}
		catch(Exception e)
		{
			System.out.println("I am in insert");
			System.out.println(e);
		}
	}

	boolean checkSchema(String []inputHeader, String [] outputHeader)
	{
		if(inputHeader.length==outputHeader.length)
		{
			for(int i=0;i<outputHeader.length;i++)
			{
				if(!inputHeader[i].equalsIgnoreCase(outputHeader[i]))
				{
					return false;
				}
			}
		}
		else
			return false;
		return true;
	}
	int getCharNo(String input)
	{
		char[] c=input.toCharArray();
		int k=1;
		int number=0;
		for(int i=c.length-1;i>0;i--)
		{
			number=number+(i-'0')*k;
			k=k*10;
		}
		return number;
	}
	
	// The first time user asks for a cursor with conditions: It returns the first qualifying record
	
	Cursor scanOpen(File heapFile, Condition con,int conditions)			
	{	
		try
		{
			RandomAccessFile raf=new RandomAccessFile(heapFile,"r");
			int fieldTypeSize=Integer.parseInt(hr.get("i4").read(raf,12,4));
			raf.seek(16);
			String []header=(hr.get("c").read(raf,16,fieldTypeSize)).split(",");
			raf.seek(16+fieldTypeSize);
			if(con==null)
			{
				int length=0;
				for(int i=0;i<header.length;i++)
				{
					if(header[i].charAt(0)=='c')
					{
						length+=Integer.parseInt(header[i].substring(1));
					}
					else
						length+=hc.get(header[i]).length;
				}
				byte []temp=new byte[length];
				raf.read(temp, 0,length);
				Cursor cs=new Cursor(raf, temp, header);
				return cs;	
			}
			else
			{
				boolean indexFound = false;
				int fieldNo = 0;
				String fieldValue = "";
				File fileName;
				int length=0;
				long RID = 0;
				Index index = new Index();
				for(int i=0;i<header.length;i++)
				{
					if(header[i].charAt(0)=='c')
					{
						length+=Integer.parseInt(header[i].substring(1));
					}
					else
						length+=hc.get(header[i]).length;
				}
				byte []temp=new byte[length];
				Cursor cs = new Cursor();
				if(!con.opv.isEmpty())
				{
					for(OpValue op:con.opv)
					{
						if(op.field>header.length || op.field<1)
						{
							System.out.println("Selection out of column range, restart program");
							System.exit(1);
						}
						fileName = new File(heapFile.getName().split("\\.")[0]+"."+op.field+"."+"lht");
						if(fileName.exists() && op.op.equalsIgnoreCase("="))
						{
							
							RID = index.searchIndex(heapFile.getName().split("\\.")[0],op.value,op.field,header[op.field-1],0);
							indexFound = true;
							fieldNo = op.field;
							fieldValue = op.value;
							if(RID!=-1)
							{
								raf.seek(RID);
								raf.read(temp,0,length);
								cs=new Cursor(raf, temp, header,RID,heapFile.getName().split("\\.")[0]+"."+op.field+"."+"lht");
								
							}
							else
								return null;
							
						}
						if(indexFound)
						{
							break;
						}
					}
				}
				if(!indexFound)
				{
					raf.read(temp, 0,length);
					cs=new Cursor(raf, temp, header);
				}
				int offset=0;
				boolean checkCondition=false;
				if(!con.opv.isEmpty())
				{
					do{
						inner:for(OpValue i:con.opv)
						{	
							
							if(i.field>cs.recordHeader.length || i.field<1)
							{
								System.out.println("Selection out of column range, restart program");
								System.exit(1);
							}
							
							if(i.field<=header.length)
							{
								checkCondition=false;
								offset=0;
								for(int j=0;j<i.field-1;j++)
								{
									if(header[j].charAt(0)=='c')
										offset+=Integer.parseInt(header[j].substring(1));
									else
										offset+=hc.get(header[j]).length;
								}
								
								// calls the respective compare functions of the My'X' classes
								switch(i.op)
								{
								case "=":
								{	
									if(header[i.field-1].charAt(0)=='c')
									{
										String finder=header[i.field-1].substring(0,1);
										if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))==0)
										{
											checkCondition=true;
										}
									}
									else
									{
										if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))==0)
										{
											checkCondition=true;
										}
									}
									break;
								}
								case "<":
								{
									if(header[i.field-1].charAt(0)=='c')
									{
										String finder=header[i.field-1].substring(0,1);
										if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))<0)
										{
											checkCondition=true;
										}
									}
									else
									{
										if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))<0)
										{
											checkCondition=true;
										}
									}
									break;
								}
								case ">":
								{
									if(header[i.field-1].charAt(0)=='c')
									{
										String finder=header[i.field-1].substring(0,1);
										if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))>0)
										{
											checkCondition=true;
										}
									}
									else
									{
										if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))>0)
										{
											checkCondition=true;
										}
									}
									break;
								}
								case "<=":
								{
									if(header[i.field-1].charAt(0)=='c')
									{
										String finder=header[i.field-1].substring(0,1);
										if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))<=0)
										{
											checkCondition=true;
										}
									}
									else
									{
										if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))<=0)
										{
											checkCondition=true;
										}
									}
									break;
								}
								case ">=":
								{
									if(header[i.field-1].charAt(0)=='c')
									{
										String finder=header[i.field-1].substring(0,1);
										if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))>=0)
										{
											checkCondition=true;
										}
									}
									else
									{
										if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))>=0)
										{
											checkCondition=true;
										}
									}
									break;
								}
								case "<>":
								{
									if(header[i.field-1].charAt(0)=='c')
									{
										String finder=header[i.field-1].substring(0,1);
										if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))!=0)
										{
											checkCondition=true;
										}
									}
									else
									{
										if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))!=0)
										{
											checkCondition=true;
										}
									}
									break;
								}
								default:
								{
									checkCondition=true;
								}
								}
								if(checkCondition)
								{
									continue inner;
								}
								else
								{
									break;
								}
							}
						}
					if(checkCondition)
					{
						return cs;
					}
					if(indexFound)
					{
						RID = index.searchIndex(heapFile.getName().split("\\.")[0],fieldValue,fieldNo,header[fieldNo-1],cs.RID);
						if(RID!=-1)
						{
							raf.seek(RID);
							raf.read(temp,0,length);
							cs=new Cursor(raf, temp, header,RID,cs.fileName);
						}
						else
							cs = null;
					}
					if(cs.raf.getFilePointer()<=cs.raf.length()-cs.recordInBytes.length && !indexFound)
					{
						cs.raf.seek(cs.raf.getFilePointer());
						cs.raf.read(temp,0,cs.recordInBytes.length);
						Cursor cs1=new Cursor(cs.raf,temp,cs.recordHeader);
						cs=cs1;
					}
					else
						cs = null;
					

					}while(cs!=null);
					
					return cs;
				}
				else
					return cs;
			}

		}
		catch(Exception e)
		{
			System.out.println("I am in scan open ");
			System.out.println(e);
			return null;
		}	

	}
	
	// Returns the next qualifying record when user requests for the next record with conditions 
	
	public Cursor scanNext(Cursor cs,Condition con, int conditions)
	{
		if(cs.raf==null)
		{
			return null;
		}
		else
		{
			if(con==null)
			{
				byte temp[]=new byte[cs.recordInBytes.length];
				try
				{
					if(cs.raf.getFilePointer()<=cs.raf.length()-cs.recordInBytes.length)
					{
						cs.raf.seek(cs.raf.getFilePointer());
						cs.raf.read(temp,0,cs.recordInBytes.length);
						Cursor cs1=new Cursor(cs.raf,temp,cs.recordHeader);
						return cs1;
					}
					else
						return null;
				}
				catch(Exception e)
				{
					System.out.println(e);
					return null;
				}
			}
			else
			{
				int length=0;
				int indexField = 0;
				long RID = 0;
				boolean indexFound = false;
				int fieldNo = 0;
				Index index = new Index();
				String fieldValue = "";
				String []header=cs.recordHeader;
				if(!cs.fileName.equalsIgnoreCase(""))
				{
					String []t = cs.fileName.split("\\.");
					indexField = Integer.parseInt(t[1]);
				}
				else
					indexField = 0;
				for(int i=0;i<header.length;i++)
				{
					if(header[i].charAt(0)=='c')
					{
						length+=Integer.parseInt(header[i].substring(1));
					}
					else
						length+=hc.get(header[i]).length;
				}
				byte []temp=new byte[length];
				
				try
				{
					if(!con.opv.isEmpty())
					{
						for(OpValue op:con.opv)
						{
							if(op.field>header.length || op.field<1)
							{
								System.out.println("Selection out of column range, restart program");
								System.exit(1);
							}
							if(indexField == op.field && op.op.equalsIgnoreCase("=") && !cs.fileName.equalsIgnoreCase(""))
							{
								RID = index.searchIndex(cs.fileName.split("\\.")[0],op.value,op.field,header[op.field-1],cs.RID);
								indexFound = true;
								fieldNo = op.field;
								fieldValue = op.value;
								if(RID!=-1)
								{
									cs.raf.seek(RID);
									cs.raf.read(temp,0,length);
									cs = new Cursor(cs.raf, temp, header,RID,cs.fileName);
									
								}
								else
									return null;	
							}
						}
					}
					
						if(!indexFound)
						{
							if(cs.raf.getFilePointer()<=cs.raf.length()-cs.recordInBytes.length)
							{
								cs.raf.seek(cs.raf.getFilePointer());	
								cs.raf.read(temp,0,length);
								Cursor csTemp=new Cursor(cs.raf,temp,cs.recordHeader);
								cs=csTemp;
							}
							else
							{
								return null;
							}
						}
						if(!con.opv.isEmpty())
						{
							int offset=0;
							boolean checkCondition=false;
							outer:do{
								inner:for(OpValue i:con.opv)
								{	
									if(i.field>cs.recordHeader.length || i.field<1 )
									{
										System.out.println("Selection out of column range, restart program");
										System.exit(1);
									}
									checkCondition=false;
									offset=0;
									for(int j=0;j<i.field-1;j++)
									{
										if(header[j].charAt(0)=='c')
											offset+=Integer.parseInt(header[j].substring(1));
										else
											offset+=hc.get(header[j]).length;
									}

									switch(i.op)
									{
									case "=":
									{
										if(header[i.field-1].charAt(0)=='c')
										{
											String finder=header[i.field-1].substring(0,1);
											if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))==0)
											{
												checkCondition=true;
											}
										}
										else
										{
											if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))==0)
											{
												checkCondition=true;
											}
										}
										break;
									}
									case "<":
									{
										if(header[i.field-1].charAt(0)=='c')
										{
											String finder=header[i.field-1].substring(0,1);
											if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))<0)
											{
												checkCondition=true;
											}
										}
										else
										{
											if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))<0)
											{
												checkCondition=true;
											}
										}
										break;
									}
									case ">":
									{
										if(header[i.field-1].charAt(0)=='c')
										{
											String finder=header[i.field-1].substring(0,1);
											if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))>0)
											{
												checkCondition=true;
											}
										}
										else
										{
											if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))>0)
											{
												checkCondition=true;
											}
										}
										break;
									}
									case "<=":
									{
										if(header[i.field-1].charAt(0)=='c')
										{
											String finder=header[i.field-1].substring(0,1);
											if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))<=0)
											{
												checkCondition=true;
											}
										}
										else
										{
											if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))<=0)
											{
												checkCondition=true;
											}
										}
										break;
									}
									case ">=":
									{
										if(header[i.field-1].charAt(0)=='c')
										{
											String finder=header[i.field-1].substring(0,1);
											if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))>=0)
											{
												checkCondition=true;
											}
										}
										else
										{
											if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))>=0);
											{
												checkCondition=true;
											}
										}
										break;
									}
									case "<>":
									{
										if(header[i.field-1].charAt(0)=='c')
										{
											String finder=header[i.field-1].substring(0,1);
											if((hcom.get(finder)).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))!=0)
											{
												checkCondition=true;
											}
										}
										else
										{
											if(hcom.get(header[i.field-1]).compare(cs.recordInBytes, offset,i.value,Integer.parseInt(header[i.field-1].substring(1)))!=0)
											{
												checkCondition=true;
											}
										}
										break;
									}
									default:
									{
										checkCondition=false;
									}
									}
									if(checkCondition)
									{
										continue inner;
									}
									else
									{
										break;
									}
								}
							if(checkCondition)
							{
								return cs;
							}
							if(indexFound)
							{
								RID = index.searchIndex(cs.fileName.split("\\.")[0],fieldValue,fieldNo,header[fieldNo-1],cs.RID);
								if(RID!=-1)
								{
									cs.raf.seek(RID);
									cs.raf.read(temp,0,length);
									cs=new Cursor(cs.raf, temp, header,RID,cs.fileName);
									
								}
								else
									return null;
							}
							if(cs.raf.getFilePointer()<=cs.raf.length()-cs.recordInBytes.length && !indexFound)
							{
								cs.raf.seek(cs.raf.getFilePointer());
								cs.raf.read(temp,0,cs.recordInBytes.length);
								Cursor cs1=new Cursor(cs.raf,temp,cs.recordHeader);
								cs=cs1;
							}
							else
							{
								cs = null;
							}
							
							}while(cs!=null);
							
							return cs;
						}
						else
						{
							return cs;
						}
				}
				catch(Exception e)
				{
					System.out.println("I am in scan next");
					System.out.println(e);
					return null;
				}
			}
		}
	}
	
	void createHeapIndex (File heapFile, int []fieldNo)
	{
		try
		{
		RandomAccessFile raf = new RandomAccessFile(heapFile, "r");
		Index in = new Index();
		int length = 0;
		int []offset = new int[fieldNo.length];
		byte []forLong = new byte [8];
		byte []forInt = new byte [4];
		int recordLength = 0;
		long noOfRecords = 0;
		long noOfFields = 0;
		int headerFieldTypeSize = 0;
		int []fieldSize = new int[fieldNo.length];
		String value = "";
		raf.read(forLong,0,8);
		noOfRecords = MyLong.toLong(forLong);
		raf.read(forInt,0,4);
		noOfFields = MyInt.toInt(forInt);
		raf.read(forInt,0,4);
		headerFieldTypeSize = MyInt.toInt(forInt);
		byte []fieldHeader = new byte[headerFieldTypeSize];
		raf.read(fieldHeader,0,headerFieldTypeSize);
		String []header;
		header = (new String(fieldHeader)).split(",");
		long RID = 16+headerFieldTypeSize;
		for(int n=0; n<fieldNo.length ;n++)
		{
			if(fieldNo[n]>header.length)
			{
				System.out.println("Field does not exits in heapfile, cannot create index");
				System.exit(1);
			}
			fieldSize[n] = Integer.parseInt(header[fieldNo[n]-1].substring(1));
			
			in.createIndexFiles(heapFile.getName().split("\\.")[0],header[fieldNo[n]-1],fieldNo[n]);
			
			length = 0;
			
			for(int i=0;i<header.length;i++)
			{
				if(i == fieldNo[n]-1)
					offset[n] = length;
				length+=Integer.parseInt(header[i].substring(1));
			}
		}
		byte []forRecord = new byte[length];
		for(int i=0; i<noOfRecords;i++)
		{
			RID = raf.getFilePointer();
			raf.read(forRecord,0,length);
			for(int n=0; n<fieldNo.length;n++)
			{
				if(header[fieldNo[n]-1].charAt(0)=='c')
				{
					HeapFile.hc.get("c").length = Integer.parseInt(header[fieldNo[n]-1].substring(1));
					value = HeapFile.hc.get("c").convert(forRecord, offset[n]);
				}
				else
					value = HeapFile.hc.get(header[fieldNo[n]-1]).convert(forRecord, offset[n]);
				
				in.insertRecord(heapFile.getName().split("\\.")[0], value, header[fieldNo[n]-1], fieldNo[n], RID);
			}	
		}
		raf.close();
		}
		catch (Exception e)
		{
			System.out.println("I am in create index from heap "+e+", restart program");
			System.exit(1);
		}
		
		
	}
	
	public static void main(String[] args) {
		HeapFile hf=new HeapFile();
		initialize();
		boolean checkInput=true;
		FileWriter fw=null;
		
		// The if-else chains here are only for validation of the input and not used for reading, writing or comparing the records 
		
		
		// Only heap file input
		
		if(args.length==1 && args[0]!=null)
		{	
			checkInput=false;
			File heapFile;
			if(args[0].split("\\.").length >1 && args[0].split("\\.")[1].equalsIgnoreCase("hf"))
				heapFile=new File(args[0]);
			else
				heapFile=new File(args[0]+".hf");
			int count1=0;
			if(heapFile.isFile())
			{
				checkInput=false;
				StringBuilder sb=new StringBuilder();
				StringBuilder sb1=new StringBuilder();
				Cursor cs=hf.scanOpen(new File(args[0]),null,0);
				if(cs!=null)
				{
					for(int i=0;i<cs.recordHeader.length;i++)
					{
						sb1.append(cs.recordHeader[i]);
						if(i==cs.recordHeader.length-1)
							sb1.append("\r\n");
						else
							sb1.append(",");
					}
					do{
						int length=0;
						for(int i=0;i<cs.recordHeader.length;i++)
						{
							if(cs.recordHeader[i].charAt(0)=='c')
							{
								hc.get("c").length=Integer.parseInt(cs.recordHeader[i].substring(1));
								sb.append(hc.get("c").convert(cs.recordInBytes,length).trim());
								length+=Integer.parseInt(cs.recordHeader[i].substring(1));
								if(i==cs.recordHeader.length-1)
									sb.append("\r\n");
								else
									sb.append(",");
							}
							else
							{
								sb.append(hc.get(cs.recordHeader[i]).convert(cs.recordInBytes,length));
								length+=hc.get(cs.recordHeader[i]).length;
								if(i==cs.recordHeader.length-1)
									sb.append("\r\n");
								else
									sb.append(",");
							}
						}
						if(count1==0)
						{
							System.out.println(sb1.toString()+sb.toString());
						}
						else
							System.out.println(sb.toString());
						count1++;
						sb=new StringBuilder();
						cs=hf.scanNext(cs,null,0);
					}while(cs!=null);
				}
				else
				{
					System.out.println("Restart program and enter a valid input file");
					System.exit(1);
				}
			}
		}
		
		// Displaying an index file and it's overflow file
		
		if(args.length == 3 && args[0].equalsIgnoreCase("display"))
		{
			checkInput = false;
			if(new File(args[1]).exists() && new File(args[2]).exists() && args[1].split("\\.")[2].equalsIgnoreCase("lht") && args[2].split("\\.")[2].equalsIgnoreCase("lho"))
			{
				Index in = new Index();
				in.displayIndex(new File(args[1]), new File(args[2]));
			}
		}
		
		
		// if only the heap file and the indices
		
		if(args.length>1 && args[0]!=null && args[1].length() > 2 && args[1].substring(0,2).equalsIgnoreCase("-b") && checkInput)
		{
			
			checkInput = false;
			int []fields = new int[args.length-1];
			int j=0;
			for(int i=1; i<args.length;i++)
			{
				if(args[i].length() > 2 && args[i].substring(0,2).equalsIgnoreCase("-b") )
				{
					fields[j] = Integer.parseInt(args[i].substring(2));
					j = j+1;
				}
			}
			if(args[0].split("\\.").length >1 && args[0].split("\\.")[1].equalsIgnoreCase("hf"))
				hf.createHeapIndex(new File(args[0]),fields);
			else
				hf.createHeapIndex(new File(args[0]+".hf"),fields);
		}
		
		
		// If only the heap file and the output file
		
		if(args.length==3 && args[1].equalsIgnoreCase(">") && !args[0].substring(0,2).equalsIgnoreCase("-s") && !args[0].substring(0,2).equalsIgnoreCase("-p"))
		{
			checkInput=false;
			File heapFile;
			if(args[0].split("\\.").length >1 && args[0].split("\\.")[1].equalsIgnoreCase("hf"))
				heapFile=new File(args[0]);
			else
				heapFile=new File(args[0]+".hf");
			int count=0;
			try
			{
			FileWriter f=new FileWriter(new File(args[2]));		
				if(heapFile.isFile())
				{
					checkInput=false;
					StringBuilder sb=new StringBuilder();
					StringBuilder sb1=new StringBuilder();
					Cursor cs=hf.scanOpen(new File(args[0]),null,0);
					if(cs!=null)
					{		
						for(int i=0;i<cs.recordHeader.length;i++)
						{
							sb1.append(cs.recordHeader[i]);
							if(i==cs.recordHeader.length-1)
							{
								sb1.append("\r\n");
							}
							else
							{
								sb1.append(",");
							}
						}
						do{
							int length=0;
							for(int i=0;i<cs.recordHeader.length;i++)
							{
								if(cs.recordHeader[i].charAt(0)=='c')
								{
									hc.get("c").length=Integer.parseInt(cs.recordHeader[i].substring(1));
									sb.append(hc.get("c").convert(cs.recordInBytes,length).trim());
									length+=Integer.parseInt(cs.recordHeader[i].substring(1));
									if(i==cs.recordHeader.length-1)
										sb.append("\n");
									else
										sb.append(",");
								}
								else
								{
									sb.append(hc.get(cs.recordHeader[i]).convert(cs.recordInBytes,length));
									length+=hc.get(cs.recordHeader[i]).length;
									if(i==cs.recordHeader.length-1)
										sb.append("\n");
									else
										sb.append(",");
								}
							}
							if(count==0)
							{
								f.write(sb1.toString());
							}
							//f.write(sb.toString());
							System.out.print(sb.toString());
							cs=hf.scanNext(cs,null,0);
						}while(cs!=null);
					}
					else
					{
						System.out.println("Restart program and enter a valid input file");
						System.exit(1);
					}
				}
			}
			catch(Exception e)
			{
				System.out.println(e+"Error in file, restart program with valid outputfile");
				System.exit(1);
			}
		}
		
		// Create heap file with at least one index
		
		if(args.length > 2 && args[0]!=null && args[1]!=null && args[1].equalsIgnoreCase("-i") && checkInput)
		{
			checkInput=false;
			
			int []fieldNos = new int[args.length-2];
			boolean entered = false;
			int a = 0;
			
			File outputFile;
					if(args[0].split("\\.").length == 2 && args[0].split("\\.")[1].equalsIgnoreCase("hf"))
						 outputFile=new File(args[0]);
					else
						outputFile=new File(args[0]+".hf");
					
					
					for(int i = 2; i<args.length ;i++)
					{
						if(args[i].substring(0, 2).equalsIgnoreCase("-b"))
						{
							entered = true;
							fieldNos[a++] = Integer.parseInt(args[i].substring(2));
						}
					}
					
					if(!entered)
					{
						System.out.println("Invalid parameters, restart program");
						System.exit(1);
					}
					
						if(outputFile.isFile())
						{
							hf.append(outputFile,fieldNos);
						}
						
						else
							hf.createAndInsert(outputFile,fieldNos);
		}
		
		
	// If appending to the heap file without building any new index
		
		if(args.length == 2 && args[0]!=null && args[1]!=null && args[1].equalsIgnoreCase("-i") && checkInput)
		{
			checkInput=false;
			int []fieldNos = new int[0];
			File outputFile;
					if(args[0].split("\\.").length >1 && args[0].split("\\.")[1].equalsIgnoreCase("hf"))
						 outputFile=new File(args[0]);
					else
						outputFile=new File(args[0]+".hf");
					
						if(outputFile.isFile())
						{
							hf.append(outputFile,fieldNos);
						}
						else
							hf.createAndInsert(outputFile,fieldNos);
		}
		
		
		
		// With select or project conditions 
		
		if(args.length>1 && ((args[0]!=null && args[1]!=null)&&!args[1].equalsIgnoreCase("-i")) && checkInput)
		{
			
			File inputFile;
			if(args[0].split("\\.").length == 2 && args[0].split("\\.")[1].equalsIgnoreCase("hf"))
				 inputFile=new File(args[0]);
			else
				inputFile=new File(args[0]+".hf");
			
			Condition con=new Condition();
			boolean isOutputFile=false;
			File outputFile=null;
			int headerCount=0;
			if(inputFile.isFile())
			{
				for(int i=1;i<args.length;i++)
				{
						
						if(args[i].length()>2)
						{
							if(args[i].charAt(0)=='-' && args[i].charAt(1)=='s')
							{
								if(i<args.length-1)
								{
									if(args[i+1].equalsIgnoreCase("<")||args[i+1].equalsIgnoreCase(">")||args[i+1].equalsIgnoreCase("=")||args[i+1].equalsIgnoreCase("<>"))
									{
										con.opv.add(new OpValue(Integer.parseInt(args[i].substring(2)),args[i+1],args[i+2]));
										checkInput=false;
									}
									else
									{
										System.out.println("Invalid operator, restart program");
										System.exit(1);
									}
								}	
								else
								{
									System.out.println("Invalid input parameters, restart program");
									System.exit(1);
								}
							}
						
							if(args[i].charAt(0)=='-' && args[i].charAt(1)=='p')
							{
								con.prov.add(new ProValue(Integer.parseInt(args[i].substring(2))));
								checkInput=false;
							}
						}
						if(args[i-1].length()>2 && i<args.length-1)
						{
							if(args[i].equalsIgnoreCase(">") && !args[i-1].substring(0,2).equalsIgnoreCase("-s"))
							{
								isOutputFile=true;
								outputFile=new File(args[i+1]);
								checkInput=false;
								try
								{
									fw=new FileWriter(outputFile);
								}
								catch(Exception e)
								{
									System.out.println(e+" I/O exception please check output file and restart program");
									System.exit(1);
								}
							}
						}
				}
				Cursor cs=hf.scanOpen(inputFile,con,con.opv.size());
				if(cs!=null && !checkInput)
				{	
					StringBuilder sb1=new StringBuilder();
					StringBuilder sb2=new StringBuilder();
					StringBuilder sb3=new StringBuilder();
					StringBuilder sb4=new StringBuilder();
						for(int i=0;i<cs.recordHeader.length;i++)
						{
							sb3.append(cs.recordHeader[i]);
							if(i==cs.recordHeader.length-1)
							{
								sb3.append("\r\n");
							}
							else
							{
								sb3.append(",");
							}
						}
					do{
						int length=0;
						for(int i=0;i<cs.recordHeader.length;i++)
						{
							if(cs.recordHeader[i].charAt(0)=='c')
							{
								hc.get("c").length=Integer.parseInt(cs.recordHeader[i].substring(1));
								sb1.append(hc.get("c").convert(cs.recordInBytes,length).trim());
								length+=Integer.parseInt(cs.recordHeader[i].substring(1));
								if(i==cs.recordHeader.length-1)
									sb1.append("\n");
								else
									sb1.append(",");
							}
							else
							{
								sb1.append(hc.get(cs.recordHeader[i]).convert(cs.recordInBytes,length));
								length+=hc.get(cs.recordHeader[i]).length;
								if(i==cs.recordHeader.length-1)
									sb1.append("\r\n");
								else
									sb1.append(",");
							}
						}
						if(!con.prov.isEmpty())
						{
							String [] lineSplit=sb1.toString().split(",");
							String []headerSplit=sb3.toString().split(",");
							for(int p=0;p<con.prov.size();p++)
							{
								if(con.prov.get(p).field<=lineSplit.length && con.prov.get(p).field>0)
								{
									sb2.append(lineSplit[con.prov.get(p).field-1]);
									sb4.append(headerSplit[con.prov.get(p).field-1]);
									
								}
								else
								{
									System.out.println("Projection out of column range, restart program");
									System.exit(1);
								}
								if(p==con.prov.size()-1)
									{
									sb2.append("\r\n");
									sb4.append("\r\n");
									}
								else
									{
									sb2.append(",");
									sb4.append(",");
									}
							}
							if(isOutputFile)
							{	
								try
								{	
									if(headerCount==0)
									{
									fw.write(sb4.toString());
									}
									fw.write(sb2.toString());
								}
								catch(Exception e)
								{
									System.out.println(e+" I/O exception please check output file and restart program");
									System.exit(1);
								}
							}
							else
							{	
								if(headerCount==0)
								{
									System.out.print(sb4.toString());
								}
								System.out.println(sb2.toString());
							}
							sb2=new StringBuilder();
							sb1=new StringBuilder();
						}
						else
						{	
							if(isOutputFile)
							{	
								try
								{	
									if(headerCount==0)
										{
											fw.write(sb3.toString());
										}
									fw.write(sb1.toString());
								}
								catch(Exception e)
								{
									System.out.println(e+" I/O exception please check output file and restart program");
									System.exit(1);
								}
							}
							else
								{
									if(headerCount==0)
									{
										System.out.print(sb3.toString());
									}
									System.out.println(sb1.toString());
								}
							sb1=new StringBuilder();
						}
						cs=hf.scanNext(cs,con,con.opv.size());
						headerCount++;
					}while(cs!=null);
						if(isOutputFile)
							{
								try
								{
								fw.close();
								}
								catch(Exception e)
								{
									System.out.println("Invalid parameters, error in closing the file, restart the program");
									System.exit(1);
								}
							}
				}
			}
			else
			{
				System.out.println("Enter a valid heapFile and restart ");
				System.exit(1);
			}
			
		}
		
		// If no valid input
		
		if(checkInput)
		{
			System.out.println("Restart program and enter valid input parameters");
			System.exit(1);
		}
		
		endTime = Calendar.getInstance().getTimeInMillis();
		
		System.out.println("Time taken = "+Long.toString(endTime-startTime));
		
	}	
}
