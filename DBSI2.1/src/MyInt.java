 import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class MyInt extends Convert implements Write, Read, Compare, Hash {
	public MyInt()
	{
		length=4;
	}
	public static byte[] toByteArray(int data)
	{
		return new byte[]{(byte)((data >> 24) & 0xff),
				(byte)((data >> 16) & 0xff),
				(byte)((data >> 8) & 0xff),
				(byte)((data >> 0) & 0xff)};
	}
	public static int toInt(byte[] data) 
	{
		if (data == null || data.length != 4) 
			return 0x0;
		return (int)((0xff & data[0]) << 24  |
				(0xff & data[1]) << 16  |
				(0xff & data[2]) << 8   |
				(0xff & data[3]) << 0);
	}
	public int compare(byte []record1, int off, String value, int length)
	{
		byte []b1=Arrays.copyOfRange(record1,off,off+4);
		int x = toInt(b1);
        int y = Integer.parseInt(value);
        return x < y ? -1 : x > y ? 1 : 0;
	}
	public String convert(byte []b,int off)
	{
		byte []ba=Arrays.copyOfRange(b, off, off+4);
		System.out.println(toInt(ba));
		return Integer.toString(toInt(ba));
	}
	public String read (RandomAccessFile raf,int off, int length)
	{
		byte [] byteArray=new byte[4];
		int t=0;
		try{
		 raf.seek(off);
		 raf.read(byteArray,0,4);
		}
		catch(Exception e)
		{
			System.out.println("i am in Int");
			System.out.println(e);
		}
		t=toInt(byteArray);
		return Integer.toString(t);
	}
	public long hash(byte []field)
	{	
		long hashValue = 0;
		hashValue = toInt(field);
		return hashValue;
	}
	public  long write(ByteArrayOutputStream baos, String input, long off)
	{
		byte []byteArray=toByteArray(Integer.parseInt(input));
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
		return 4;
	}
	
}
