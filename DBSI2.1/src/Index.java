import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class Index {
	int bucketSize = 73;	
	Index()
	{}
	Index(int size)
	{
		bucketSize=size;
	}
	void createIndexFiles(String heapFileName,String field,int fieldNo)
	{
		try{
			int fieldHeaderSize = 0;
			int fieldSize = 0;
			if(bucketSize < 8)
			{
				System.out.println("BucketSize has to be atleast 8 bytes in size, restart program with valid bucketSize");
				System.exit(1);
			}
			byte []bucket = new byte[bucketSize-8];
			for(int i=0;i<bucket.length;i++)
			{
				bucket[i] = 0;
			}
			if(field.charAt(0)=='c')
				fieldSize = Integer.parseInt(field.substring(1));
			else
				fieldSize=HeapFile.hc.get(field).length;
			if(fieldSize > bucketSize-16)
			{
				System.out.println("Cannot create index on the field as fieldSize greater than bucketsize, restart program with valid fieldSize");
				System.exit(1);
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			String index = heapFileName+"."+Integer.toString(fieldNo)+"."+"lht";
			String indexOverflow = heapFileName+"."+Integer.toString(fieldNo)+"."+"lho";
			File mainIndex=new File(index);
			File overflowIndex = new File(indexOverflow);
			RandomAccessFile ri = new RandomAccessFile(mainIndex,"rw");
			RandomAccessFile ro = new RandomAccessFile(overflowIndex,"rw");
			fieldHeaderSize=field.getBytes().length;
			baos.write(MyLong.toByteArray(0));						//Level
			baos.write(MyLong.toByteArray(0));						//Next
			baos.write(MyInt.toByteArray(bucketSize));				//bucketSize
			baos.write(MyInt.toByteArray(fieldSize));				//fieldSize
			baos.write(MyInt.toByteArray(fieldHeaderSize));			//fieldHeaderSize
			baos.write(field.getBytes());							//fieldHeader
			baos.write(MyInt.toByteArray(fieldNo));					//fieldNo
			baos.write(bucket);										//Initialize bucket with zeros
			baos.write(MyLong.toByteArray(-1));						//Next pointer to -1
			ri.write(baos.toByteArray());
			baos.flush();
			baos.close();
			ro.write(MyLong.toByteArray(-1));						//Overflow bucket freeList = -1
			ri.close();
			ro.close();	

		}
		catch(Exception e)
		{
			System.out.println("I am in create index files "+e+", restart program");
			System.exit(0);
		}

	}
	boolean checkBucket(byte []bucket, int fieldSize)
	{
		int i = 0;
		long temp = 0;
		for(i=0;i<bucket.length-8;i+=fieldSize+8)
		{
			temp = MyLong.toLong(Arrays.copyOfRange(bucket, i, i+8));
			if(temp==0)
				break;
		}
		if(i<=(bucket.length-8-8-fieldSize))
			return false;
		else
			return true;
	}
	byte [] insertIntoBucket(byte []bucket,byte []fieldRecord,long RID)
	{	
		int i = 0;
		int j = 0;
		boolean found = false;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			for(i=0;i<=bucket.length-8-8-fieldRecord.length;i+=fieldRecord.length+8)
			{
				if(MyLong.toLong(Arrays.copyOfRange(bucket,i,i+8))==0)
				{
					found = true;
					break;
				}		
			}
			if(found)
			{
				for(j=0;j<i;j++)
				{
					baos.write(bucket[j]);
				}
				baos.write(MyLong.toByteArray(RID));
				baos.write(fieldRecord);
				for(j=i+fieldRecord.length+8;j<bucket.length;j++)
					baos.write(bucket[j]);
			}
		}
		catch(Exception e)
		{
			System.out.println("I am in insertIntoBucket, exception: "+e+", restart program");
			System.exit(1);
		}
		if(found)
			return baos.toByteArray();
		else
			return bucket;
	}
	long getOverflowBucket(RandomAccessFile ro, int bSize)
	{
		long RID=8;
		try
		{
			byte []forLong = new byte[8];
			byte []forBucket = new byte[bSize-8];
			int i = 0;
			ro.seek(0);
			ro.read(forLong,0,8);
			if(MyLong.toLong(forLong)==-1)
			{
				ro.seek(ro.length());
				RID = ro.length();
				for(i=0;i<bSize-8;i++)
				{
					forBucket[i]=0;
				}
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				baos.write(forBucket);
				baos.write(MyLong.toByteArray(-1));
				ro.write(baos.toByteArray());
			}
			else
			{
				RID=MyLong.toLong(forLong);
				ro.seek(RID+bSize-8);
				ro.read(forLong,0,8);
				ro.seek(0);
				ro.write(forLong);
			}	
		}
		catch ( Exception e )
		{
			System.out.println("I am in getOverflowBucket "+e+", restart program");
			System.exit(1);
		}
		return RID;
	}
	byte [] updateNextPointer(byte []bucket,long pointer)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			baos.write(Arrays.copyOfRange(bucket,0,bucket.length-8));
			baos.write(MyLong.toByteArray(pointer));
		}
		catch ( Exception e )
		{
			System.out.println("I am in updateNextPointer "+e+", restart program");
			System.exit(1);
		}
		return baos.toByteArray();
	}

	void extract (ArrayList<IndexRecord> main, byte []extract, int fieldSize)
	{	
		int i = 0;
		long temp = 0;
		for(i = 0; i<=extract.length-8-8-fieldSize ; i+=fieldSize+8)
		{
			temp = MyLong.toLong(Arrays.copyOfRange(extract,i,i+8));
			if(temp!=0)
			{
				IndexRecord indexRecord= new IndexRecord(fieldSize);
				indexRecord.field = Arrays.copyOfRange(extract,i+8,i+8+fieldSize);
				indexRecord.RID = temp;
				main.add(indexRecord);
			}
		}
	}

	byte [] bucketInitialization (byte []bucket, int bSize )
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try
		{
			for(int i=0; i < bucket.length-8; i++)
				baos.write(0);
			baos.write(MyLong.toByteArray(-1));
		}
		catch(Exception e)
		{
			System.out.println("I am in bucket initialization "+e+", restart program");
			System.exit(1);
		}
		return baos.toByteArray();

	}

	void deallocateOverflowBucket (RandomAccessFile ro, long position, int bSize)
	{
		try
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte []emptyBucket = new byte[bSize];
			byte []freeList = new byte[8];
			long freeListValue = -1;
			bucketInitialization(emptyBucket, bSize);
			ro.seek(0);
			ro.read(freeList,0,8);
			freeListValue = MyLong.toLong(freeList);
			baos.write(Arrays.copyOfRange(emptyBucket,0,bSize-8));
			baos.write(MyLong.toByteArray(freeListValue));
			emptyBucket = baos.toByteArray();
			ro.seek(0);
			ro.write(MyLong.toByteArray(position));
			ro.seek(position);
			ro.write(emptyBucket);
		}
		catch (Exception e)
		{
			System.out.println("I am in deallocate "+e+", restart program");
			System.exit(1);
		}
	}
	long searchIndex(String heapFile, String value, int fieldNo,String fieldHeader,long RID)
	{	
		long rid = -1;
		try
		{
			RandomAccessFile ri = new RandomAccessFile(new File(heapFile+"."+fieldNo+"."+"lht"),"r");
			RandomAccessFile ro = new RandomAccessFile(new File(heapFile+"."+fieldNo+"."+"lho"),"r");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ArrayList<IndexRecord> extract = new ArrayList<IndexRecord>();
			if(RID == -1)
			{
				return -1;
			}
			boolean found = false;
			byte []forLong = new byte [8];
			byte []forInt = new byte [4];
			long bucketNo = 0;
			int bSize = 0;
			int fieldSize = 0;
			int fieldHeaderSize = 0;
			long overflowPointer = -1;
			ri.read(forLong,0,8);
			long level = MyLong.toLong(forLong);
			ri.read(forLong,0,8);
			long next = MyLong.toLong(forLong);
			ri.read(forInt,0,4);
			bSize = MyInt.toInt(forInt);
			byte [] forBucket = new byte [bSize];
			ri.read(forInt,0,4);
			fieldSize = MyInt.toInt(forInt);
			ri.read(forInt,0,4);
			fieldHeaderSize = MyInt.toInt(forInt);
			if(fieldHeader.charAt(0)=='c')
			{
				baos.write(value.getBytes());
			}
			else
				HeapFile.hm.get(fieldHeader).write(baos,value,0);
			long hv = hash(fieldHeader,baos.toByteArray());
			bucketNo = hv%(long)Math.pow(2,level);
			if(bucketNo < next)
				bucketNo = hv%(2*(long)Math.pow(2,level));
			ri.seek(32+fieldHeaderSize+(bucketNo*bSize));
			ri.read(forBucket,0,bSize);
			
			do
			{
				found = false;
				overflowPointer = MyLong.toLong(Arrays.copyOfRange(forBucket,forBucket.length-8,forBucket.length));
				extract(extract,forBucket,fieldSize);
				for(IndexRecord i:extract)
				{
					
					if(i.RID!=RID && i.RID > RID && fieldHeader.charAt(0)!='c' && HeapFile.hcom.get(fieldHeader).compare(i.field,0,value,0) == 0)
					{
						rid = i.RID;
						found = true;
						break;
						
					}
					if(i.RID!=RID && i.RID>RID && fieldHeader.charAt(0)=='c' && HeapFile.hcom.get("c").compare(i.field,0,value,Integer.parseInt(fieldHeader.substring(1))) == 0)
					{
						rid = i.RID;
						found = true;
						break;
					}
				}
				if(overflowPointer!=-1 && !found)
				{
					extract.clear();
					ro.seek(overflowPointer);
					ro.read(forBucket,0,bSize);
				}
			}while(overflowPointer != -1 && !found);
		ri.close();
		ro.close();
		baos.close();
		}
		catch(Exception e)
		{
			System.out.println("I am in index search "+e+" , restart program");
			System.exit(1);
		}
		return rid;	
	}
		boolean isBucketEmpty (byte []bucket)
		{
			for(int i=0; i<bucket.length-8; i++)
			{
				if(bucket[i]!=0)
				{
					return false;
				}
			}
			return true;
		}
		void insertRecord(String heapFileName, String value, String fieldHeader, int fieldNo, long RID)
		{
			try
			{
				File mainIndexFile = new File(heapFileName+"."+Integer.toString(fieldNo)+"."+"lht");
				File overflowIndexFile = new File(heapFileName+"."+Integer.toString(fieldNo)+"."+"lho");
				RandomAccessFile ri = new RandomAccessFile(mainIndexFile,"rw");
				RandomAccessFile ro = new RandomAccessFile(overflowIndexFile,"rw");
				ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
				long level = 0;
				long next = 0;
				int bSize = 0;
				long noOfBuckets = 1;
				long bucketNo=0;
				long hashValue=0;
				boolean isFull = true;
				byte []forLong = new byte[8];
				byte []forInt = new byte[4];
				boolean isOverflow=false;
				int  fieldSize = 0;
				ri.read(forLong, 0, 8);
				level = MyLong.toLong(forLong);
				ri.read(forLong,0,8);
				next = MyLong.toLong(forLong);
				ri.read(forInt,0,4);
				bSize = MyInt.toInt(forInt);
				byte []forBucket = new byte[bSize];
				byte []forOverflowBucket = new byte[bSize];
				noOfBuckets = (long)(Math.pow(2,level))+next;
				if(fieldHeader.charAt(0)=='c')
					baos1.write(value.getBytes());
				else
					HeapFile.hm.get(fieldHeader).write(baos1,value,0);
				fieldSize = baos1.toByteArray().length;
				hashValue = hash(fieldHeader,baos1.toByteArray());
				bucketNo = hashValue%(2*(long)Math.pow(2,level));
				if(bucketNo > noOfBuckets-1)
					bucketNo = bucketNo - (long)Math.pow(2,level);
				//		if(hashValue%noOfBuckets>=next && hashValue%noOfBuckets<noOfBuckets)
				//			bucketNo = hashValue%noOfBuckets;
				//		else
				//			bucketNo = (hashValue)%(2*noOfBuckets);
				ri.seek(32+fieldHeader.getBytes().length+((bucketNo)*bSize));
				ri.read(forBucket,0,bSize);
				isFull = checkBucket(forBucket,baos1.toByteArray().length);
				if(!isFull)
				{
					forBucket = insertIntoBucket(forBucket,baos1.toByteArray(),RID);
					ri.seek(32+fieldHeader.getBytes().length+((bucketNo)*bSize));
					ri.write(forBucket);
				}
				else
				{
					long pointer = MyLong.toLong(Arrays.copyOfRange(forBucket,forBucket.length-8,forBucket.length));

					if(pointer==-1)
					{
						long overflowBucket = getOverflowBucket(ro,bSize);
						isOverflow = true;
						ro.seek(overflowBucket);
						ro.read(forOverflowBucket,0,bSize);
						forOverflowBucket = insertIntoBucket(forOverflowBucket,baos1.toByteArray(),RID);
						ro.seek(overflowBucket);
						ro.write(forOverflowBucket);
						forBucket = updateNextPointer(forBucket, overflowBucket);
						ri.seek(32+fieldHeader.getBytes().length+((bucketNo)*bSize));
						ri.write(forBucket);
					}
					else
					{
						boolean isOverflowFull = true;
						long overflowBucket = MyLong.toLong(Arrays.copyOfRange(forBucket,forBucket.length-8,forBucket.length));
						long prev = overflowBucket;
						while(isOverflowFull && overflowBucket!=-1)
						{
							prev = overflowBucket;
							ro.seek(overflowBucket);
							ro.read(forOverflowBucket,0,bSize);
							isOverflowFull = checkBucket(forOverflowBucket, baos1.toByteArray().length);
							if(isOverflowFull)
								overflowBucket = MyLong.toLong(Arrays.copyOfRange(forOverflowBucket,forOverflowBucket.length-8,forOverflowBucket.length));
						}

						if(!isOverflowFull)
						{
							forOverflowBucket = insertIntoBucket(forOverflowBucket,baos1.toByteArray(),RID);
							ro.seek(overflowBucket);
							ro.write(forOverflowBucket);

						}
						if(isOverflowFull)
						{
							long newOverflowBucket = getOverflowBucket(ro,bSize);
							isOverflow = true;
							ro.seek(prev);
							forOverflowBucket = updateNextPointer(forOverflowBucket,newOverflowBucket);
							ro.write(forOverflowBucket);

							ro.seek(newOverflowBucket);
							ro.read(forOverflowBucket,0,bSize);
							forOverflowBucket = insertIntoBucket(forOverflowBucket,baos1.toByteArray(), RID);
							ro.seek(newOverflowBucket);
							ro.write(forOverflowBucket);
						}	
					}
				}
				if(isOverflow)
				{
					byte []mainBucket = new byte[bSize];
					byte []splitBucket = new byte[bSize];
					byte []extract = new byte[bSize];
					long splitPosition = ri.length();
					long mainPosition = 32+fieldHeader.getBytes().length+(next*bSize);
					long overflowPosition = 0;
					long hv = 0;
					long mainLocationPreviousPointer = 0;
					long splitLocationPreviousPointer = 0;
					boolean checkMainBucket = false;
					boolean checkSplitBucket = false;
					int mainFirst = 0;
					int splitFirst = 0;
					ArrayList<IndexRecord> main = new ArrayList<IndexRecord>();
					ArrayList<IndexRecord> split = new ArrayList<IndexRecord>();
					ArrayList<IndexRecord> original = new ArrayList<IndexRecord>();
					ArrayList<Long> overflowPointers = new ArrayList<Long>();
					ArrayList<IndexRecord> mainRemove = new ArrayList<IndexRecord>();
					ArrayList<IndexRecord> splitRemove = new ArrayList<IndexRecord>();
					//		ArrayList<Long>	overflowPointersRemove = new ArrayList<Long>();

					mainBucket = bucketInitialization (mainBucket, bSize);
					splitBucket = bucketInitialization (splitBucket, bSize);
					extract = bucketInitialization (extract, bSize);

					ri.seek(mainPosition);
					ri.read(extract,0,bSize);
					overflowPosition = MyLong.toLong(Arrays.copyOfRange(extract,bSize-8,bSize));
					extract(original,extract,fieldSize);

					do
					{	
						// deciding whether main or split
						for(IndexRecord ir:original)
						{
							hv = hash(fieldHeader,ir.field);
							//					if(hv%(noOfBuckets+noOfBuckets) >= next && hv%(noOfBuckets+noOfBuckets) < noOfBuckets)
							//					{
							if(hv%(2*(long)Math.pow(2,level)) == next)
							{
								main.add(ir);
								//					original.remove(original.indexOf(ir));
							}
							else
							{
								split.add(ir);
								//					original.remove(original.indexOf(ir));
							}
						}
						original.clear();
						// inserting into the main bucket

						for(IndexRecord ir:main)
						{
							checkMainBucket = checkBucket(mainBucket,fieldSize);
							if(!checkMainBucket)
							{
								mainBucket = insertIntoBucket(mainBucket, ir.field, ir.RID);
								//		main.remove(main.indexOf(ir));
								mainRemove.add(ir);
							}
							else
								break;
						}

						// inserting into the split bucket

						for(IndexRecord ir:split)
						{
							checkSplitBucket = checkBucket(splitBucket,fieldSize);
							if(!checkSplitBucket)
							{
								splitBucket = insertIntoBucket(splitBucket, ir.field, ir.RID);
								//		split.remove(split.indexOf(ir));
								splitRemove.add(ir);
							}
							else
								break;
						}
						for(IndexRecord r:mainRemove)
						{
							main.remove(r);
						}
						for(IndexRecord r:splitRemove)
						{
							split.remove(r);
						}

						mainRemove.clear();				// Helper to keep the main clean
						splitRemove.clear();			// Helper to keep the split clean

						if(checkMainBucket && mainFirst !=0)
						{
							// create an overflow bucket and insert
							long mainOverflow = getOverflowBucket(ro, bSize);
							ro.seek(mainOverflow);
							ro.write(mainBucket);
							if(mainFirst==1)
							{
								ri.seek(mainLocationPreviousPointer);
								ri.write(MyLong.toByteArray(mainOverflow));
							}
							else
							{
								ro.seek(mainLocationPreviousPointer);
								ro.write(MyLong.toByteArray(mainOverflow));
							}
							mainLocationPreviousPointer = mainOverflow+bSize-8;
							mainFirst++;
							mainBucket = bucketInitialization(mainBucket, bSize);

						}

						if(checkSplitBucket && splitFirst != 0)
						{
							// create an overflow bucket and insert
							long splitOverflow = getOverflowBucket(ro, bSize);
							ro.seek(splitOverflow);
							ro.write(splitBucket);
							if(splitFirst==1)
							{
								ri.seek(splitLocationPreviousPointer);
								ri.write(MyLong.toByteArray(splitOverflow));
							}
							else
							{
								ro.seek(splitLocationPreviousPointer);
								ro.write(MyLong.toByteArray(splitOverflow));
							}
							splitLocationPreviousPointer = splitOverflow+bSize-8;
							splitFirst++;
							splitBucket = bucketInitialization(splitBucket, bSize);
						}

						if(checkMainBucket && mainFirst == 0)
						{
							// write main bucket to original location
							// empty the main bucket
							// store the location of the next pointer in temp variable
							ri.seek(mainPosition);
							ri.write(mainBucket);
							mainLocationPreviousPointer = mainPosition+bSize-8;
							mainFirst++;
							mainBucket = bucketInitialization(mainBucket, bSize);
						}

						if(checkSplitBucket && splitFirst == 0)
						{
							// write split bucket to split location
							// empty the split bucket
							// store the location of the next pointer in temp variable
							ri.seek(splitPosition);
							ri.write(splitBucket);
							splitLocationPreviousPointer = splitPosition+bSize-8;
							splitFirst++;
							splitBucket = bucketInitialization(splitBucket, bSize);
						}

						if(overflowPosition != -1)
						{
							// Extract the information
							overflowPointers.add(overflowPosition);
							ro.seek(overflowPosition);
							ro.read(extract,0,bSize);
							deallocateOverflowBucket(ro,overflowPosition, bSize);
							overflowPosition = MyLong.toLong(Arrays.copyOfRange(extract, bSize-8, bSize));
							extract(original,extract,fieldSize);
						}

					}while(!original.isEmpty() || !main.isEmpty() || !split.isEmpty());	

					// If the main and the split bucket has records to insert

					if(!isBucketEmpty(mainBucket) && mainFirst !=0)
					{
						// create an overflow bucket and insert
						long mainOverflow = getOverflowBucket(ro, bSize);
						ro.seek(mainOverflow);
						ro.write(mainBucket);
						if(mainFirst==1)
						{
							ri.seek(mainLocationPreviousPointer);
							ri.write(MyLong.toByteArray(mainOverflow));
						}
						else
						{
							ro.seek(mainLocationPreviousPointer);
							ro.write(MyLong.toByteArray(mainOverflow));
						}
						mainLocationPreviousPointer = mainOverflow+bSize-8;
						mainFirst++;
						//		mainBucket = bucketInitialization(mainBucket, bSize);

					}

					if(!isBucketEmpty(splitBucket) && splitFirst != 0)
					{
						// create an overflow bucket and insert
						long splitOverflow = getOverflowBucket(ro, bSize);
						ro.seek(splitOverflow);
						ro.write(splitBucket);
						if(splitFirst==1)
						{
							ri.seek(splitLocationPreviousPointer);
							ri.write(MyLong.toByteArray(splitOverflow));
						}
						else
						{
							ro.seek(splitLocationPreviousPointer);
							ro.write(MyLong.toByteArray(splitOverflow));
						}
						splitLocationPreviousPointer = splitOverflow+bSize-8;
						splitFirst++;
						//	splitBucket = bucketInitialization(splitBucket, bSize);
					}

					if(mainFirst == 0)
					{
						// write main bucket to original location
						// empty the main bucket
						// store the location of the next pointer in temp variable
						ri.seek(mainPosition);
						ri.write(mainBucket);
						mainLocationPreviousPointer = mainPosition+bSize-8;
						mainFirst++;
						//	mainBucket = bucketInitialization(mainBucket, bSize);
					}

					if(splitFirst == 0)
					{
						// write split bucket to split location
						// empty the split bucket
						// store the location of the next pointer in temp variable
						ri.seek(splitPosition);
						ri.write(splitBucket);
						splitLocationPreviousPointer = splitPosition+bSize-8;
						splitFirst++;
						//	splitBucket = bucketInitialization(splitBucket, bSize);
					}

					next = next+1;

					if(next == Math.pow(2,level))
					{
						next = 0;
						level = level+1;
					}

					ri.seek(0);
					ri.write(MyLong.toByteArray(level));
					ri.write(MyLong.toByteArray(next));	
				}

				ri.close();
				ro.close();
				baos1.close();
//				displayIndex(mainIndexFile,overflowIndexFile);
			}
			catch(Exception e)
			{
				System.out.println("Error while inserting record in index file "+e+", restart program");
				System.exit(1);
			}
		}
		long hash(String headerField,byte[] field)
		{
			long hashValue = 1;
			if(headerField.charAt(0)=='c')
			{
				hashValue = HeapFile.hh.get("c").hash(field);
			}
			else
			{
				hashValue = HeapFile.hh.get(headerField).hash(field);
			}
			return hashValue;
		}
		void displayIndex(File mainIndex, File overIndex)
		{
			try
			{
				RandomAccessFile ri = new RandomAccessFile(mainIndex,"r");
				RandomAccessFile ro = new RandomAccessFile(overIndex, "r");
				byte []forLong = new byte[8];
				byte []forInt = new byte[4];
				boolean isString = false;
				int bSize = 0;
				int fieldSize = 0;
				long level = 0;
				long next = 0;
				long overflow = -1;
				String fieldType = "";
				StringBuilder sb = new StringBuilder();
				sb.append("MainIndex\n");
				ri.read(forLong,0,8);
				level = MyLong.toLong(forLong);
				sb.append("Level: "+HeapFile.hc.get("i8").convert(forLong,0)+"\t");
				ri.read(forLong,0,8);
				next = MyLong.toLong(forLong);
				sb.append("Next: "+HeapFile.hc.get("i8").convert(forLong,0)+"\t");
				ri.read(forInt,0,4);
				sb.append("BucketSize: "+HeapFile.hc.get("i4").convert(forInt,0)+"\t");
				bSize = MyInt.toInt(forInt);
				byte []forBucket = new byte[bucketSize];
				ri.read(forInt,0,4);
				sb.append("FieldSize: "+HeapFile.hc.get("i4").convert(forInt,0)+"\t");
				fieldSize = MyInt.toInt(forInt);
				byte []forField = new byte[fieldSize];
				ri.read(forInt,0,4);
				sb.append("FieldHeaderSize: "+HeapFile.hc.get("i4").convert(forInt,0)+"\t");
				byte []fieldHeader = new byte[MyInt.toInt(forInt)];
				ri.read(fieldHeader,0,MyInt.toInt(forInt));
				fieldType = new String(fieldHeader);
				sb.append("FieldHeader: "+fieldType+"\t");
				ri.read(forInt,0,4);
				sb.append("FieldNo: "+HeapFile.hc.get("i4").convert(forInt,0)+"\n");
				System.out.println(sb.toString());
				sb = new StringBuilder();
				if(fieldType.charAt(0)=='c')
				{
					isString = true;
				}
				for(int i=0; i<Math.pow(2,level)+next;i++)
				{
					int overflowNumber = 1;
					if(ri.getFilePointer()<=ri.length()-bSize)
					{
						ri.read(forBucket,0,bSize);	
						sb.append("bucketNo: "+i+"\n");
						int j = 0;
						for(j = 0; j <= forBucket.length-8-8-fieldSize;j += 8 + fieldSize)
						{
							forLong = Arrays.copyOfRange(forBucket,j,j+8);
							sb.append("RID: "+MyLong.toLong(forLong)+"\t");
							forField = Arrays.copyOfRange(forBucket,j+8,j+8+fieldSize);
							if(isString)
								sb.append("FieldValue: "+new String(forField)+"\t");
							else
								sb.append("FieldValue: "+HeapFile.hc.get(fieldType).convert(forField,0)+"\t");
						}
						forLong = Arrays.copyOfRange(forBucket,forBucket.length-8,forBucket.length);
						overflow = MyLong.toLong(forLong);
						sb.append("\nNext pointer: "+MyLong.toLong(forLong)+"\n");
						System.out.println(sb.toString());
						sb = new StringBuilder();
						while(overflow!=-1)
						{
							ro.seek(overflow);
							ro.read(forBucket,0,bSize);
							sb.append("OveflowBucketNo: "+overflowNumber+"\n");
							for(j=0; j<=forBucket.length-8-8-fieldSize;j+=8+fieldSize)
							{
								forLong = Arrays.copyOfRange(forBucket,j,j+8);
								sb.append("RID: "+MyLong.toLong(forLong)+"\t");
								forField = Arrays.copyOfRange(forBucket,j+8,j+8+fieldSize);
								if(isString)
									sb.append("FieldValue: "+new String(forField)+"\t");
								else
									sb.append("FieldValue: "+HeapFile.hc.get(fieldType).convert(forField,0)+"\t");
							}
							forLong = Arrays.copyOfRange(forBucket,forBucket.length-8,forBucket.length);
							overflow = MyLong.toLong(forLong);
							sb.append("\nNext pointer: "+MyLong.toLong(forLong)+"\n");
							System.out.println(sb.toString());
							sb = new StringBuilder();
							overflowNumber++;
						}
					}
				}
				ri.close();
				ro.close();
			}
			catch (Exception e)
			{
				System.out.println("I am in display "+e+", restart program");
				System.exit(1);
			}
		}
	}
