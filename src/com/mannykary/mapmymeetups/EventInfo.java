package com.mannykary.mapmymeetups;

import java.sql.Date;

import com.google.android.gms.maps.model.LatLng;

public class EventInfo {
	
	private LatLng latLng;
	private String title;
	private Date date;
	private String url;
	
	
	public EventInfo( LatLng l, String t, Date d, String u ) {
		setLatLng(l);
		setTitle(t);
		setDate(d);
		setUrl(u);		
	}
	
	public void setLatLng( LatLng newLatLng ) { 
		latLng = newLatLng; 
	}
	
	public LatLng getLatLng() { 
		return latLng; 
	}
	
	public void setTitle( String newTitle ) { 
		title = newTitle; 
	}
	
	public String getTitle() { 
		return title; 
	}
	
	public void setDate( Date newDate ) { 
		date = newDate; 
	}
	
	public Date getDate() { 
		return date; 
	}
	
	public void setUrl( String newUrl ) { 
		url = newUrl; 
	}
	
	public String getUrl() { 
		return url; 
	}
	

}
