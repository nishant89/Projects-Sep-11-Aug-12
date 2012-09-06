import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class MyFloat extends Convert implements Write,Read, Compare, Hash {
	public MyFloat()
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
	public String convert(byte []b,int off)
	{
		byte []ba=Arrays.copyOfRange(b, off, off+4);
		int t=toInt(ba);
		float res=Float.intBitsToFloat(t);
		return Float.toString(res);
	}
	public int compare(byte []record1, int off, String value,int length)
	{
		byte []b1=Arrays.copyOfRange(record1,off,off+4);
		int a = toInt(b1);
		float x=Float.intBitsToFloat(a);
        float y=Float.parseFloat(value);
        return x < y ? -1 : x > y ? 1 : 0;
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
			System.out.println(e);
		}
		t=toInt(byteArray);
		float res=Float.intBitsToFloat(t);
		return Float.toString(res);
	}
	public long hash(byte []field)
	{	
		long hashValue = 0;
		hashValue = toInt(field);
		return hashValue;
	}
	public long write(ByteArrayOutputStream baos, String input, long off)
	{
		int i=Float.floatToRawIntBits(Float.parseFloat(input));
		byte []byteArray=toByteArray(i);
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
