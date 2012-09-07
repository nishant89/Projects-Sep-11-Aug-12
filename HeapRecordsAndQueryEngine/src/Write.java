import java.io.ByteArrayOutputStream;


public interface Write {
	long write(ByteArrayOutputStream baos,String input,long off);
}
