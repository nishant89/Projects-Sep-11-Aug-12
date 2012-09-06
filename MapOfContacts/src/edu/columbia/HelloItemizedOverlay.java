package edu.columbia;
import java.util.ArrayList;


import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;


public class HelloItemizedOverlay extends ItemizedOverlay {
	static public MapOfContactsActivity con;
	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	public HelloItemizedOverlay(Drawable defaultMarker, MapOfContactsActivity context) {
		super(boundCenterBottom(defaultMarker));
		con=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}
	public void addOverlay(OverlayItem overlay)
	{    
		mOverlays.add(overlay);    
		populate();
	}
	protected OverlayItem createItem(int i) 
	{  
		return mOverlays.get(i);
	}
	protected boolean onTap(int i)
	{
		GeoPoint  gpoint = mOverlays.get(i).getPoint();
        double lat = gpoint.getLatitudeE6()/1e6;
        double lon = gpoint.getLongitudeE6()/1e6;
        String toast = "Name: "+mOverlays.get(i).getTitle();
        //toast += "\nText: "+mOverlays.get(i).getSnippet();
        //toast += 	"\nSymbol coordinates: Lat = "+lat+" Lon = "+lon+" (microdegrees)";
        //Toast.makeText(con, toast, Toast.LENGTH_LONG).show();
        AlertDialog.Builder ad=new AlertDialog.Builder(con);
        ad.setTitle(mOverlays.get(i).getTitle());
        ad.show();
        return(true);
        
        
        
        

	}
}
