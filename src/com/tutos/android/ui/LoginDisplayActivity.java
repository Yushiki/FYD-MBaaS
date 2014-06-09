package com.tutos.android.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tutos.android.ui.vo.User_Sub;

public class LoginDisplayActivity extends Activity {

	EditText username,password,name,email;
	Button joinButton;
	User_Sub sub;
	NotificationManager NM;
	private String url1 = "http://192.168.52.50:8080/signup";
	final String EXTRA_LOGIN = "user_login";
	final String EXTRA_PASSWORD = "user_password";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_display);
        
        name =(EditText) findViewById(R.id.etName);
        email = (EditText) findViewById(R.id.etEmail);
        username = (EditText) findViewById(R.id.etUsername);
		password = (EditText) findViewById(R.id.etpass);
		joinButton = (Button) findViewById(R.id.join);
        
        joinButton.setOnClickListener(joinBtn);

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
                Log.e("tag_login", username.getText().toString());
                
                if(suc)
        		{
        			Intent in = new Intent(LoginDisplayActivity.this,DestinationsActivity.class);
        			
        			String title = "Welcome "+username.getText().toString();
        		    String subject = name.getText().toString();
        		    String body = email.getText().toString();
        		    NM=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        		    Notification notify=new Notification(R.drawable.ic_launcher,title,System.currentTimeMillis());
        		    PendingIntent pending=PendingIntent.getActivity(getApplicationContext(),0, new Intent(),0);
        		    notify.setLatestEventInfo(getApplicationContext(),subject,body,pending);
        		    NM.notify(0, notify);
        		    
        		     // put key/value data
        	        in.putExtra("message", "Hana jit");
        	        
        	        // 2. add data to a bundle
        	        Bundle extras = new Bundle();
        	        extras.putString("username", username.getText().toString());
        	 
        	        // 3. add bundle to intent
        	        in.putExtras(extras);
        		    
        		    startActivity(in);
        			
                	Log.e("res", msg);
        		}
        		else 
        			Toast.makeText(LoginDisplayActivity.this, "Username already exist !!", Toast.LENGTH_LONG).show();
                }
            else
                result = "Did not work!";
 
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
 
        // 11. return result
        return result;
    }
    
    				OnClickListener joinBtn = new OnClickListener(){

    					@Override
    					public void onClick(View v) {
    						
    final String nameTxt = name.getText().toString();
    final String emailTxt = email.getText().toString();
    final String usernameTxt = username.getText().toString();
    final String passTxt = password.getText().toString();
    
    if(nameTxt.equals("") || emailTxt.equals("") || usernameTxt.equals("") || passTxt.equals("")) {
    Toast.makeText(LoginDisplayActivity.this,R.string.empty,Toast.LENGTH_SHORT).show();
    	return;
    }
    
    else if(nameTxt.length()<6){

    Toast.makeText(LoginDisplayActivity.this,R.string.name_format_error,Toast.LENGTH_SHORT).show();
                            
    	return;
    }
    						
    else if(usernameTxt.length()<5){
    	Toast.makeText(LoginDisplayActivity.this, R.string.username_format_error, Toast.LENGTH_SHORT).show();
    	return;
    }
    
    else if(passTxt.length()<6){
    	Toast.makeText(LoginDisplayActivity.this, R.string.password_format_error, Toast.LENGTH_SHORT).show();
    	return;
    }
    
    else if(!isEmailValid(emailTxt)){
    	Toast.makeText(LoginDisplayActivity.this, R.string.email_format_error, Toast.LENGTH_SHORT).show();
    	return;
    }
    
    else {
    							
        switch(v.getId()){
        case R.id.join:
            if(!validate())
            Toast.makeText(getBaseContext(), "ERROR : INVALIDE form !!", Toast.LENGTH_LONG).show();
             // call AsynTask to perform network operation on separate thread
    	     new HttpAsyncTask().execute(url1);
	         Log.e("data2",url1);
	         break;
    }					
    }
		}
	};
	
	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
 
            sub = new User_Sub();
            sub.setName(name.getText().toString());
            sub.setEmail(email.getText().toString());
            sub.setUsername(username.getText().toString());
            sub.setPassword(password.getText().toString());
 
            return POST(urls[0],sub);
        }
    }
 
    private boolean validate(){
    		 if(name.getText().toString().trim().equals(""))
    		return false;
    	else if(email.getText().toString().trim().equals(""))
    		return false;
    	else if(username.getText().toString().trim().equals(""))
            return false;
        else if(password.getText().toString().trim().equals(""))
            return false;
        else 
            return true;    
    }
    
    //function to make sure that the user has entered a valid email
    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }
    
    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_display, menu);
        return true;
    }
}
