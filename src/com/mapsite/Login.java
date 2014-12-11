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
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.example.android.trivialdrivesample.util.IabHelper;
import com.example.android.trivialdrivesample.util.IabResult;
import com.example.android.trivialdrivesample.util.Inventory;
import com.example.android.trivialdrivesample.util.Purchase;
public class Login extends Activity {

	
	static boolean login_status=false;
	static char cat_i;
	static char cat_b;
	static String user="";
	static String category="";
	static String pass="";
	static String ph="";
	String PREFS_NAME="PreferenceFile";
	static String myid="";
	static EditText usernm,pwd,phone;
	static Spinner spinner;
	AlertDialog alertDialog;
	//IabHelper mHelper;
	IInAppBillingService mService;
	ServiceConnection mServiceConn;
	private Context mContext=this;
	IabHelper mHelper;
	static final String ITEM_SKU = "business";
	
	
	
	
	
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener= new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, 
	                    Purchase purchase) 
		{
		   if (result.isFailure()) {
		      // Handle error
		      return;
		 }      
		 else if (purchase.getSku().equals(ITEM_SKU)) {
		     consumeItem();
		  //  buyButton.setEnabled(false);
		}
		      
	   }
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		
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
						Login.this.finish();
					}
				  });
			
				// create alert dialog
				alertDialog = alertDialogBuilder.create();
				if(!isNetworkAvailable())
				{
				
					alertDialog.show();
				}
				
		
		
		
		usernm=(EditText) findViewById(R.id.usernm);
		pwd=(EditText) findViewById(R.id.pwd);
		phone=(EditText) findViewById(R.id.phone_no);
		 SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
		Login.ph=set.getString("phone", "");
		Login.user=set.getString("username", "");
		usernm.setText(Login.user);
		phone.setText(Login.ph);
		
				
		String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAhI8EHbmK/BHYJGixqIxujOcVuWtOLOma/3kmW4POoiuGvO4t2eh/Qgw+wG8sxtY0sooAMX6YZozt/69soRioygDjrxW2nbHcD3ATgxsNDzhrvAaBmufE7sEetVd+QyXL2KRHdgYcx1szRpSVpU78BOhpT4Hqxxeui8PIynhqZ+qHxKAOkXRk+kwxdbWZsiZmuQ2HJsL2Vr/GBP42p+dLqfcTqk4uikWNfeBY8hgeL3ZZjS5DeYBd5BopYuj29O5IprRS+8qNC3ilRCQVQ27nj2UD9CTR8lh+M4uDjctkNO8Bi/quZmeZHl6H/9qq2cxLeuc47gHio040gRl+BMbWqQIDAQAB";

		mHelper = new IabHelper(this, base64EncodedPublicKey);

		mHelper.startSetup(new 
		IabHelper.OnIabSetupFinishedListener() {
		public void onIabSetupFinished(IabResult result) 
		{
		if (!result.isSuccess()) {
		  System.out.println("In-app Billing setup failed: " + result);
		} else {             
			System.out.println("In-app Billing is set up OK");
		}
		}
		});
		
		
		
		
				 
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void onResume()
	{
		super.onResume();
		 SharedPreferences set = getSharedPreferences(PREFS_NAME, 0);
			Login.ph=set.getString("phone", "");
			Login.user=set.getString("username", "");
			usernm.setText(Login.user);
			phone.setText(Login.ph);
		
	}
	
	
	
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()== R.id.endlogin)
		{
			this.finish();
		}
		
		
	            return super.onOptionsItemSelected(item);
	     } 
	
	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mService != null) {
	        unbindService(mServiceConn);
	    } 
	}
	
	
	
	
		 public boolean testDetails(String str)
		 {
			 return str.matches("^[A-Za-z0-9 ]+$");
		 }

		 String account_price="";
		 
		 
		 final IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
				  new IabHelper.OnConsumeFinishedListener() {
				   public void onConsumeFinished(Purchase purchase, 
			             IabResult result) {

				 if (result.isSuccess()) {		    	 
				   	 // clickButton.setEnabled(true);
				 } else {
				         // handle error
				 }
			  }
			};
		 
		 IabHelper.QueryInventoryFinishedListener mReceivedInventoryListener 
		   = new IabHelper.QueryInventoryFinishedListener() {
			   public void onQueryInventoryFinished(IabResult result,
			      Inventory inventory) {

				   		   
			      if (result.isFailure()) {
				  // Handle failure
			      } else {
		                 mHelper.consumeAsync(inventory.getPurchase(ITEM_SKU), 
					mConsumeFinishedListener);
			      }
		    }
		};
	public void signUp(View view)
	{
		
		if(Login.phone.getText().toString().equalsIgnoreCase(""))
		{
		
		Toast.makeText(getApplicationContext(), "Please enter a phone number !!",
				   Toast.LENGTH_LONG).show();
		return;
		
		
		}
		else if(Login.usernm.getText().length()<5 || Login.pwd.getText().length()<5)
		{
			Toast.makeText(getApplicationContext(), "Username and password should be atleast 5 characters !!",
					   Toast.LENGTH_LONG).show();
			return;
		}
		else if(!(testDetails(Login.usernm.getText().toString())&&testDetails(Login.usernm.getText().toString())))
				{Toast.makeText(getApplicationContext(), "Username and password can contain alphabets, numbers or both !!",
						   Toast.LENGTH_LONG).show();
				return;}
		
	/*
	 * Uncomment LINE 1 and COMMENT out LINE 2 to enable in app billing
	 * 
	 */
		
		
		//	     mHelper.launchPurchaseFlow(this, ITEM_SKU, 10001,mPurchaseFinishedListener, "mypurchasetoken");   // .. LINE 1
			
			new SignUp().execute(this); // .. LINE 2
			
	}
	
	
	public void consumeItem() {
		mHelper.queryInventoryAsync(mReceivedInventoryListener);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, 
	     Intent data) 
	{
	      if (!mHelper.handleActivityResult(requestCode, 
	              resultCode, data)) {     
	    	super.onActivityResult(requestCode, resultCode, data);
	      }
	      else if (resultCode == RESULT_OK)
	      {
	    	  new SignUp().execute(this);
	      }
	      else
	      {
	    	  Toast.makeText(getApplicationContext(), "No accounts were created !!", Toast.LENGTH_LONG).show();
	      }
	}
	
	void alert(String title, String message)
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		 
		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
			.setMessage(message)
			.setCancelable(true)
			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, close
					// current activity
					
				}
			  });

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();	
	}
	
	
	public void signIn(View view)
	{
		if(Login.usernm.getText().length()<5 || Login.pwd.getText().length()<5)
		{
			Toast.makeText(getApplicationContext(), "Username and password should be atleast 5 characters !!",
					   Toast.LENGTH_LONG).show();
			return;
		}
		
		new SignIn().execute(this);
		SharedPreferences pref = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("phone", Login.ph);
		editor.putString("username", Login.user);
		editor.commit();
		
}
	private boolean isNetworkAvailable() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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


