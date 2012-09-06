import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class MyByte extends Convert implements Write, Read, Compare, Hash {
	public MyByte()
	{
		length=1;
	}
	public static byte[] toByteArray(byte data) 
	{
		return new byte[]{data};
	}
	public static byte[] toByteArray(byte[] data) 
	{
		return data;
	}
	String convert(byte [] b,int off)
	{
		return Byte.toString(b[off]);
	}
	public int compare(byte []record1, int off, String value, int length)
	{
		byte x=record1[off];
		byte y = Byte.parseByte(value);
        return x < y ? -1 : x > y ? 1 : 0;
	}
	public String read (RandomAccessFile raf,int off, int length)
	{
		byte [] byteArray=new byte[1];
		try{
			raf.seek(off);
		raf.read(byteArray,0,1);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return new String(toByteArray(byteArray));
	}
	public long hash(byte []field)
	{	
		long hashValue = 0;
		hashValue = field[0];
		return hashValue;
	}
	public long write(ByteArrayOutputStream baos,String input,long off)
	{
		
		byte b=Byte.parseByte(input);
		try
		{
		//raf.seek(off);
		//.write(b);
			baos.write(b);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return 1;
	}

}
