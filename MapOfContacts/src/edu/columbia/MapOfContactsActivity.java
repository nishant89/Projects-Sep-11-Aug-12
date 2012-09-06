package edu.columbia;

import java.io.BufferedReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapOfContactsActivity extends MapActivity {
    /** Called when the activity is first created. */
	MapView mapView;
    List<Overlay> mapOverlays;
    Drawable drawable;
    HelloItemizedOverlay itemizedOverlay;
	protected boolean isRouteDisplayed() 
		{    
		return false;
		}
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Downlaoding and saving the file
        try{
        URL	textUrl = new URL("http://www.cs.columbia.edu/~coms6998-8/assignments/homework2/contacts/contacts.txt");   
        String dir = Environment.getExternalStorageDirectory().toString();
        File f= new File(dir,"SampleContact.txt");
        URLConnection urlconn = textUrl.openConnection();
        InputStream in = urlconn.getInputStream();
        BufferedInputStream br = new BufferedInputStream(in);
        ByteArrayBuffer ba = new ByteArrayBuffer(500);
        int cursor = 0;
        while ((cursor = br.read()) != -1)
        {                    
        	ba.append((byte) cursor);
        }
        FileOutputStream out = new FileOutputStream(f);
        out.write(ba.toByteArray());
        out.close();
        }
        catch (IOException e)
        {
        	System.out.println(e);
        }
        
        // Reading from the file and creating contacts
        String [] temp; 
        try 
        {
        FileInputStream fis= new FileInputStream("/mnt/sdcard/SampleContact.txt");
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        StringBuffer bf=new StringBuffer();
        String input;
        while((input=br.readLine()) != null)
        {
        	bf.append(input+" ");
        }
        String s=bf.toString();
        temp=s.split(" ");
        for(int i=0;i<=temp.length-4;i=i+4)
        {
        ContentValues values=new ContentValues();
        Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values); 
        long rawContactId = ContentUris.parseId(rawContactUri); 
        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId); 
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE); 
        values.put(StructuredName.DISPLAY_NAME, temp[i]);
        getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE); 
        values.put(Email.DATA1, temp[i+1]);
        values.put(Email.TYPE, Phone.TYPE_WORK);
        getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE); 
        values.put(Phone.NUMBER, temp[i+2]);
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE); 
        values.put(Phone.NUMBER, temp[i+3]);
        values.put(Phone.TYPE, Phone.TYPE_HOME);
        getContentResolver().insert(Data.CONTENT_URI, values);
        }
        }
        catch (MalformedURLException e) 
        { 
        e.printStackTrace();     
        } 
        catch (IOException e) 
        {   
        e.printStackTrace();    
        }
        
        // Querying the Contacts
        ArrayList<ContactElement> contactData=new ArrayList<ContactElement>();
        Cursor c = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while(c.moveToNext()){
        ContactElement ce= new ContactElement();
         String ContactID = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
         ce.contactID=ContactID;
         String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
         ce.name=name;
         String hasPhone =c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
         if(Integer.parseInt(hasPhone) == 1)
         {
          Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"='"+ContactID+"'",null, null);
          while(phoneCursor.moveToNext())
          {
           String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

           String numberType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
           if(numberType.equalsIgnoreCase("2"))
        	   ce.mobile=number;
           if(numberType.equalsIgnoreCase("1"))
        	   ce.home=number;
           }
         phoneCursor.close();
        }
         else{
        	 System.out.println("no phone number found");
        	 }
         if(ce.home!=null && ce.mobile!=null)
        	 contactData.add(ce);
        }
      
        // Create OverlayItems on the Map and Displaying the Map
        setContentView(R.layout.main);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        mapOverlays = mapView.getOverlays();
        drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedOverlay = new HelloItemizedOverlay(drawable, this);
        TextView tv= new TextView(this);
        for(ContactElement i:contactData)
        {
        GeoPoint point = new GeoPoint(Integer.parseInt(i.mobile),Integer.parseInt(i.home));
        OverlayItem overlayitem = new OverlayItem(point, i.name, "");
        itemizedOverlay.addOverlay(overlayitem);
        }
        mapOverlays.add(itemizedOverlay);
        }
}
