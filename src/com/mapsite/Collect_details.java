package com.mapsite;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;


public class Collect_details extends Activity implements OnFocusChangeListener {
	
		//Thread th;
	final static int ACTIVITY_CREATE = 1;
	ProgressDialog rec_dialog;
	EditText product1=null;
	EditText product2=null;
	EditText addressField=null;
    EditText phoneField=null;   
    EditText nameField=null; 
	EditText words;
	static int selected=0; // shopName
	double latitude=0.0;
	double longitude=0.0;
	int check=1;
    protected static final int REQUEST_OK = 1;
	static LocationManager mlocManager,m1locManager;
	static LocationListener mlocListener;
	public static final int MEDIA_TYPE_IMAGE = 1;
	static boolean gps=false;// 1 GPS , 0 N/W
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_collect_details);
		product1=(EditText) findViewById(R.id.details3);
		product1.setFocusable(true);
		product1.setOnFocusChangeListener(this);
		product2=(EditText) findViewById(R.id.details4);
		product2.setFocusable(true);
		product2.setOnFocusChangeListener(this);
		addressField = (EditText) findViewById(R.id.details5);
		addressField.setFocusable(true);
		addressField.setOnFocusChangeListener(this);
		phoneField= (EditText) findViewById(R.id.details2);
		phoneField.setFocusable(true);
		//phoneField.setText(Login.ph);
		//phoneField.setEnabled(false);
		phoneField.setEnabled(true); //UNCOMMENT THE ABOVE TWO LINE IF YOU WANT A BUSINESS LOGIN
		phoneField.setOnFocusChangeListener(this);
		nameField= (EditText) findViewById(R.id.details1);  
		nameField.setFocusable(true);
		nameField.setOnFocusChangeListener(this);
		/* Use the LocationManager class to obtain GPS locations */
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        m1locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
     
        try
        {
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        Toast.makeText(getApplicationContext(), "Enable location from network by clicking on the options button, if GPS takes too long", Toast.LENGTH_SHORT);
        }
        catch(Exception e)
        {
        	Toast.makeText(getApplicationContext(), "Please start GPS manually and retry", Toast.LENGTH_LONG);
        }
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
		

	public void onFocusChange(View v, boolean hasFocus) {

		
		if(v.getId()==R.id.details1)
		{
			selected=1;
		}
		else if(v.getId()==R.id.details2)
		{
			selected=2;
		}
		else if(v.getId()==R.id.details3)
		{
			selected=3;
		}
		else if(v.getId()==R.id.details4)
		{
			selected=4;
		}
		else if(v.getId()==R.id.details5)
		{
			selected=5;
		}
	}
	
	
	public static final String PREFS_NAME = "MyPrefsFile";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		getMenuInflater().inflate(R.menu.activity_collect_details, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
        //super.onOptionsItemSelected(item);

      if(item.getItemId()==R.id.net) {

               try
               {
        	m1locManager.requestLocationUpdates( LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
        	Toast.makeText( Collect_details.this.getApplicationContext(), "Obtaining location from network.", Toast.LENGTH_SHORT).show();
               }
               catch(Exception e)
               {
               	Toast.makeText(getApplicationContext(), "Cannot fetch Location from Network", Toast.LENGTH_LONG);
               }
               }
               
        return super.onOptionsItemSelected(item);
 } 	
	
	
	
	 public void OnClick(View view){
		 
 		   Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
 		  File imagesFolder=null;
 		   if(new StorageHelper().isExternalStorageAvailableAndWriteable())
 		   {
	       imagesFolder = new File(Environment.getExternalStorageDirectory(), "MyImages");
 		   
	       imagesFolder.mkdirs(); // <----
	       File image = new File(imagesFolder, "image_001.jpg");
	       Uri uriSavedImage = Uri.fromFile(image);
	       imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
	       startActivityForResult(imageIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
 		   }
 		   else
 		   {
 			  Toast.makeText( getApplicationContext(), "External Storage not available", Toast.LENGTH_SHORT).show();
 			     
 		   }

  		    }
		
	
	 @SuppressWarnings("deprecation")
	public void submit(View view)
	 {
		 /*if(th.isAlive())
		 {th.interrupt();}*/
		if(latitude==0 && longitude==0)
		{
		 AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		 alertDialog.setTitle("Location unknown");
		 alertDialog.setMessage("Please wait for GPS coordinates to arrive. Otherwise enable location from Network.");
		 alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		 public void onClick(DialogInterface dialog, int which) {
		 // here you can add functions
			
		 }
		 });
		 alertDialog.show();
		 return;
		}
		
		 EditText nameField = (EditText) findViewById(R.id.details1);  
		 String name = nameField.getText().toString();
		 EditText phoneField = (EditText) findViewById(R.id.details2);  
		 String phone = phoneField.getText().toString();
		 System.out.println("Phone number "+phone);
		 EditText productField = (EditText) findViewById(R.id.details3);  
		 String product1 = productField.getText().toString();
		 EditText productField1 = (EditText) findViewById(R.id.details4);  
		 String product2 = productField1.getText().toString();
		 EditText addressField = (EditText) findViewById(R.id.details5);  
		 String address = addressField.getText().toString();
		// HindiToEnglish hte = new HindiToEnglish();
		 String Eng_product1 = product1;
		 String Eng_product2 = product2;
		 int j=0;char ch;
		
		 while(j<Eng_product1.length())
		 {
			 ch=Eng_product1.charAt(j);
			 if(ch==' ')
			 { String Text = "Product1 should not contain spaces";
	        	Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
			 return;
			 }
			 j++;
		 }
		 //i=0;
		 
		if(!(testDetails(name)&&testDetails(Eng_product1)&&testDetails(Eng_product2)&&testDetails(address)))
		{
			Toast.makeText(getApplicationContext(), "Text box can contain only alphabets", Toast.LENGTH_LONG).show();
			return;
		}
		/*else if(!testPhone(phone))
		{
			Toast.makeText(getApplicationContext(), "Phone number can contain only digits from 0 to 9", Toast.LENGTH_LONG).show();	
			return;
		}
	     */
		 SharedPreferences pref = getSharedPreferences(PREFS_NAME, 0);
	     SharedPreferences.Editor editor = pref.edit();
         editor.putString("name", name);
         editor.putString("phone", phone);
         editor.putString("product1", Eng_product1);
         editor.putString("product2", Eng_product2);
         editor.putString("address", address);
         editor.putString("lat",String.valueOf(latitude));
         editor.putString("lng",String.valueOf(longitude));
         editor.commit();
         
         Intent intent = new Intent(this, MainActivity.class);
       //  intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
      //   startActivity(intent);
         setResult(RESULT_OK,intent);
         this.finish();
		 
		}
	 
	 private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	 

	 public boolean testDetails(String str)
	 {
		 return str.matches("^[A-Za-z ]+$");
	 }
	 
	/* public boolean testPhone(String str)
	 {
		 return str.matches("^[0-9]$");
	 }*/
	 
	 
	 @Override
	 protected void onActivityResult (int requestCode, int resultCode, Intent Data) {
	     if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	         if (resultCode == RESULT_OK) {
	             // Image captured and saved to fileUri specified in the Intent
	        	 String Text = "Image Saved";
		        	Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
	         } else if (resultCode == RESULT_CANCELED) {
	             // User cancelled the image capture
	         } else {
	             // Image capture failed, advise user
	         }
	     }
	         else if(requestCode==REQUEST_OK  && resultCode==RESULT_OK)
	         {
	        	 final ArrayList<String> thingsYouSaid = Data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
	        	 switch(this.selected)
	        	 {
	        	 case 1:
	        		 nameField.setText(thingsYouSaid.get(0));
	        		 break;
	        	 case 2:
	        		 phoneField.setText(thingsYouSaid.get(0));
	        		 break;
	        	 case 3:
	        		 product1.setText(thingsYouSaid.get(0));
	        		 break;
	        	 case 4:
	        		 product2.setText(thingsYouSaid.get(0));
	        		 break;
	        	 case 5:
	        		 addressField.setText(thingsYouSaid.get(0));
	        		 break;
	        	 }
	         }
	     
	     mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	

	 }
	
	 /* Class My Location Listener */
	    public class MyLocationListener implements LocationListener
	    {

	    	 double THRESHOLD=5000;
	    	 double diff;
	      public void onLocationChanged(Location loc)
	      {
	    	  diff=System.currentTimeMillis()-loc.getTime();
	    	  if(diff > THRESHOLD)
	    	  {
	    	  	//Toast.makeText( getApplicationContext(), "Exit : "+ diff, Toast.LENGTH_SHORT).show();
	    	  	Toast.makeText( getApplicationContext(), "Old location rejected!! Retrying", Toast.LENGTH_SHORT).show();
	    	  	return;
	    	  }

	       latitude= loc.getLatitude();
	       longitude= loc.getLongitude();
	       String Text;
	       
	        Text = "My current location is: " +
	        "Latitud = " + loc.getLatitude() +
	       "Longitud = " + loc.getLongitude();
	       
	        if(check==1)
	        {
	        	Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_SHORT).show();
	        	check++;
	        }
	      
	      }

	      public void onProviderDisabled(String provider)
	      {
	        Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
	      }

	      
	      public void onProviderEnabled(String provider)
	      {
	        Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
	      }

	      
	      public void onStatusChanged(String provider, int status, Bundle extras)
	      {

	      }
	    }
	    
	    @Override
	    public void onPause() {
	    	super.onPause();
	    	
	        mlocManager.removeUpdates(mlocListener);
	       
	    }
	 public void onResume()
	 {
		 super.onResume();
		 /*if(th.isInterrupted())
		 {th.start();}*/
		 mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
	 }

	  public void Speak(View v) {
		  System.out.println("View called");
	    	Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    	         i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
	    	        	 try {
	    	             startActivityForResult(i, REQUEST_OK);
	    	         } catch (Exception e) {
	    	        	 	Toast.makeText(this, "Error initializing speech to text engine.", Toast.LENGTH_LONG).show();
	    	         }
	    	}
		
}
