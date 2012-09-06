package edu.columbia;

public class ContactElement{
	String contactID;
	String name;
	String mobile;
	String home;
	ContactElement()
	{
//		name=null;
//		contactID=null;
//		home=null;
//		mobile=null;
	}
	ContactElement(String id, String n, String e, String m, String h)
	{
		contactID=id;
		name=n;
		mobile=m;
		home=h;
	
	}
	ContactElement(String id)
	{
		contactID=id;
	}
}
