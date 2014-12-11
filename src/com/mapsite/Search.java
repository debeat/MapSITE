package com.mapsite;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Search extends Activity   {

	EditText words;

	protected static final int REQUEST_OK = 1;	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search);
		
		//findViewById(R.id.speak).setOnClickListener(this);
		
	}
	
	  public boolean onOptionsItemSelected(MenuItem item) {
			
			if(item.getItemId()== R.id.cate){
				Intent intent = new Intent(this, Business.class);
		    	startActivity(intent);
			}
		    	else if(item.getItemId()==R.id.end){
				Search.this.finish();
			}
			
		            return super.onOptionsItemSelected(item);
		     } 

	
	public void Speak(View v) {
	Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	         i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
	        	 try {
	             startActivityForResult(i, REQUEST_OK);
	         } catch (Exception e) {
	        	 	Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
	         }
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        if (requestCode==REQUEST_OK  && resultCode==RESULT_OK) {
	        		final ArrayList<String> thingsYouSaid = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        		//((TextView)findViewById(R.id.text1)).setText(thingsYouSaid.get(0));
	        		
	        		EditText searchField = (EditText) findViewById(R.id.text1);  
	        		searchField.setText(thingsYouSaid.get(0));
	        }
	    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
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
	
	public void location(View view)
	 {
		final EditText infoField = (EditText) findViewById(R.id.text1);  
		 String info = infoField.getText().toString();
		 String Eng_info="";
		
			Eng_info +=info;
		 System.out.println(Eng_info);
		 char ch;
		 int i=0;
		 /*while(i<Eng_info.length())
		 {
			 ch=Eng_info.charAt(i);
			 if(ch==' ')
			 { String Text = "Product name should not contain spaces";
	        	Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
			 return;
			 }
			 i++;
		 }*/
		 
		 if(!testDetails(Eng_info))
		 {
			 Toast.makeText(getApplicationContext(), "Search box Should only contain alphabets without spaces", Toast.LENGTH_SHORT).show();
			 return;
		 }
		 
		 //Create the bundle
		  Bundle bundle = new Bundle();
		  //Add your data to bundle
		  bundle.putString("stuff",Eng_info); 
		 
		  Intent intent = new Intent(this, MyLocation.class);
		  intent.putExtras(bundle);
		  	startActivity(intent);
		  	this.finish();
		  	

	 }
	 
	 		
		
	 public boolean testDetails(String str)
	 {
		 return str.matches("^[a-zA-Z]+$");
	 }
		
		}
		



