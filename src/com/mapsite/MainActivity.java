package com.mapsite;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//COMPRESS BITMAP IMAGE. BITMAP TOO LARGE TO BE UPDATED IN TEXTURE(MAX 2048x2048)
public class MainActivity extends Activity {
	
	
	double latitude;
	double longitude;
	String product1="";
	String product2="";
	String description="";
	String phone="";
	String address="";
	static String ref_value="";
	static String id_value="";
	static Geocoder gcd=null;
	static Context context=null;
	public static final String PREFS_NAME = "MyPrefsFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=getApplicationContext();
        new New_Listing();
        if(!isNetworkAvailable())
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
	 
				// set title
				alertDialogBuilder.setTitle("No internet :(");
	 
				// set dialog message
				alertDialogBuilder
					.setMessage("No internet connectivity. Please try again!!")
					.setCancelable(false)
					.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,int id) {
							// if this button is clicked, close
							// current activity
							MainActivity.this.finish();
						}
					  });
	 
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
		}
        
        gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        
        SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
        String restoredText = set.getString("name", "");
       // description+=restoredText;
        description=restoredText;
        TextView textView = (TextView)findViewById(R.id.name_output);
        textView.setText(restoredText);
        restoredText="";
        restoredText = set.getString("phone", "");
        phone=restoredText;
        textView = (TextView)findViewById(R.id.phone_output);
        textView.setText(restoredText);
        restoredText="";
        restoredText = set.getString("address", "");
        textView = (TextView)findViewById(R.id.address_out);
        textView.setText(restoredText);
        address=restoredText;
        restoredText="";
        restoredText = set.getString("product1", "");
        product1=restoredText;
        textView = (TextView)findViewById(R.id.product_output1);
        textView.setText(restoredText);
        restoredText="";
        restoredText = set.getString("product2", "");
        product2=restoredText;
        textView = (TextView)findViewById(R.id.product_output2);
        textView.setText(restoredText);
        restoredText = set.getString("lat", "");
        System.out.println("lat value"+ restoredText);
        if(isNumeric(restoredText))
        {
        	System.out.println("check1");
        latitude=Double.parseDouble(restoredText);
        	System.out.println("check2");
        }
        textView = (TextView)findViewById(R.id.lat_output);
        textView.setText(restoredText);
        restoredText = set.getString("lng", "");
        if(isNumeric(restoredText))
        {
        longitude=Double.parseDouble(restoredText);
        }
        textView = (TextView)findViewById(R.id.lng_output);
        textView.setText(restoredText);
        
        File imgFile = new  File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyImages/image_001.jpg");
        ImageView myImage = (ImageView) findViewById(R.id.imageView1);
        if(imgFile.exists()){
        	    myImage.setImageBitmap(
        	    	    decodeSampledBitmapFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyImages/image_001.jpg", 100, 100));
        	    
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
    
    public static Bitmap decodeSampledBitmapFromFile(String path,
            int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

            options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(path, options);
      }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
      
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
    
    public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()== R.id.cate)
		{
			Intent intent = new Intent(this, Business.class);
	    	startActivity(intent);
		}
		else if(item.getItemId()== R.id.end)
		{
			MainActivity.this.finish();
		}
		
	            return super.onOptionsItemSelected(item);
	     } 
    
    public void edit_details(View view){
    	Intent intent = new Intent(this, Collect_details.class);
    	//startActivity(intent);
    	startActivityForResult(intent,7);
    	//this.finish();
        }
  
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
  	
  	  
  	  if (requestCode == 7) {
  	        if(responseCode == RESULT_OK){

  	          SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
  	        String restoredText = set.getString("name", "");
  	        description=restoredText;
  	        TextView textView = (TextView)findViewById(R.id.name_output);
  	        textView.setText(restoredText);
  	        restoredText="";
  	        restoredText = set.getString("phone", "");
  	        phone=restoredText;
  	        textView = (TextView)findViewById(R.id.phone_output);
  	        textView.setText(restoredText);
  	        restoredText="";
  	        restoredText = set.getString("address", "");
  	        textView = (TextView)findViewById(R.id.address_out);
  	        textView.setText(restoredText);
  	        address=restoredText;
  	        restoredText="";
  	        restoredText = set.getString("product1", "");
  	        product1=restoredText;
  	        textView = (TextView)findViewById(R.id.product_output1);
  	        textView.setText(restoredText);
  	        restoredText="";
  	        restoredText = set.getString("product2", "");
  	        product2=restoredText;
  	        textView = (TextView)findViewById(R.id.product_output2);
  	        textView.setText(restoredText);
  	        restoredText = set.getString("lat", "");
  	        System.out.println("lat value"+ restoredText);
  	        if(isNumeric(restoredText))
  	        {
  	        	System.out.println("check1");
  	        latitude=Double.parseDouble(restoredText);
  	        	System.out.println("check2");
  	        }
  	        textView = (TextView)findViewById(R.id.lat_output);
  	        textView.setText(restoredText);
  	        restoredText = set.getString("lng", "");
  	        if(isNumeric(restoredText))
  	        {
  	        longitude=Double.parseDouble(restoredText);
  	        }
  	        textView = (TextView)findViewById(R.id.lng_output);
  	        textView.setText(restoredText);
  	        
  	        File imgFile = new  File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyImages/image_001.jpg");
  	        ImageView myImage = (ImageView) findViewById(R.id.imageView1);
  	        if(imgFile.exists()){
  	        	    myImage.setImageBitmap(
  	        	    	    decodeSampledBitmapFromFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/MyImages/image_001.jpg", 100, 100));
  	        	    
  	        }
  	        	
  	        }
  	        
  	    }
  	}


    
    
    private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
    
    public void remove_listing(View view)
	{
		if(!isNetworkAvailable())
		{
			Toast.makeText( getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
			return;
		}
   new Remove().execute(Login.myid);
	}
    
    
    /*AsyncTask for removing listing*/
    
    class Remove extends AsyncTask<String, Void, Boolean> {
    	
    	ProgressDialog progress;
    	
  	  
  	 // New_Listing newlisting = new New_Listing();
  	public void onPreExecute() {
  	 progress = new ProgressDialog(MainActivity.this);
  	progress.setCanceledOnTouchOutside(false);
		 progress.setMessage("Removing Previous Listings !!");
  	    progress.show();
  	  }
    	
    	protected Boolean doInBackground(String... myid)
    	{
    		ResponseHandler res2=new BasicResponseHandler();
        	DefaultHttpClient httpclient1 = new DefaultHttpClient();
        	String check="";
    		try {
    			check = httpclient1.execute(new HttpGet("http://YOURWEBSITE.com/deleteAll.php?id="+Login.myid),res2);
    			System.out.println("Value of check "+check);
    		} catch (ClientProtocolException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	String[] ob=check.split("X");
        	System.out.println(ob[0]);
        	if(!ob[0].equals("NULL")){
        		System.out.println("Obtained after split "+ob[0]);
        		
            	New_Listing.del_result=1;
            	return true;
        	}
        	else
        	{
        		
            	return false;
        	}
    		
    	}
    	
    	protected void onPostExecute(Boolean val)
    	{
    		if(val)
    		{
    			String Text = "Previous listing removed";
            	Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_LONG).show();
    		}
    		else
    		{
    			String Text = "No previous listings available";
            	Toast.makeText( getApplicationContext(), Text, Toast.LENGTH_LONG).show();
    		}
    		progress.dismiss();
    	}
    }
    
    
    
    @SuppressWarnings("deprecation")
	public void add_listing(View view)
    { 
    	//String[] str=new String[10];
    	
    	if(!isNetworkAvailable())
		{
			Toast.makeText( getApplicationContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
			return;
		}
    	
    	if(latitude==0 && longitude==0)
		{
		 AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		 alertDialog.setTitle("Data Unavailable");
		 alertDialog.setMessage("Please Go to Edit Page and fill the form, and then retry.");
		 alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			}
		 
		 })
		 ;
		 alertDialog.show();
		 return;
		}
    	
    	SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
    	
			 ProgressDialog progress = new ProgressDialog(this);
			 progress.setMessage("Adding New Listing. Please Wait !!");
			 
			 
			 new MyTask(progress).execute(this);
			
			 
				 
			
    }
    
   
    public static boolean isNumeric(String str)  
    {  
      try  
      {  
    	  double d = Double.parseDouble(str);  
      }  
      catch(NumberFormatException nfe)  
      {  
        return false;  
      }  
      return true;  
    }
    
    public void exit_Main(View view)
	{
    		
    	String str="";
		  
		  if(New_Listing.ins_result==1 && New_Listing.del_result==1)
		  {
			  str="New listing created";
		  }
		  else if(New_Listing.ins_result==1)
		  {
			  str="New listing created";
		  }
		  else if(New_Listing.del_result==1)
		  {
			  str="Previous Listing Deleted";
		  }
		  else
		  {
			  str="No new Listing added or removed";
		  }
		 
		  Toast.makeText( getApplicationContext(), str, Toast.LENGTH_SHORT).show();	
	
	New_Listing.ins_result=0;
	New_Listing.del_result=0;
	this.finish(); 			
	}
     
    public void onPause()
    {
    	super.onPause();
    	New_Listing.ins_result=0;
    	New_Listing.del_result=0;
    //	this.finish();
    	
    }

}

