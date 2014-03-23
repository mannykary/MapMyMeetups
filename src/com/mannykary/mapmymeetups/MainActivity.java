package com.mannykary.mapmymeetups;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mannykary.mapmymeetups.JSONReaderTask;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity implements
	OnMapClickListener, OnMapLongClickListener /*implements
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener */{

	private GoogleMap mMap;
	private LatLng myLocation;
	private LocationClient mLocationClient;
	private Location mCurrentLocation;
	private String query;
	private HashMap<String,String> data;
	private String eventURL;
	private int numEvents;
	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//mLocationClient = new LocationClient(this, this, this);
		//mLocationClient.connect();
		setUpMapIfNeeded();
	}
	
//    @Override
//    protected void onStart() {
//        super.onStart();
//        // Connect the client.
//        mLocationClient.connect();
//    }
//    
//    @Override
//    protected void onStop() {
//        // Disconnecting the client invalidates it.
//        mLocationClient.disconnect();
//        super.onStop();
//    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Check if device has the Google Play Services APK
		GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		setUpMapIfNeeded();
	}
	
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the map
		if (mMap == null) {
			mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			
			// Check if we were successful in obtaining the map
			if (mMap != null) {
				// The map is verified. It is now safe to manipulate the map.
				mMap.setMyLocationEnabled(true);
				mMap.setOnMapClickListener(this);
		        mMap.setOnMapLongClickListener(this);
		        
		        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();

                Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                if (location != null)
                {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 13));

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(12)                   // Sets the zoom
                    //.bearing(90)                // Sets the orientation of the camera to east
                    //.tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                }
		        			    
			}
		}
	}

	@Override
	public void onMapLongClick(LatLng point) {
		Log.i("MapMyMeetups", "onMapLongClick" + point.toString());
		myLocation = point;
		        
		query = "https://api.meetup.com/2/open_events?&sign=true"
        		+ "&lon=" + myLocation.longitude 
        		+ "&lat=" + myLocation.latitude
        		+ "&time=" + System.currentTimeMillis()
        		+ "," + (System.currentTimeMillis() + 86400000)
        		+ "&radius=10&page=20"
        		+ "&key=" + getString(R.string.MeetupKey)
        		+ "&format=json";
			
        Log.i("MapMyMeetups", "query: " + query);
        
        String jq = null;
        
		try {
			jq = new JSONReaderTask().execute(query).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Log.i("MapMyMeetups", "json: " + jq);
		
		try {
			data = getMeetups(jq);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, yyyy h:mm a");
				
		for( int i = 0; i < numEvents; i++ ) {
			
			Log.i("MapMyMeetups", "event_" + i + "_lat: " + data.get("event_" + i + "_lat"));
			Log.i("MapMyMeetups", "event_" + i + "_lon: " + data.get("event_" + i + "_lon"));
			
			mMap.addMarker(new MarkerOptions()
			.position(new LatLng((Float.parseFloat(data.get("event_" + i + "_lat"))), 
								  Float.parseFloat(data.get("event_" + i + "_lon"))))
			.title(data.get("event_" + i + "_name"))
			.snippet(sdf.format(new Date(Long.parseLong(data.get("event_" + i + "_time")))))
			.icon(BitmapDescriptorFactory
					.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
			.alpha(0.7f));
			
			Log.i("MapMyMeetups", data.get("event_" + i + "_url"));
			
			eventURL = "event_" + i + "_url";
			
			//TODO figure out how to handle an info window click.
			// need to get URL of the event and pass it to click listener
			// to trigger URL intent.
			mMap.setOnInfoWindowClickListener(
		      		  new OnInfoWindowClickListener(){
		      		    public void onInfoWindowClick(Marker marker){
		      		    	Intent intent = new Intent(Intent.ACTION_VIEW, 
		      		    				   Uri.parse(data.get(eventURL)) );
		      		    	startActivity(intent);
		      		    }
		      		  }
		      		);
		}
		
	}

	@Override
	public void onMapClick(LatLng point) {
		Log.i("MapMyMeetups", "onMapClick" + point.toString());
		
	}
	
	public void onMarkerClick(Marker marker) {
		
	}
	
	public HashMap<String,String> getMeetups(String q) throws JSONException {
		
		HashMap<String, String> data = new HashMap<String, String>();
		
		JSONObject objMain = new JSONObject(q);
		JSONArray results = objMain.getJSONArray("results");
		
		numEvents = results.length();
		
		for( int i = 0; i < numEvents; i++ ){
			if( results.getJSONObject(i).has("venue") ) {
				data.put("event_" + i + "_lon", results.getJSONObject(i).getJSONObject("venue").getString("lon"));
				data.put("event_" + i + "_lat", results.getJSONObject(i).getJSONObject("venue").getString("lat"));
			}
			else {
				data.put("event_" + i + "_lon", results.getJSONObject(i).getJSONObject("group").getString("group_lon"));
				data.put("event_" + i + "_lat", results.getJSONObject(i).getJSONObject("group").getString("group_lat"));
			}
			data.put("event_" + i + "_time", results.getJSONObject(i).getString("time"));
			data.put("event_" + i + "_url", results.getJSONObject(i).getString("event_url"));
			data.put("event_" + i + "_desc", results.getJSONObject(i).getString("description"));
			data.put("event_" + i + "_name", results.getJSONObject(i).getString("name"));
			data.put("event_" + i + "_group", results.getJSONObject(i).getJSONObject("group").getString("name"));
			
			Log.i("MapMyMeetups", "event_" + i + "_lon: " + data.get("event_" + i + "_lon"));
			Log.i("MapMyMeetups", "event_" + i + "_lat: " + data.get("event_" + i + "_lat"));
			Log.i("MapMyMeetups", "event_" + i + "_time: " + data.get("event_" + i + "_time"));
			Log.i("MapMyMeetups", "event_" + i + "_url: " + data.get("event_" + i + "_url"));
			Log.i("MapMyMeetups", "event_" + i + "_desc:" + data.get("event_" + i + "_desc"));
			Log.i("MapMyMeetups", "event_" + i + "_name: " + data.get("event_" + i + "_name"));
			Log.i("MapMyMeetups", "event_" + i + "_group: " + data.get("event_" + i + "_group"));
		}
		
		return data;

	}

	
//    /*
//     * Called by Location Services when the request to connect the
//     * client finishes successfully. At this point, you can
//     * request the current location or start periodic updates
//     */
//    @Override
//    public void onConnected(Bundle dataBundle) {
//        // Display the connection status
//        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
//    }
//    
//    /*
//     * Called by Location Services if the connection to the
//     * location client drops because of an error.
//     */
//    @Override
//    public void onDisconnected() {
//        // Display the connection status
//        Toast.makeText(this, "Disconnected. Please re-connect.",
//                Toast.LENGTH_SHORT).show();
//    }
//    
//    /*
//     * Called by Location Services if the attempt to
//     * Location Services fails.
//     */
//    @Override
//    public void onConnectionFailed(ConnectionResult connectionResult) {
//        /*
//         * Google Play services can resolve some errors it detects.
//         * If the error has a resolution, try sending an Intent to
//         * start a Google Play services activity that can resolve
//         * error.
//         */
//        if (connectionResult.hasResolution()) {
//            try {
//                // Start an Activity that tries to resolve the error
//                connectionResult.startResolutionForResult(
//                        this,
//                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
//                /*
//                 * Thrown if Google Play services canceled the original
//                 * PendingIntent
//                 */
//            } catch (IntentSender.SendIntentException e) {
//                // Log the error
//                e.printStackTrace();
//            }
//        } else {
//            /*
//             * If no resolution is available, display a dialog to the
//             * user with the error.
//             */
//            showErrorDialog(connectionResult.getErrorCode());
//        }
//    }
//    void showErrorDialog(int code) {
//    	  GooglePlayServicesUtil.getErrorDialog(code, this, 
//    			  CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
//    }
//    

}
