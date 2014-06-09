package com.tutos.android.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.tutos.android.ui.vo.Destination;

public class DestinationsActivity extends Activity {
	
	private String url1 = "http://192.168.52.50:8080/api/getDestinations";
	private String url2 = "http://192.168.52.50:8080/api/newDestination";
	private String url3 = "http://192.168.52.50:8080/api/deleteDestination/";
	Button logout,add,addDestination,show;
	Dialog dialog,addDialog;
	private SharedPreferences mPreferences;
	private NotificationManager NM;
	String result;
	Destination dest = new Destination();
	
	 protected void onCreate(Bundle savedInstanceState) {
			
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.mydestination_display);
	       
	        logout = (Button) findViewById(R.id.logout);
	        add = (Button) findViewById(R.id.add);
	        show =(Button) findViewById(R.id.show);
	        
	        logout.setOnClickListener(btnlogout);
	        add.setOnClickListener(btnadd);
	        //show the map
	        show.setOnClickListener(new OnClickListener() {
	    		
	    		@Override
	    		public void onClick(View v) {
	    			
	    			Intent intent = new Intent(DestinationsActivity.this, ProfilActivity.class);
	    			startActivity(intent);
	    		}
	    	});
	        
	        new HttpAsyncTaskGet().execute(url1);
	 }
	 