class SignIn extends AsyncTask<Login, Void, Login> {
    
	ProgressDialog progress;
	
	public SignIn() {
		   progress = new ProgressDialog(Login.this);
		   progress.setMessage("Signing In");
		}
	protected void onPreExecute()
	{
		progress.setCanceledOnTouchOutside(false);
		progress.show();
	}
	
	protected Login doInBackground(Login... that) {
       
        //String username=Login.user;
        //String password=Login.pass;
        HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://streetvendor.uphero.com/secret_signIn.php");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", Login.usernm.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("password", Login.pwd.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("phone", Login.phone.getText().toString()));
        System.out.println("Username "+Login.usernm.getText().toString());
        System.out.println("Password "+Login.pwd.getText().toString());
        System.out.println("Phone "+Login.phone.getText().toString());
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				ResponseHandler<String> responseHandler = new BasicResponseHandler();
				 
				String result= httpclient.execute(httppost,responseHandler);
				System.out.println("Result after sign in "+result);
				/*if(result.charAt(0)=='I')
				{
					Login.cat='I';
					Login.login_status=true;
					Login.user=Login.usernm.getText().toString();
					Login.pass=Login.pwd.getText().toString();
					Login.ph=Login.phone.getText().toString();
					Login.myid=Login.user+"_"+Login.pass+"_"+Login.ph+"_"+Login.category;
				}*/
				if(result.charAt(0)=='B')
				{
					
					Login.cat_b='B';
					
					Login.login_status=true;
					Login.user=Login.usernm.getText().toString();
					Login.pass=Login.pwd.getText().toString();
					Login.ph=Login.phone.getText().toString();
					Login.myid=Login.user+"_"+Login.pass+"_"+Login.ph+"_"+Login.category;
					
				}
				else
				{
					Login.login_status=false;
				}
				System.out.println("Sign In done");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        
        return that[0];
    }

    
    protected void onPostExecute(Login that) {
     //showDialog();   
    	System.out.println("Post execute");
    	if(Login.login_status)
    	{
    		SharedPreferences pref = that.getSharedPreferences("PreferenceFile", 0);
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("phone", Login.ph);
			editor.putString("username", Login.user);
			editor.commit();
    		Toast.makeText(that.getApplicationContext(), "Login Successful !!",Toast.LENGTH_SHORT).show();
    		progress.dismiss(); 
    		Bundle bundle = new Bundle();
  		  
  		 bundle.putString("login_data","loggedin"); 
  		Intent intent = new Intent(that, StreetVendorMain.class);
  		intent.putExtras(bundle);
  		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
  		setResult(RESULT_OK,intent);
  //	that.startActivity(intent);
  	Login.this.finish();
    	}
    	else
    	{
    		progress.dismiss(); 
    		Toast.makeText(that.getApplicationContext(), "Login Unsuccessful !!",Toast.LENGTH_SHORT).show();
    	}
    }
}


