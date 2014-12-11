package com.mapsite;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;


public class PlaceDetailsActivity extends Activity {

	
	 static String reference="";
	 static String name="";
	 static boolean flag=true;
	WebView webview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_details);

		
		super.onCreate(savedInstanceState);
		webview = new WebView(this);
		 setContentView(webview);
		 
		 
		 	reference = getIntent().getStringExtra("id");
		 	String[] ref=reference.split(";");
		 	PlaceDetailsTask.email=ref[1];
	        name=getIntent().getStringExtra("name");
	  //      flag=getIntent().getStringExtra("flag");
	   //     System.out.println("Value of flag : "+flag);
	         new PlaceDetailsTask().execute(webview);
	        
}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK))
	    {
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	
}



class PlaceDetailsTask extends AsyncTask<WebView,Void,WebView>
{
	String shop_name,address,phone,Url="",lat,lng,reference,comments="",username="";
	String others="";
	static String imgSrcHtml="";
	String name="";
	static String email="";
	protected WebView doInBackground(WebView... web)
	{	
		 JSONObject k,js;
			String x="";
			DefaultHttpClient httpclient = new DefaultHttpClient();
		 // Getting place reference from the map
	        reference =PlaceDetailsActivity.reference;
	        name=PlaceDetailsActivity.name;
	     //if(PlaceDetailsActivity.flag.equalsIgnoreCase("false"))
	     //{
	    	 JSONObject json4= new JSONObject();
	        HttpPost httpost3 = new HttpPost("http://YOURWEBSITE.com/comment_get.php");
	        JSONObject json3 = new JSONObject();
	        HttpPost httpost2 = new HttpPost("http://YOURWEBSITE.com/select.php");
	        try {
				json3.put("Reference_number",reference);
				json4.put("Reference_number",reference);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        StringEntity se2,se3;
			try {
				se2 = new StringEntity(json3.toString());
				httpost2.setEntity(se2);
				se3 = new StringEntity(json4.toString());
				httpost3.setEntity(se3);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
			
	  	 
	  	    
	  	  ResponseHandler responseHandler = null;
	  	    responseHandler = new BasicResponseHandler();
	  	    String y="";
	  	    try {
				x=httpclient.execute(httpost2, responseHandler);
				y=httpclient.execute(httpost3, responseHandler);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  	    //System.out.println("SELECT : "+x);
	  	  try {
		    	k=new JSONObject(x);
				shop_name=k.getString("ShopName");
				address=k.getString("address");
				phone=k.getString("phone");
				Url=k.getString("image_url");
				others=k.getString("Other items");
				username=k.getString("username").split(";")[0].replace('~', ' ');
				System.out.println("y "+y);
				String[] arr=y.split("[+]");
				int i;
				for(i=0;i<arr.length-1;i++)
				{
					
					comments+="<br>"+arr[i]+"<br>";
					
				}
				System.out.println("list "+arr[0]);
				System.out.println("i value "+i);
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  	  
	  	
	  byte[] imageRaw = null;
	  	try {
	  		
	  	 URL url = new URL("http://YOURWEBSITE.com/"+Url);
	  	 HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	  	 InputStream in = new BufferedInputStream(urlConnection.getInputStream());
	  	 ByteArrayOutputStream out = new ByteArrayOutputStream();

	  	 int c;
	  	 while ((c = in.read()) != -1) {
	  	     out.write(c);
	  	 }
	  	 out.flush();

	  	 imageRaw = out.toByteArray();

	  	 urlConnection.disconnect();
	  	 in.close();
	  	 out.close();
	  	 } catch (IOException e) {
	  	 // TODO Auto-generated catch block
	  	 e.printStackTrace();
	  	 }
	  //	onPostExecute(web[0]);
	  	return web[0];
	        }
	     
	    
	    	
	     
	  
		
	
	protected void onPostExecute(WebView webview) {
		 String urlStr   = "http://YOURWEBSITE.com/"+Url;
	  	  
		  	
	  	 /*changed !!*/
		 //System.out.println("Username : "+Login.user);
		 //String usernm=Login.user;
		 String imgSrcHtml;
		 String[] id =username.split("_");
		 if(Login.login_status)
	        {

			 
			 imgSrcHtml ="<html>"+
	                "<body><h1><center>"+shop_name+"</center></h1>" +
	                "<br style='clear:both' />" + "<img src='" + urlStr + "'>"+
	                		"<p>Owner's Username : "+id[0]+"</p>"+
	                		 "<p>Owner's email id : "+email+"</p>"+
	                        "<p>Address : " + address + "</p>" +
	                        "<p>Phone : " + phone + "</p>" +
	                        "<p>Main product : " + name + "</p>" +
	                        "<p>Other Items : " + others + "</p>" +
	                        "<form action=\"http://YOURWEBSITE.com/comment_post.php\" method=\"post\"> Write a Comment :<br />" +
	                        " <textarea name=\"comment\" id=\"comment\"></textarea>" +
	                        
	                        "<textarea name=\"usernm\" id=\"usernm\" style=\"display:none;\">"+Login.user+"</textarea>" +
	                        "<textarea type=\"text\" name=\"ref\" id=\"id\" style=\"display:none;\">"+reference+"</textarea>"+
	                        "<br /><input type=\"submit\" value=\"Submit\" /></form>"+
	                        "<p>"+"Comments : "+comments+"</p>"+
   	                        "</body></html>";
	        
	        }
		 else
		 {
			 imgSrcHtml ="<html>"+
		                "<body><h1><center>"+shop_name+"</center></h1>" +
		                "<br style='clear:both' />" + "<img src='" + urlStr + "'>"+
		                "<p>Owner's Username : "+id[0]+"</p>"+
		                "<p>Owner's email id : "+email+"</p>"+
		                        "<p>Address : " + address + "</p>" +
		                        "<p>Phone : " + phone + "</p>" +
		                        "<p>Main product : " + name + "</p>" +
		                        "<p>Other Items : " + others + "</p>" +
		                        
		                        "<p>Please LOGIN to comment !!"+
		                        "<p>"+"Comments : "+comments+"</p>"+
	   	                        "</body></html>";
		 }
	        
	     
	       webview.loadData(imgSrcHtml, "text/html", "utf-8");
	           
	        
		
    }
	
	
}