//	 @Override
//	 protected void onPause() {
//	     super.onPause();
//	     feedBack.dismiss();
//	 }
//	 
//	 public FeedbackSettings SendFeedback()
//	 {
//		 FeedbackSettings feedbackSettings = new FeedbackSettings();
//
//		//SUBMIT-CANCEL BUTTONS
//		feedbackSettings.setCancelButtonText("No");
//		feedbackSettings.setSendButtonText("Send");
//
//		//DIALOG TEXT
//		feedbackSettings.setText("Hey, would you like to give us some feedback so that we can improve your experience?");
//		feedbackSettings.setYourComments("Type your question here...");
//		feedbackSettings.setTitle("Feedback Dialog Title");
//
//		//TOAST MESSAGE
//		feedbackSettings.setToast("Thank you so much!");  
//		feedbackSettings.setToastDuration(Toast.LENGTH_LONG);
//
//		//RADIO BUTTONS
//		feedbackSettings.setRadioButtons(false); // Disables radio buttons
//		feedbackSettings.setBugLabel("Bug");
//		feedbackSettings.setIdeaLabel("Idea");
//		feedbackSettings.setQuestionLabel("Question");
//
//		//RADIO BUTTONS ORIENTATION AND GRAVITY
//		feedbackSettings.setOrientation(LinearLayout.HORIZONTAL);
//		feedbackSettings.setGravity(Gravity.CENTER);
//
//		//SET DIALOG MODAL
//		feedbackSettings.setModal(true); //Default is false
//
//		//DEVELOPER REPLIES
//		feedbackSettings.setReplyTitle("Message from the Developer");
//		feedbackSettings.setReplyCloseButtonText("Close");
//		feedbackSettings.setReplyRateButtonText("RATE!");
//
//		//DEVELOPER CUSTOM MESSAGE (NOT SEEN BY THE END USER)
//		feedbackSettings.setDeveloperMessage("This is a custom message that will only be seen by the developer!");
//		Log.e("feedback_setting", feedbackSettings.toString());
// return feedbackSettings;
//	 }
	 
	 //Parse the JSONArray and display it on TableLayout
	 public void ParseJSONAndAddtoTable(String data)
	 {
		 
		    String id;
			String title;
			String address, category;
			JSONArray jsonArray;
			JSONObject destination;
			final TableLayout table_desti;
		 
			table_desti = (TableLayout) findViewById(R.id.table_destination);
	        
	        try {
	        	//get the json Array that contains the user's destinations from result
				jsonArray = new JSONArray(data);
				
				for(int i=0 ; i < jsonArray.length() ; i++)
				{
					destination = jsonArray.getJSONObject(i);
					Log.e("destination " + i, destination.toString());
					
					id       = destination.getString("_id");
					title    = destination.getString("title");
					address  = destination.getString("address");
					category = destination.getString("category");
					
	       			dest.setId(id);
	       		    dest.setTitle(title);
	       		    dest.setAdresse(address);
	       		    dest.setCategory(category);
					
					Log.e("destination_id", id);
					// set destinations in a TableLayout
					View view = getLayoutInflater().inflate(R.layout.row_table_destination, null);
					
					
					TextView textViewTitle    = (TextView)view.findViewById(R.id.ttl);
					TextView textViewAddress  = (TextView)view.findViewById(R.id.adrs);
					TextView textViewCategory = (TextView)view.findViewById(R.id.cat);
					
	 	         	textViewTitle.setText(title);
	 	         	textViewAddress.setText(address);
	 	         	textViewCategory.setText(category);
 	         	
	 	         	view.setOnClickListener(new OnClickListener() {
 	   			
	 	   			@Override
	 	   			public void onClick(View v) {
 	   				//Display a dialog (which is a small screen) to delete or update the chosen destination or show it on the map
 	   				dialog = new Dialog(DestinationsActivity.this);
 	   				dialog.setTitle("Update My Destination");
 	   	         	dialog.setContentView(R.layout.update_display);
 	   	         	Button cancel  = (Button) dialog.findViewById(R.id.cancel);
 	   	         	Button update  = (Button) dialog.findViewById(R.id.updateAdresse);
 	   	         	Button delete  = (Button) dialog.findViewById(R.id.deleteAdresse);
 	   	         	Button showMap = (Button) dialog.findViewById(R.id.showAdresse);
 	   	         	EditText ttl = (EditText)  dialog.findViewById(R.id.etTitle);
 	   	         	final EditText adr = (EditText)  dialog.findViewById(R.id.etAdresse);
 	   	        	EditText cat = (EditText)  dialog.findViewById(R.id.etCategory);
			        
	 	   	        dialog.show();	
 	   	         	cancel.setOnClickListener(new View.OnClickListener() {
 	   	                 @Override
 	   	                 public void onClick(View view) {
 	   	                     dialog.dismiss();
 	   	                 }
 	   	             });
 	   	         	
 	   	         	ttl.setText(dest.getTitle().toString());
 	   	         	adr.setText(dest.getAdresse().toString());
 	   	         	cat.setText(dest.getCategory().toString());
 	   	        //update button 	
 	   	        update.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
		 				
						   String ttle = ((EditText) dialog.findViewById(R.id.etTitle)).getText().toString();
		 				   String adrs = ((EditText) dialog.findViewById(R.id.etAdresse)).getText().toString();
		 				   String ctgr = ((EditText) dialog.findViewById(R.id.etCategory)).getText().toString();
		 				   
		 				   if(ttle.equals("") || adrs.equals("") || ctgr.equals("")){
		 					   Toast.makeText(DestinationsActivity.this, R.string.empty, Toast.LENGTH_SHORT).show();
		 						return;
		 				   }
		 				   else if(ttle.length()<4)
		 				   {
		 					   Toast.makeText(DestinationsActivity.this, "The title is too short !!", Toast.LENGTH_SHORT).show();
		 						return;
		 				   }
		 				   else if(adrs.length()<10)
		 				   {
		 					   Toast.makeText(DestinationsActivity.this, "Please enter the full address ", Toast.LENGTH_SHORT).show();
		 						return;
		 				   }
		 				  else if(ctgr.length()<4)
		 				   {
		 					  Toast.makeText(DestinationsActivity.this, "Category is too short !! ", Toast.LENGTH_SHORT).show();
		 						return;
		 				   }
		 				   else {
								
								dest.setTitle(ttle);
			 			        dest.setAdresse(adrs);
			 			        dest.setCategory(ctgr);
								
								// get passed intent 
					            Intent intent = getIntent();

					            // get bundle from intent
					            Bundle bundle = intent.getExtras();

					            // get username value from bundle
					            String username = bundle.getString("username");
					            
								
								new HttpAsyncTaskAdd().execute(url2);
								Log.e("des_update", dest.getId().toString());
								new HttpAsyncTaskDelete().execute(url3+dest.getId().toString()+"/"+username);
		 				   }
		 				   
		 				  dialog.dismiss();
					}
				});
 	   	         	//Show Map button, but before that we send the address, by using a Bundle, to googleMap to display the address on the map
 	   	        	//Bundle generally use for passing data between various Activities, bundle can hold all types of values and pass them to the new activity.
 	   	         	showMap.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						// 1. create an intent pass class name or intnet action name 
				        Intent intent = new Intent(DestinationsActivity.this,ProfilActivity.class);
				 
				        // 3. or you can add data to a bundle
				        Bundle extras = new Bundle();
				        extras.putString("adress", adr.getText().toString());
				 
				        // 4. add bundle to intent
				        intent.putExtras(extras);
				 
				        // 5. start the activity
				        startActivity(intent);
						
					}
				});
 	   	         	
 	   	         	//Delete button
 	   	         	delete.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							
							TableRow table_row = (TableRow) findViewById(R.id.table_row);
							table_row.removeAllViews();
							
							// get passed intent from Login activity
				            Intent intent = getIntent();

				            // get bundle from intent
				            Bundle bundle = intent.getExtras();

				            // get username value from bundle
				            String username = bundle.getString("username");
				            
				            //execute the HttpDelete and pass the destination's id and the username on the URL
							new HttpAsyncTaskDelete().execute(url3+dest.getId().toString()+"/"+username);
							
							dialog.dismiss();
						}
					});
 	   	         	
 	   			}
 	   		});
 	         	
 	         	table_desti.addView(view);
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
	 }
	 public String POST(String url, String username, String method){
	        InputStream inputStream = null;
	        String result = "";
	        String json = "";
	        
	        //HttpPost 
	        if(method.equals("getDestination"))
	      {
	        try {
	 
	            // create HttpClient
	            HttpClient httpclient = new DefaultHttpClient();
	 
	            // make POST request to the given URL
	            HttpPost httpPost = new HttpPost(url);
	 
	            // build jsonObject
	            JSONObject jsonObject = new JSONObject();
	            jsonObject.accumulate("username", username);
	 
	            // convert JSONObject to String
	            json = jsonObject.toString();
	            Log.e("json",json);
	 
	            // set json to StringEntity
	            StringEntity se = new StringEntity(json);
	 
	            // set httpPost Entity
	            httpPost.setEntity(se);
	 
	            // Set some headers to inform server about the type of the content   
	            httpPost.setHeader("Accept", "application/json");
	            httpPost.setHeader("Content-type", "application/json");
	 
	            // Execute POST request to the given URL
	            HttpResponse httpResponse = httpclient.execute(httpPost);
	 
	            // receive response as inputStream
	            inputStream = httpResponse.getEntity().getContent();
	 
	            // convert inputstream to string
	            if(inputStream != null)
	            	{
		            	result = convertInputStreamToString(inputStream);
		            	Log.e("get_result",result);
		            	ParseJSONAndAddtoTable(result);
	                }
	            else
	                result = "Did not work!";
	 
	        } catch (Exception e) {
	            Log.d("InputStream", e.getLocalizedMessage());
	        }
	      }
	        //HttpDelete
	        else if(method.equals("deleteDestination"))
	        {
	        	try {
	        		 
		            // create HttpClient
		            HttpClient httpclient = new DefaultHttpClient();
		 
		            // make DELETE request to the given URL
		            HttpDelete httpDelete = new HttpDelete(url);
		 
		            // Set some headers to inform server about the type of the content   
		            httpDelete.setHeader("Accept", "application/json");
		            httpDelete.setHeader("Content-type", "application/json");
		 
		            // Execute POST request to the given URL
		            HttpResponse httpResponse = httpclient.execute(httpDelete);
		 
		            // receive response as inputStream
		            inputStream = httpResponse.getEntity().getContent();
		 
		            
		            if(inputStream != null)
		            	{	
		            		// convert inputstream to string
			            	result = convertInputStreamToString(inputStream);
			                Log.e("delete_result",result);
			            	ParseJSONAndAddtoTable(result);
		                }
		            else
		                result = "Did not work!";
		 
		        } catch (Exception e) {
		            Log.d("InputStream", e.getLocalizedMessage());
		        }
	        }
	        //HttpPost
	        else if(method.equals("addDestination"))
	        {
	        	try {
	        		 
		            // 1. create HttpClient
		            HttpClient httpclient = new DefaultHttpClient();
		 
		            // 2. make POST request to the given URL
		            HttpPost httpPost = new HttpPost(url);
		 
		            // 3. build jsonObject
		            JSONObject jsonObject = new JSONObject();
		            jsonObject.accumulate("username", username);
		            jsonObject.accumulate("title",    dest.getTitle());
		            jsonObject.accumulate("address",  dest.getAdresse());
		            jsonObject.accumulate("category", dest.getCategory());
		 
		            // 4. convert JSONObject to JSON to String
		            json = jsonObject.toString();
		            Log.e("json_add",json);
		 
		            // 5. set json to StringEntity
		            StringEntity se = new StringEntity(json);
		 
		            // 6. set httpPost Entity
		            httpPost.setEntity(se);
		 
		            // 7. Set some headers to inform server about the type of the content   
		            httpPost.setHeader("Accept", "application/json");
		            httpPost.setHeader("Content-type", "application/json");
		 
		            // 8. Execute POST request to the given URL
		            HttpResponse httpResponse = httpclient.execute(httpPost);
		 
		            // 9. receive response as inputStream
		            inputStream = httpResponse.getEntity().getContent();
		 
		            // 10. convert inputstream to string
		            if(inputStream != null)
		            	{

		            	result = convertInputStreamToString(inputStream);
		                Log.e("add_result",result);
		            	ParseJSONAndAddtoTable(result);
	             
		                }
		            else
		                result = "Did not work!";
		            	
		       } catch (Exception e) {
		            Log.d("InputStream", e.getLocalizedMessage());
		        }
	        }
	        // return result
	        return result;
	      }

	 private class HttpAsyncTaskGet extends AsyncTask<String, Void, String> {
			
			@Override
		     protected String doInBackground(String... urls) {
			 
			 // get passed intent 
		     Intent intent = getIntent();

		     // get bundle from intent
		     Bundle bundle = intent.getExtras();

		     // get username value from bundle
		     String username = bundle.getString("username");
		            
		     Log.e("username_recieved", username);
		        	
		     return POST(urls[0], username, "getDestination");
		        }
	    }
	 
	private class HttpAsyncTaskAdd extends AsyncTask<String, Void, String> {
			
			@Override
		     protected String doInBackground(String... urls) {
			 
			 // get passed intent 
		     Intent intent = getIntent();

		     // get bundle from intent
		     Bundle bundle = intent.getExtras();

		     // get username value from bundle
		     String username = bundle.getString("username");
		            
		     Log.e("username_recieved", username);
		        	
		     return POST(urls[0], username, "addDestination");
		        }
	    }

		private class HttpAsyncTaskDelete extends AsyncTask<String, Void, String> {
	        @Override
	        protected String doInBackground(String... urls) {
	 
	        	// get passed intent 
	            Intent intent = getIntent();

	            // get bundle from intent
	            Bundle bundle = intent.getExtras();

	            // get username value from bundle
	            String username = bundle.getString("username");
	            
	            Log.e("username_recieved", username);
	        	
	            return POST(urls[0], username, "deleteDestination");
	        }
	    }
		
		//convert the recieved InputStream to String
		private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	        String line = "";
	        String result = "";
	        while((line = bufferedReader.readLine()) != null)
	            result += line;
	 
	        inputStream.close();
	        return result;
	 }
		
		 // Add destination button
		OnClickListener btnadd = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Display a dialog (which is a small screen) to delete or update the chosen destination or show it on the map
				addDialog = new Dialog(DestinationsActivity.this);
				addDialog.setTitle("Update My Destination");
	         	addDialog.setContentView(R.layout.add_display);
	         	addDestination = (Button) addDialog.findViewById(R.id.addAdresse);
		        Button cancel  = (Button) addDialog.findViewById(R.id.cancel);
		        cancel.setOnClickListener(new View.OnClickListener() {
		                 @Override
		                 public void onClick(View view) {
		                     addDialog.dismiss();
		                 }
		             });
		        addDialog.show();
		        
		        addDestination.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						   String ttle = ((EditText)addDialog.findViewById(R.id.etTitle)).getText().toString();
		 				   String adrs = ((EditText)addDialog.findViewById(R.id.etAdresse)).getText().toString();
		 				   String ctgr = ((EditText)addDialog.findViewById(R.id.etCategory)).getText().toString();
		 				   
		 				   
		 				   if(ttle.equals("") || adrs.equals("") || ctgr.equals("")){
		 					   Toast.makeText(DestinationsActivity.this, R.string.empty, Toast.LENGTH_SHORT).show();
		 						return;
		 				   }
		 				   else if(ttle.length()<4)
		 				   {
		 					   Toast.makeText(DestinationsActivity.this, "The title is too short !!", Toast.LENGTH_SHORT).show();
		 						return;
		 				   }
		 				   else if(adrs.length()<10)
		 				   {
		 					   Toast.makeText(DestinationsActivity.this, "Please enter the full address ", Toast.LENGTH_SHORT).show();
		 						return;
		 				   }
		 				   else if(ctgr.length()<4)
		 				   {
		 					  Toast.makeText(DestinationsActivity.this, "Category is too short !! ", Toast.LENGTH_SHORT).show();
		 						return;
		 				   }
		 				   else {
		 					   
		 					    dest.setTitle(ttle);
		 					    dest.setAdresse(adrs);
		 					    dest.setCategory(ctgr);
		 					    
		 					   	new HttpAsyncTaskAdd().execute(url2);
		 					   	addDialog.dismiss();
		 				   }	
					}
				});
			}
		};
		
		 // Logout button
	    OnClickListener btnlogout = new OnClickListener() {
	    @SuppressWarnings("deprecation")@Override	
	    public void onClick(View v) {
	    	
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
	    
	    }
	    };
}