class SignUp extends AsyncTask<Login, Void, Login> {
    
	ProgressDialog progress;
	public SignUp() {
		   progress = new ProgressDialog(Login.this);
		   progress.setMessage("Signing Up");
		}
	protected void onPreExecute()
	{
		progress.setCanceledOnTouchOutside(false);
		progress.show();
	}
	
	protected Login doInBackground(Login... that) {
      //  Login.category=(String)Login.spinner.getSelectedItem();
        System.out.println("category "+Login.category);
        System.out.println("Phone "+Login.phone.getText().toString());
        HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://streetvendor.uphero.com/secret_signUp.php");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("username", Login.usernm.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("password", Login.pwd.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("phone", Login.phone.getText().toString()));
        nameValuePairs.add(new BasicNameValuePair("category", "Business"));
       try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = httpclient.execute(httppost);
			
			
				
				Login.login_status=true;
				Login.user=Login.usernm.getText().toString();
				Login.pass=Login.pwd.getText().toString();
				Login.ph=Login.phone.getText().toString();
				
				Login.myid=Login.user+"_"+Login.pass+"_"+Login.ph+"_"+Login.category;
				System.out.println("My id "+Login.myid);
				/*if(Login.category.equals("Business"))
				{
					Login.cat='B';
				}
				else
				{
					Login.cat='I';
				}*/
			
				Login.cat_b='B';
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return that[0];
    }

    
    protected void onPostExecute(Login that) {
     //showDialog();
    	if(that==null)
    	{
    		return;
    	}
    	
    	SharedPreferences pref = that.getSharedPreferences("PreferenceFile", 0);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString("phone", Login.ph);
		editor.putString("username", Login.user);
		editor.commit();
    	Toast.makeText(that.getApplicationContext(), "Login Successful !!",
				   Toast.LENGTH_SHORT).show();
    	progress.dismiss();
    	Bundle bundle = new Bundle();
		  
		  bundle.putString("login_data","loggedin"); 
		Intent intent = new Intent(that, StreetVendorMain.class);
		intent.putExtras(bundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		setResult(RESULT_OK,intent);	
//	that.startActivity(intent);
	Login.this.finish();
    }

}
}
