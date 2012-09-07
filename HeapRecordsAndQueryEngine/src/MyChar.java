import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class MyChar extends Convert implements Write, Read, Compare, Hash{
	public static byte[] toByteArray(char data) 
	{
		return new byte[] {
				(byte)((data >> 8) & 0xff),
				(byte)((data >> 0) & 0xff)};
	}
	public static byte[] toByteArray(char[] data) 
	{
		if (data == null)
			return null;
	byte[] byts = new byte[data.length * 2];
	for (int i = 0; i < data.length; i++)
		System.arraycopy(toByteArray(data[i]), 0, byts, i * 2, 2);
	return byts;
	}
	public static char toChar(byte[] data) 
	{
		if (data == null || data.length != 2) 
			return 0x0; 
			return (char)((0xff & data[0]) << 8   |
				(0xff & data[1]) << 0);
	}
	public static char[] toCharArray(byte[] data) 
	{ 
		if (data == null || data.length % 2 != 0) 
			return null;
		char[] chrs = new char[data.length / 2];
		for (int i = 0; i < chrs.length; i++) 
		{
			chrs[i] = toChar(new byte[] {data[(i*2)],data[(i*2)+1],});
		}
		return chrs;
	}
	public static String toString(byte[] data) 
	{
		return (data == null) ? null : new String(data);
	}
	public String convert(byte [] b,int off)
	{
		byte []ba=Arrays.copyOfRange(b, off, off+length);
		return toString(ba);
	}
	public int compare(byte []record1, int off, String value, int length)
	{
		byte []b1=Arrays.copyOfRange(record1,off,off+length);
		String x = (toString(b1).trim()).toUpperCase();
        String y = (value.trim()).toUpperCase();
        return x.compareTo(y);
	}
	public String read (RandomAccessFile raf,int off, int length)
	{
		byte [] byteArray=new byte[length];
		try{
		raf.seek(off);	
		raf.read(byteArray,0,length);
		}
		catch(Exception e)
		{
			System.out.println("I am in char");
			System.out.println(e);
		}
		return toString(byteArray);
	}
	public long hash(byte []field)
	{
		long sum = 0;
		char []ch = new String(field).toCharArray();
		for(int i=0;i<ch.length;i++)
		{
			sum+=ch[i]*26*17;
		}
		return sum;
	}
	public long write(ByteArrayOutputStream baos, String input, long off)
	{
		//char []ch=input.toCharArray();
		//byte []byteArray=toByteArray(ch);
		byte []byteArray=input.getBytes();
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
		return byteArray.length;
	}
}
