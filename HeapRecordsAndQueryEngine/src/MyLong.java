import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class MyLong extends Convert implements Write, Read, Compare, Hash{
	public MyLong()
	{
		length=8;
	}
	public static byte[] toByteArray(long data) 
	{
		return new byte[] {(byte)((data >> 56) & 0xff),
				(byte)((data >> 48) & 0xff),
				(byte)((data >> 40) & 0xff),
				(byte)((data >> 32) & 0xff),        
				(byte)((data >> 24) & 0xff),
				(byte)((data >> 16) & 0xff),
				(byte)((data >> 8) & 0xff),
				(byte)((data >> 0) & 0xff)};
	}
	public static long toLong(byte[] data) 
	{
		if (data == null || data.length != 8) return 0x0; 
		return (long)((long)(0xff & data[0]) << 56  |
				(long)(0xff & data[1]) << 48  |
				(long)(0xff & data[2]) << 40  |
				(long)(0xff & data[3]) << 32  |
				(long)(0xff & data[4]) << 24  |
				(long)(0xff & data[5]) << 16  |
				(long)(0xff & data[6]) << 8   |
				(long)(0xff & data[7]) << 0);
	}
	public int compare(byte []record1, int off, String value, int length)
	{
		byte []b1=Arrays.copyOfRange(record1,off,off+8);
		long x = toLong(b1);
        long y = Long.parseLong(value);
        return x < y ? -1 : x > y ? 1 : 0;
	}
	public String convert(byte []b,int off)
	{
		byte []ba=Arrays.copyOfRange(b, off, off+8);
		return Long.toString(toLong(ba));
	}
	public String read (RandomAccessFile raf,int off, int length)
	{
		byte [] byteArray=new byte[8];
		long t=0;
		try
		{
		raf.seek(off); 
		raf.read(byteArray,0,8);
		}
		catch(Exception e)
		{
			System.out.println("I am in Long");
			System.out.println(e);
		}
		t=toLong(byteArray);
		return Long.toString(t);
	}
	public long hash(byte []field)
	{	
		long hashValue = 0;
		hashValue = toLong(field);
		return hashValue;
	}
	public  long write(ByteArrayOutputStream baos, String input, long off)
	{
		byte []byteArray=toByteArray(Long.parseLong(input));
		try
		{
			//raf.seek(off);
			//raf.write(byteArray);
			baos.write(byteArray);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return 8;
	}
}
