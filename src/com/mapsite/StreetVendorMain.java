package com.mapsite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

public class StreetVendorMain extends Activity implements
 OnClickListener, com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks, com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener {
	
	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;

	/* Store the connection result from onConnectionFailed callbacks so that we can
	 * resolve them when the user clicks sign-in.
	 */
	private ConnectionResult mConnectionResult;
	
	/* Request code used to invoke sign in user interactions. */
	  private static final int RC_SIGN_IN = 0;

	  /* Client used to interact with Google APIs. */
	  private GoogleApiClient mGoogleApiClient;

	  private boolean mIntentInProgress;
	  
	  /* A flag indicating that a PendingIntent is in progress and prevents
	   * us from starting further intents.
	   */

	SignInButton btnSignIn;
	Button btnSignOut;
	final Context context = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_street_vendor_main);
		
		
		btnSignIn = (SignInButton) findViewById(R.id.btn_sign_in);
		btnSignIn.setOnClickListener(this);
		btnSignOut = (Button) findViewById(R.id.btn_sign_out);
		btnSignOut.setOnClickListener(this);
		
		if(Login.login_status && Login.cat_b=='B')
		{
		Button login=(Button)findViewById(R.id.login_button);
		Button logout=(Button)findViewById(R.id.logout_button);
		login.setVisibility(View.GONE);
		logout.setVisibility(View.VISIBLE);
		}
		
		
		 // Initializing google plus api client
       /* mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).addApi(Plus.API, null)
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();*/
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(Plus.API)
        .addScope(Plus.SCOPE_PLUS_LOGIN)
        
        .build();
	}
	
	
	protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }
 
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
      /*  Login.login_status=false;
		Login.myid="";
		Login.cat_b='f';*/
    }
 
   /* public void onResume()
    {
    	super.onResume();
    	Bundle bundle = getIntent().getExtras();

	    //Extract the data…
	   
		if(bundle!=null )
		{
			 String loginstatus = bundle.getString("login_data");
			 if(loginstatus.equals("loggedin"))
			{Button login=(Button)findViewById(R.id.login_button);
			Button logout=(Button)findViewById(R.id.logout_button);
			login.setVisibility(View.GONE);
			logout.setVisibility(View.VISIBLE);}
		}
    }*/
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK))
	    {
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
    
    /**
     * Button on click listener
     * */
    @Override
    public void onClick(View v) {
    	if(v.getId()==R.id.btn_sign_in)
    	{
    		signInWithGplus();
    	}
    	else if (v.getId()==R.id.btn_sign_out)
    	{
    		signOutFromGplus();
    		Login.myid="";
    		Login.login_status=false;
    		Login.cat_i='f'; // arbitrary character
    	}
       
    }


/**
 * Sign-in into google
 * */
private void signInWithGplus() {
    if (!mGoogleApiClient.isConnecting()) {
        mSignInClicked = true;
        resolveSignInError();
    }
}
 
/**
 * Method to resolve any signin errors
 * */
private void resolveSignInError() {
    if (mConnectionResult.hasResolution()) {
        try {
            mIntentInProgress = true;
            mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
        } catch (SendIntentException e) {
            mIntentInProgress = false;
            mGoogleApiClient.connect();
        }
    }
}


protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
	  if (requestCode == RC_SIGN_IN) {
	    if (responseCode != RESULT_OK) {
	      mSignInClicked = false;
	    }

	    mIntentInProgress = false;

	    if (!mGoogleApiClient.isConnecting()) {
	      mGoogleApiClient.connect();
	    }
	  }
	  
	  if (requestCode == 8) {
	        if(responseCode == RESULT_OK){
	        	Bundle bundle = intent.getExtras();

	    	    //Extract the data…
	    	   
	    		if(bundle!=null )
	    		{
	    			 String loginstatus = bundle.getString("login_data");
	    			 if(loginstatus.equals("loggedin"))
	    			{Button login=(Button)findViewById(R.id.login_button);
	    			Button logout=(Button)findViewById(R.id.logout_button);
	    			login.setVisibility(View.GONE);
	    			logout.setVisibility(View.VISIBLE);}
	    		}
	        }
	        
	    }
	}


/**
 * Sign-out from google
 * */
private void signOutFromGplus() {
    if (mGoogleApiClient.isConnected()) {
        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
        mGoogleApiClient.disconnect();
        mGoogleApiClient.connect();
        updateUI(false);
    }
}

/**
 * Revoking access from google
 * */
