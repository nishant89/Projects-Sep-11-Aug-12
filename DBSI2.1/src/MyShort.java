import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class MyShort extends Convert implements Write, Read, Compare, Hash {
	public MyShort()
	{
		length=2;
	}
	public static byte[] toByteArray(short data)
	{
		return new byte[] {
				(byte)((data >> 8) & 0xff),(byte)((data >> 0) & 0xff)};
	}
	public static short toShort(byte[] data) 
	{
		if (data == null || data.length != 2) return 0x0; 
		return (short)((0xff & data[0]) << 8   | (0xff & data[1]) << 0);
	}
	public String convert(byte []b,int off)
	{
		byte []ba=Arrays.copyOfRange(b, off, off+2);
		return Short.toString(toShort(ba));
	}
	public int compare(byte []record1, int off, String value, int length)
	{
		byte []b1=Arrays.copyOfRange(record1,off,off+2);
		short x = toShort(b1);
        short y = Short.parseShort(value);
        return x < y ? -1 : x > y ? 1 : 0;
	}
	public String read (RandomAccessFile raf,int off, int length)
	{
		byte [] byteArray=new byte[2];
		short t=0;
		try{
		raf.seek(off);
		raf.read(byteArray,0,2);
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		t=toShort(byteArray);
		return Short.toString(t);
	}
	public long hash(byte []field)
	{	
		long hashValue = 0;
		hashValue = toShort(field);
		return hashValue;
	}
	
	public  long write(ByteArrayOutputStream baos, String input,long off)
	{
		byte []byteArray=toByteArray(Short.parseShort(input));
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
		return 2;
	}
}
