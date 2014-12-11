package com.mapsite;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Help extends Activity {

	EditText help;
	String desc="";
	AlertDialog alertDialog;
	Help that=this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		help=(EditText) findViewById(R.id.help);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// set title
		alertDialogBuilder.setTitle("No internet :(");

		// set dialog message
		alertDialogBuilder
			.setMessage("No internet connectivity. Please try again!!")
			.setCancelable(false)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					Help.this.finish();
				}
			  });
		
			// create alert dialog
			alertDialog = alertDialogBuilder.create();
			if(!isNetworkAvailable())
			{
			
				alertDialog.show();
			}
	}

	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.help, menu);
		return true;
	}
	public void submit(View view)
	{
		//new Send().x=this;
		EditText help=(EditText) findViewById(R.id.help);
		new Send().execute(help.getText().toString());
	}


	class Send extends AsyncTask<String, Void, Void> {
	    
		ProgressDialog progress;
		
		//static Help x;
		
		public Send() {
			   progress = new ProgressDialog(Help.this);
			   progress.setMessage("Sending");
			}
		
		protected void onPreExecute()
		{
			progress.setCanceledOnTouchOutside(false);
			progress.show();
		}
		protected Void doInBackground(String... values) {
			
			//EditText help=(EditText) new Help().that.findViewById(R.id.help);
			String desc=values[0];
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://streetvendor.uphero.com/help.php");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	        nameValuePairs.add(new BasicNameValuePair("help", desc));
	        nameValuePairs.add(new BasicNameValuePair("username", Login.myid));
	        try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost); // should be removed from main thread
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				System.out.println("Error in communication");
				e1.printStackTrace();
			}
	        
	        return null;
	    }

	    
	    protected void onPostExecute(Void that) {
	    	progress.dismiss();
	    	Toast.makeText(Help.this.getApplicationContext(), "Submitted !! Thank you for your feedback :)",
					   Toast.LENGTH_SHORT).show();
	     
	    }
	}



}