/*private void revokeGplusAccess() {
    if (mGoogleApiClient.isConnected()) {
        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
        Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status arg0) {
                        //Log.e(TAG, "User access revoked!");
                        mGoogleApiClient.connect();
                        updateUI(false);
                    }
 
                });
    }
}*/

		@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
			
			//menu.add(0, 1, Menu.NONE, "Business Categories");
			
		     
		getMenuInflater().inflate(R.menu.street_vendor_main, menu);
		return true;
	}
		 
		public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==R.id.cate)
		{
		
			Intent intent = new Intent(this, Business.class);
	    	startActivity(intent);
		}
	    	else if(item.getItemId()== R.id.end){
			StreetVendorMain.this.finish();
		}
		
	            return super.onOptionsItemSelected(item);
	     } 
				
		
		public void help(View view){
			if(Login.login_status)
			{
	    	Intent intent = new Intent(this, Help.class);
	    	startActivity(intent);
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Please Login to use this feature !!",
						   Toast.LENGTH_SHORT).show();
			}
	    	

	    }
		
	public void newVendor(View view){
		//if(Login.login_status && Login.cat_b=='B')    // IF YOU WANT TO PROVIDE A BUSINESS LOGIN UNCOMMENT THIS LINE AND COMMENT OUT THE NEXT LINE
	    if(Login.login_status) 
		{
    	Intent intent = new Intent(this, MainActivity.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    	startActivity(intent);
		}
		else
		{
			//Toast.makeText(getApplicationContext(), "Please Login as Business category to use this feature !!,Toast.LENGTH_SHORT).show()"; //UNCOMMENT
			Toast.makeText(getApplicationContext(), "Please Sign in first!!",Toast.LENGTH_SHORT).show(); //COMMENT OUT
		}
    	

    }
	public void search(View view){
    	Intent intent = new Intent(this, Search.class);
    	intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
    	startActivity(intent);
    	

    }
	
	public void login(View view){
    	//Intent intent = new Intent(this, Login.class);
		Intent intent = new Intent(this, Login.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivityForResult(intent,8);
    	

    }

	public void logout(View view)
	{
		Login.login_status=false;
		Login.myid="";
		Login.cat_b='f';
		Button login=(Button)findViewById(R.id.login_button);
		Button logout=(Button)findViewById(R.id.logout_button);
		login.setVisibility(View.VISIBLE);
		logout.setVisibility(View.GONE);
		
	}
	 
	  
	 @Override
	    public void onConnectionFailed(ConnectionResult result) {
	        if (!result.hasResolution()) {
	            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
	                    0).show();
	            return;
	        }
	 
	        if (!mIntentInProgress) {
	            // Store the ConnectionResult for later usage
	            mConnectionResult = result;
	 
	            if (mSignInClicked) {
	                // The user has already clicked 'sign-in' so we attempt to
	                // resolve all
	                // errors until the user is signed in, or they cancel.
	                resolveSignInError();
	            }
	        }
	 
	    }
	 

	 @Override
	    public void onConnected(Bundle arg0) {
	        mSignInClicked = false;
	        Toast.makeText(this, "You are Logged in !!", Toast.LENGTH_SHORT).show();
	 
	        // Get user's information
	        getProfileInformation();
	 
	        // Update the UI after signin
	        updateUI(true);
	 
	    }
	 
	    /**
	     * Updating the UI, showing/hiding buttons and profile layout
	     * */
	    private void updateUI(boolean isSignedIn) {
	        if (isSignedIn) {
	            btnSignIn.setVisibility(View.GONE);
	            btnSignOut.setVisibility(View.VISIBLE);
	           // btnRevokeAccess.setVisibility(View.VISIBLE);
	           // llProfileLayout.setVisibility(View.VISIBLE);
	        } else {
	            btnSignIn.setVisibility(View.VISIBLE);
	            btnSignOut.setVisibility(View.GONE);
	           // btnRevokeAccess.setVisibility(View.GONE);
	           // llProfileLayout.setVisibility(View.GONE);
	        }
	    }
	 
	    /**
	     * Fetching user's information name, email, profile pic
	     * */
	    private void getProfileInformation() {
	        try {
	            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
	                Person currentPerson = Plus.PeopleApi
	                        .getCurrentPerson(mGoogleApiClient);
	                String personName = currentPerson.getDisplayName();
	               // String personPhotoUrl = currentPerson.getImage().getUrl();
	               // String personGooglePlusProfile = currentPerson.getUrl();
	                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
	                Login.user=personName;
	                Login.myid=personName.replace(' ', '~')+";"+email; // should not be _ since names can contain "_"
	                Login.login_status=true;
	                Login.cat_i='I';
	                
	
	                //txtName.setText(personName);
	                //txtEmail.setText(email);
	 
	                
	            } else {
	                Toast.makeText(getApplicationContext(),
	                        "Person information is null", Toast.LENGTH_LONG).show();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	 
	    @Override
	    public void onConnectionSuspended(int arg0) {
	        mGoogleApiClient.connect();
	        updateUI(false);
	    }
	 
	
	
}
