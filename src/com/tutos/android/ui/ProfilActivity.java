package com.tutos.android.ui;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

 public class ProfilActivity extends FragmentActivity implements OnMapLongClickListener,OnMapClickListener {
	 
         //variable declaration
	 
	   Geocoder coder = new Geocoder(this);
	   Geocoder gCoder = new Geocoder(this);
	   List<Address> address,searchAddress;
	   Address location,searchLocation;
	   String TAG = new String();
	   String TAGE = new String();
	   private GoogleMap googleMap;
	   private LatLng me;
	   double longitude;
	   double latitude;
	   private Location position;
	   private TextView hint;
	   private Button show;
	   LocationManager locationManager; //<2>
	   Geocoder geocoder; //<3>
	   private SharedPreferences mPreferences;
	   private NotificationManager NM;
	   
	   private static final LatLng fstt = new LatLng(35.736418, -5.892919);

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profil_dispaly);
 
        //initializing
        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
        hint = (TextView) findViewById(R.id.hint);
        show =(Button) findViewById(R.id.dest);
        show.setOnClickListener(btnshow);
      
        try { 
            if (googleMap == null) {
            	
            // we are using SupportMapFragment for older android versions
           // if not we can use MapFrragment like this :
          //     googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
               
               googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
               
            }            
            googleMap.setOnMapLongClickListener(this);
            googleMap.setOnMapClickListener(this);
            
        //set the map type
         googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
         // set user's location
         googleMap.setMyLocationEnabled(true);
         // show the traffic
         googleMap.setTrafficEnabled(true);
         //make the Zoom In and Zoom Out enable
         googleMap.getUiSettings().setZoomGesturesEnabled(true);
 		
 		// 1. get passed intent 
         Intent intent = getIntent();
  
         // 4. get bundle from intent
         Bundle bundle = intent.getExtras();
  
         // 5. get status value from bundle
         String adr = bundle.getString("adress");
  
         // 6. show address
         hint.setText(adr);
 		//convert the recieved address to LatLng by using Geocoding
 		address = coder.getFromLocationName(adr,2);
 		if (address == null) {
 		
 		Log.d(TAG, "############## Address not correct ############");
 		}
 		location = address.get(0);

 		Log.d(TAG, "################ Address Latitude : "+ location.getLatitude()+ "Address Longitude : "+ location.getLongitude());
 		LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
 		
 		//display a marker on that address after we get the LatLng
 		googleMap.addMarker(new MarkerOptions()
 		.position(pos)
 		.title(adr)
 		.draggable(true));
 		
 		//move the camera to that address
 		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 10));
 		
 		googleMap.animateCamera(CameraUpdateFactory.newLatLng(pos));
         
         //add a marker at FSTT
         googleMap.addMarker(new MarkerOptions()
                 .position(fstt)
                 .title("FSTT")
                 .snippet("Faculté des Sciences et Techniques de Tanger")
                 .icon(BitmapDescriptorFactory.defaultMarker(
                       BitmapDescriptorFactory.HUE_CYAN)));
         
         //get the user's location
         position = googleMap.getMyLocation();
         //set the user's Latitude/Longitude
         me = new LatLng(position.getLatitude(), position.getLongitude());
     
     	//add marker on user's location
     		googleMap.addMarker(new MarkerOptions()
     		.position(me)
     		.draggable(true)
     		.title("You are HERE")
     		.icon(BitmapDescriptorFactory.defaultMarker(
                    BitmapDescriptorFactory.HUE_AZURE)));
     		     		
      } catch (Exception e) {
         e.printStackTrace();
      }
} 
     @Override
     public void onMapClick(LatLng point) {
      googleMap.animateCamera(CameraUpdateFactory.newLatLng(point));
     
     }

     //when the user press a long click on the map we get the LatLng and convert it to an address and display it 
     @Override
     public void onMapLongClick(LatLng point) {
     	
    	 Geocoder geocoder;
    	 List<Address> addresses;
    	 geocoder = new Geocoder(this, Locale.getDefault());
    	 try {
			addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
			String address = addresses.get(0).getAddressLine(0);
	    	String city = addresses.get(0).getAddressLine(1);
	    	
	    	hint.setText(address+", "+city);
	    	
	    	googleMap.clear();
	    	
	    	googleMap.addMarker(new MarkerOptions()
	    	.position(point)
	    	.title(address)
	    	.snippet(city));
	    	
    	 } catch (IOException e) {
			e.printStackTrace();
		}
     }
        
     	//show destinations button
        OnClickListener btnshow = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(ProfilActivity.this,DestinationsActivity.class);
				startActivity(intent);
			}
		};
		
		@SuppressLint("NewApi") @Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    // Inflate the menu items for use in the action bar
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.profil, menu);
		    
		 // Associate searchable configuration with the SearchView
		    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		    SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	 		
		    if (null != searchView) {
		        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
		        searchView.setIconifiedByDefault(false);
		    }

		    SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
		        public boolean onQueryTextChange(String newText) {
		            // this is your adapter that will be filtered
		            return true;
		        }

		        public boolean onQueryTextSubmit(String query) {
		            //Here u can get the value "query" which is entered in the search box.

		        	Log.e(TAGE, query);
				    try {
						searchAddress = gCoder.getFromLocationName(query,1);
			 		if (searchAddress.isEmpty()) {
			 		
			 		Log.e(TAGE, "############## Address is not correct ############");
				    Toast.makeText(ProfilActivity.this, "Address is not correct :(", Toast.LENGTH_LONG).show();
			 		}
			 		else{
			 			
			 		searchLocation = searchAddress.get(0);
			 		Log.e(TAGE, "################ Address Latitude : "+ searchLocation.getLatitude()+ "Address Longitude : "+ searchLocation.getLongitude());
			 		LatLng position = new LatLng(searchLocation.getLatitude(), searchLocation.getLongitude());
			 		
			 		googleMap.clear();
			 		googleMap.addMarker(new MarkerOptions()
			 		.position(position)
			 		.title(query)
			 		.snippet(searchLocation.getCountryName())
			 		.draggable(true));

			 	      googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
			 		}
				    } catch (IOException e) {
						e.printStackTrace();
					}
					return true;
		        	
		        }
		    };
		    searchView.setOnQueryTextListener(queryTextListener);

		    return super.onCreateOptionsMenu(menu);

		}

		@SuppressWarnings("deprecation") @Override
		  public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    // action with ID action_refresh was selected
		    case R.id.action_search:
		      Toast.makeText(this, "Enter an address ", Toast.LENGTH_SHORT).show();
		      
		      break;
		    // action with ID action_settings was selected
		    case R.id.logoutbtn:
		      Toast.makeText(this, "Logout ....", Toast.LENGTH_SHORT).show();
		    //logout button	
		        //Session Controller
		        mPreferences = getSharedPreferences("CurrentUser", MODE_PRIVATE);
				SharedPreferences.Editor editor=mPreferences.edit();
		        editor.remove("UserName");
		        editor.remove("PassWord");
		        editor.commit();
		        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
		        startActivity(intent);
		        finish();
		        
		        //Display "GoodBye" notifcation
		        String title = "GoodBye !!";
		        String subject = "See you next time ^_~";
		        String body = "Bye Bye ...";
		        NM=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		        Notification notify=new Notification(R.drawable.ic_launcher,title,System.currentTimeMillis());
		        PendingIntent pending=PendingIntent.getActivity(getApplicationContext(),0, new Intent(),0);
		        notify.setLatestEventInfo(getApplicationContext(),subject,body,pending);
		        NM.notify(0, notify);
		      break;
		    default:
		      break;
		    }

		    return true;
		  } 
		
 }