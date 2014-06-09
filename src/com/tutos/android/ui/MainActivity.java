package com.tutos.android.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tutos.android.ui.vo.User_Sub;

public class MainActivity extends Activity {
	
	EditText login,pass;
	Button loginButton,joinButton;
	private String url1 = "http://192.168.52.50:8080/signin";
	User_Sub user;
	NotificationManager NM;
	
	final String EXTRA_LOGIN = "user_login";
	final String EXTRA_PASSWORD = "user_password";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		login = (EditText) findViewById(R.id.username);
		pass = (EditText) findViewById(R.id.user_password);
		loginButton = (Button) findViewById(R.id.connect);
		joinButton = (Button) findViewById(R.id.join);
		
		login.setText("Yushiki");
		pass.setText("60597edd");
        
        joinButton.setOnClickListener(joinBtn);
 
        // add click listener to Button "login"
        loginButton.setOnClickListener(loginBtn);
 
    }	
 			
	@SuppressWarnings("deprecation")
	public String POST(String url, User_Sub user){
        InputStream inputStream = null;
        String msg = "";
        String result ="";
        String json = "";
        Boolean suc = false;
        try {
 
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
 
            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);
 
            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("password", user.getPassword());
            jsonObject.accumulate("username", user.getUsername());
 
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            Log.e("json",json);
 
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
            result = convertInputStreamToString(inputStream);
            Log.e("result",result);
            
            if(inputStream != null)
            	{
            	
            	JSONObject reader = new JSONObject(result);

                suc = reader.getBoolean("success");
                msg = reader.getString("message");
                
                Log.e("tag_json_suc", suc.toString());
                Log.e("tag_json_msg", msg);
                
                if(suc)
        		{
        			Intent in = new Intent(MainActivity.this,DestinationsActivity.class);
        			
        		      //"Hello" notification
        			String title = "Hey "+login.getText().toString()+" ;)";
        		    String subject = "Welcome back ^_^";
        		    String body = "Looking for somthing ?!";
        		    NM=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        		    Notification notify=new Notification(R.drawable.ic_launcher,title,System.currentTimeMillis());
        		    PendingIntent pending=PendingIntent.getActivity(getApplicationContext(),0, new Intent(),0);
        		    notify.setLatestEventInfo(getApplicationContext(),subject,body,pending);
        		    NM.notify(0, notify);
        	 
        	        // 2. add data to a bundle
        	        Bundle extras = new Bundle();
        	        extras.putString("username", login.getText().toString());
        	 
        	        // 3. add bundle to intent
        	        in.putExtras(extras);
        			
        			startActivity(in);
                	Log.e("login_if", login.getText().toString());
        		}
        		else 
        			Toast.makeText(MainActivity.this, "Username already exist !!", Toast.LENGTH_LONG).show();
                }
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        // 11. return result
        return result;
    }
			//the join button
			OnClickListener joinBtn = new OnClickListener(){

				@Override
				public void onClick(View v) {
					
					Intent intent = new Intent(MainActivity.this,LoginDisplayActivity.class);
					
					startActivity(intent);	
				}
			};
	
		
			OnClickListener loginBtn = new OnClickListener() {

				@Override
			public void onClick(View v) {
				
				final String loginTxt = login.getText().toString();
				final String passTxt = pass.getText().toString();

				if (loginTxt.equals("") || passTxt.equals("")) {
					Toast.makeText(MainActivity.this, R.string.email_or_password_empty, Toast.LENGTH_SHORT).show();
					return;
				}

				else if (login.length()<5) {
					/* Toast est une classe fournie par le SDK Android
					 pour afficher les messages dans des minis pop up
					 Le premier argument est le Context, puis
					 le message et à la fin la durée d'affichage de ce dernier*/
					
					Toast.makeText(MainActivity.this,R.string.username_format_error, Toast.LENGTH_SHORT).show();
					return;
				}
				
				else if(pass.length()<6){
					Toast.makeText(MainActivity.this, R.string.password_format_error, Toast.LENGTH_SHORT).show();
					return;
				}
				
				else {
					switch(v.getId()){
		            case R.id.connect:
		                if(!validate())
		                Toast.makeText(getBaseContext(), "ERROR : INVALIDE Login !!", Toast.LENGTH_LONG).show();
		                // call AsynTask to perform network operation on separate thread
		                Log.e("data2",url1);
		                new HttpAsyncTask().execute(url1);
		                break;
		        }
				}
		}
	};
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
            user = new User_Sub();
            user.setUsername(login.getText().toString());
            user.setPassword(pass.getText().toString());
 
            return POST(urls[0],user);
        }
    }
	
    private boolean validate(){
        if(login.getText().toString().trim().equals(""))
            return false;
        else if(pass.getText().toString().trim().equals(""))
            return false;
        else 
            return true;    
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
}