class MyTask extends AsyncTask<MainActivity,Object,Void> {
	ProgressDialog progress;
	
	  public MyTask(ProgressDialog progress) {
		  progress.setCanceledOnTouchOutside(false);
	    this.progress = progress;
	  }

	  
	  New_Listing newlisting = new New_Listing();
	public void onPreExecute() {
	    progress.show();
	  }

	  public Void doInBackground(final MainActivity... mn ) {
		
		  
		 // New_Listing newlisting = new New_Listing();
	        System.out.println("PHONE : "+mn[0].phone);
	        
	       
	        int t=0;
			if(!mn[0].product1.equalsIgnoreCase(""))
				t=newlisting.POST_NewLoc(mn[0].description,mn[0].phone,mn[0].product1,mn[0].product2,mn[0].latitude,mn[0].longitude,mn[0].address);
			
	   //     this.onPostExecute();
	      
		 // return mn[0];
	  return null;
	  }

	  public void onPostExecute(Void v) {
		  progress.dismiss();
		  
	    
	   		
	}
	  
	  
}

class New_Listing {
public static int ins_result=0;
public static int del_result=0;
//public static  String ref=""; 
//public static  String id="";
public static final String PREFS_NAME = "MyPrefsFile";
//public static String myid=Login.myid;
public int POST_NewLoc(String description,String phone, String product,String others, double latitude, double longitude, String address) 
	{
		
	 HttpPost httpost1 = new HttpPost("http://YOURWEBSITE.com/newinsert.php"); 
	 HttpPost httpost2 = new HttpPost("http://YOURWEBSITE.com/image_upload.php");
	// HttpPost httpost3 = new HttpPost("http://YOURWEBSITE.com/location_insert.php");
	if(latitude==0 && longitude==0)
	{
		return -1;
	}
	ResponseHandler res2=new BasicResponseHandler();
	DefaultHttpClient httpclient1 = new DefaultHttpClient();

	String check="";
	try {
		check = httpclient1.execute(new HttpGet("http://YOURWEBSITE.com/deleteAll.php?id="+Login.myid),res2);
	} catch (ClientProtocolException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	} catch (IOException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	String[] ob=check.split("X");
	if(!ob[0].equals("NULL")){
		del_result=1;
		
	}
	/*till here*/
		JSONObject json = new JSONObject();
		JSONObject json_loc = new JSONObject();
		JSONObject json2 = new JSONObject();
		
		
		JSONObject k;
		String x;
		Object y;
		/*Nearby search to find the nearest place*/
		ResponseHandler responseHandler2 = new BasicResponseHandler();;
		HttpGet hg=new HttpGet("https://maps.googleapis.com/maps/api/place/nearbysearch/json?"+"location="+latitude+","+longitude+"&radius=20000"+"&keyword="+product+"&language=en"+"&key=AIzaSyCjAL0t-UwKwYHBfuCzhw21JRs_tcl7cLw");
		HttpClient httpclientn = new DefaultHttpClient();
		String resp="";
		try {
			httpclientn.execute(hg,responseHandler2);
			resp=httpclientn.execute(hg,responseHandler2);
		} catch (ClientProtocolException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		System.out.println(resp);
		JSONObject json_nloc=null;
		String g_pid="";
		try {
			json_nloc= new JSONObject(resp);
			JSONArray arr=json_nloc.getJSONArray("results"); 
			g_pid=arr.getJSONObject(0).get("id").toString();
			System.out.println("Nearest place id "+g_pid);
			
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			g_pid="";
			System.out.println("Error handling response in nearby search");
			e1.printStackTrace();
		}
	
		if(g_pid.equals(""))
		{
			String admin_area="";
			String country_code="";
			String locality="";
			
			List<Address> addresses;
			try {
				addresses = MainActivity.gcd.getFromLocation(latitude, longitude, 1);
				/*if (addresses.size() > 0) 
				    System.out.println(addresses.get(0).getAdminArea());*/
				admin_area=addresses.get(0).getAdminArea();
				country_code=addresses.get(0).getCountryCode();
				locality=addresses.get(0).getLocality();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			g_pid+=admin_area+"_"+locality+"_"+country_code;
		}
		
		String temp=description.replaceAll(" ", "_");
		HttpGet httpget3 = new HttpGet("http://YOURWEBSITE.com/location_insert.php?"+"id="+Login.myid+"&pid="+g_pid+"&lat="+latitude+"&lon="+longitude+"&product="+product+"&name="+temp);
		try {
			httpclientn.execute(httpget3);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
				
			json2.put("Shop_name",description);
			json2.put("Address",address);   //......... (to be included later)
			json2.put("Phone",phone);
			json2.put("others",others);
			json2.put("id",g_pid);
			json2.put("URL",Login.myid+".jpg");
			json2.put("username",Login.myid);
			
			System.out.println(json2);
			} catch (JSONException e) {
			// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		   
        StringEntity se1=null;
		try {
			se1 = new StringEntity(json2.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//sets the post request as the resulting string
	    httpost1.setEntity(se1);
	    ResponseHandler responseHandler = null;
	    responseHandler = new BasicResponseHandler();
	    y=null;
	    try {
			y=httpclient.execute(httpost1, responseHandler);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println("Y : "+y);
	    if(y.toString().equals(""))
	    {
	    	ins_result=0;
	    }
	    else
	    {
	    	ins_result=1;
	    }
	    if(new StorageHelper().isExternalStorageAvailableAndWriteable())
	    {
	    	
	    	  File file=null;
	  	    System.out.println(Environment.getExternalStorageDirectory());
	          String imgpath=Environment.getExternalStorageDirectory()+"/MyImages/image_001.jpg";
	          System.out.println("passed");
	          file = new File(imgpath);
	    	
		   
	          //String[] files=null;
	    	   
	        	
	        	
	    ByteArrayOutputStream stream = new ByteArrayOutputStream();
	    BitmapFactory.Options opts = new BitmapFactory.Options ();
	    opts.inSampleSize = 5;   // for 1/2 the image to be loaded
	    System.out.println("image path "+imgpath);
	    Bitmap bitmap;
	    if(file.exists())
	    {
	    bitmap = Bitmap.createScaledBitmap (BitmapFactory.decodeFile(imgpath,opts), 100, 100, false);
	    }
	    else
	    {
	    	bitmap=BitmapFactory.decodeResource( MainActivity.context.getResources(), R.drawable.ic_launcher);
	    }
	    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream); //compress to which format you want.
     
        byte [] byte_arr=new byte[(int)file.length()];
	    byte_arr = stream.toByteArray();
	    String image_str = Base64.encodeToString(byte_arr,0);
        ArrayList<NameValuePair> nameValuePairs = new  ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("image",image_str));
        nameValuePairs.add(new BasicNameValuePair("param1", Login.myid+".jpg"));
        HttpResponse response=null ;
        try {
			httpost2.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			response = httpclient.execute(httpost2);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        System.out.println("image Response : "+response);
       
        
	    }
    	
	 return 1;   
	}
		
}


	

