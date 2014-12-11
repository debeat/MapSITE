package com.mapsite;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;

public class Business extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_business);
		WebView webview = new WebView(this);
		 setContentView(webview);
		 
		  new Task().execute(webview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.business, menu);
		return true;
	}

}


class Task extends AsyncTask<WebView,WebView,WebView>
{
	String business="";
	protected WebView doInBackground(WebView... web)
	{	
		 ResponseHandler responseHandler = null;
	  	    responseHandler = new BasicResponseHandler();
	  	    String y="";
	  	  DefaultHttpClient httpclient = new DefaultHttpClient();
	  	HttpPost httpost = new HttpPost("http://YOURWEBSITE.com/business.php");
	  	  try {
			y=httpclient.execute(httpost, responseHandler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	String[] arr=y.split("[+]");
	  	
	  	int i;
	  	System.out.println("Array length "+arr.length);
	  	System.out.println(arr[0]);
		for(i=0;i<arr.length-1;i++)
		{
			
			business+="<br>"+arr[i]+"<br>";
			
		}
		System.out.println("business");
		//onPostExecute(web[0]);
		return web[0];
	
	}
	
	protected void onPostExecute(WebView webview) {
		 
	  	  
		  	
	  	 /*changed !!*/
	        String imgSrcHtml ="<html>"+
	        		"<h1>BUSINESS CATEGORIES !!</h1>"+
	        		business+
	        		
	  	 "</html>";
	        webview.loadData(imgSrcHtml, "text/html", "utf-8");
}
}