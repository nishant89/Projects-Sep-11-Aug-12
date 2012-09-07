import java.io.RandomAccessFile;


public interface Read {
	public String read (RandomAccessFile raf, int off, int length);
}
