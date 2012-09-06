import java.util.ArrayList;
import java.util.Iterator;


public class TempCheck {
	static public int convert(char c[])
	{
		int result=0; int j=1;
		for(int i=c.length-1;i>=0;i--)
		{
			result+=c[i]*j;
			j=j*10;
		}
		return result;
	}
	public static void main(String[] args) {
		ArrayList<Person> c= new ArrayList<Person>();
		Iterator<Person> i= c.iterator();
		c.add(new Person("naman",25));
		while(i.hasNext())
			System.out.println(i.next().name);
		
	
	}

}
