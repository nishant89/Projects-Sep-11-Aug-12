import java.io.RandomAccessFile;
import java.util.Arrays;


public class Cursor {
	
	RandomAccessFile raf;
	byte [] recordInBytes;
	String []recordHeader;
	long RID = 0;
	String fileName = "";
	Cursor()
	{}
	Cursor(RandomAccessFile raf, byte[] b, String []s)
	{
		this.raf=raf;
		recordInBytes=new byte[b.length];
		recordInBytes=Arrays.copyOfRange(b,0,b.length);
		recordHeader=s;
	}
	Cursor(RandomAccessFile raf, byte[] b, String []s,long rid, String file)
	{
		this.raf=raf;
		recordInBytes=new byte[b.length];
		recordInBytes=Arrays.copyOfRange(b,0,b.length);
		recordHeader=s;
		RID = rid;
		fileName = file;
	}
	
}
