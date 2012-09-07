
public class IndexRecord {
	byte []field;
	long RID;
	IndexRecord(int fieldSize)
	{
		field = new byte[fieldSize];
	}
}
