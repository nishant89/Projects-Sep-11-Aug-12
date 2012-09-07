import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.util.Arrays;


public class FileTest {

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
		if (data == null || data.length % 2 != 0) return null;
		char[] chrs = new char[data.length / 2];
		for (int i = 0; i < chrs.length; i++) 
		{
			chrs[i] = toChar(new byte[] {data[(i*2)],data[(i*2)+1],});
		}
		return chrs;
	}
	static public String convert(byte []b,int off)
	{
		byte []ba=Arrays.copyOfRange(b, off, off+4);
		System.out.println(toInt(ba));
		return Integer.toString(toInt(ba));
	}
	public static String toString(byte[] data) 
	{
		return (data == null) ? null : new String(data);
	}
	public static int compare(byte []record1, int off, String value, int length)
	{
		byte []b1=Arrays.copyOfRange(record1,off,off+length);
		String x = toString(b1).trim();
        String y = value.trim();
        return x.compareTo(y);
	}
	public static void main(String[] args) {
		try
		{
		BufferedWriter bw=new BufferedWriter(new FileWriter(new File("test.txt")));
		String temp="Orson"+"\r\n"+"Nishant";
		byte []ba=new byte[10];
		ba=temp.getBytes();
		bw.write(temp);
		bw.close();
//		System.out.println(compare(ba,0,"Orson",5));
//		ByteArrayOutputStream baos=new ByteArrayOutputStream();
//		raf.write(byteArray);
//		//raf.seek(4);
//		raf.write(ba);
//		raf.seek(0);
//		raf.read(byteArray1);
//		System.out.println(toInt(byteArray1));
//		raf.read(byteArray1);
//		System.out.println(toInt(byteArray1));
		
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
	}
}
