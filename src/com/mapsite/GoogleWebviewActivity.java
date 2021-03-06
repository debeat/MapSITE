package com.mapsite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;

public class GoogleWebviewActivity extends Activity {
	WebView webview;
	static String reference="";
	 static String name="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_webview);
		webview = new WebView(this);
		 setContentView(webview);
		 
		 
		 	reference = getIntent().getStringExtra("reference");
	        name=getIntent().getStringExtra("name");
	        System.out.println("reference in GOOGLE view : "+reference);
	  //      flag=getIntent().getStringExtra("flag");
	   //     System.out.println("Value of flag : "+flag);
	        //PlaceDetailsActivity.flag=false;
			StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
	        //sb.append("reference="+reference);
			sb.append("reference="+reference);
	       // sb.append("&sensor=true");
	        sb.append("&key=AIzaSyDu4Q3hxaTmMOvvS83aVqdL0eJNFU2MNlk");//YOUR_BROWSER_API_KEY_FOR_PLACES);

	        // Creating a new non-ui thread task to download Google place details
	        PlacesTask placesTask = new PlacesTask();
	 
	        // Invokes the "doInBackground()" method of the class PlaceTask
	        System.out.println("URL : "+sb.toString());
	        placesTask.execute(sb.toString());
	         //new PlaceDetailsTask().execute(webview);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.google_webview, menu);
		return true;
	}

	
	
	
	   /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
 
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
 
            // Connecting to url
            urlConnection.connect();
 
            // Reading data from url
            iStream = urlConnection.getInputStream();
 
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
 
            StringBuffer sb = new StringBuffer();
 
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }
 
            data = sb.toString();
                br.close();
 
        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    
    
    
    /** A class, to download Google Place Details */
    private class PlacesTask extends AsyncTask<String, Integer, String>{
 
        String data = null;
 
        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();
 
            // Start parsing the Google place details in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }
    }
 
    /** A class to parse the Google Place Details in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, HashMap<String,String>>{
 
        JSONObject jObject;
 
        // Invoked by execute() method of this object
        @Override
        protected HashMap<String,String> doInBackground(String... jsonData) {
 
            HashMap<String, String> hPlaceDetails = null;
            PlaceDetailsJSONParser placeDetailsJsonParser = new PlaceDetailsJSONParser();
 
            try{
                jObject = new JSONObject(jsonData[0]);
                System.out.println("JObject Google view : "+jObject);
                // Start parsing Google place details in JSON format
                hPlaceDetails = placeDetailsJsonParser.parse(jObject);
 
            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return hPlaceDetails;
        }
 
        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(HashMap<String,String> hPlaceDetails){
 
        	String imgSrcHtml;
            String name = hPlaceDetails.get("name");
            if(name==null)
            {
            	imgSrcHtml="<html>" +
            			"<h1>No Data Received !! Sorry ...</h1>" +
            			"</html>";
            }
            else
            {
            String icon = hPlaceDetails.get("icon");
            String vicinity = hPlaceDetails.get("vicinity");
            String lat = hPlaceDetails.get("lat");
            String lng = hPlaceDetails.get("lng");
            String formatted_address = hPlaceDetails.get("formatted_address");
            String formatted_phone = hPlaceDetails.get("formatted_phone");
            String website = hPlaceDetails.get("website");
            String rating = hPlaceDetails.get("rating");
            String international_phone_number = hPlaceDetails.get("international_phone_number");
            String url = hPlaceDetails.get("url");
            
            String mimeType = "text/html";
            String encoding = "utf-8";
 
            imgSrcHtml = "<html>"+
                          "<body><img style='float:left' src="+icon+" /><h1><center>"+name+"</center></h1>" +
                          "<br style='clear:both' />" +
                          "<hr />"+
                          "<p>Vicinity : " + vicinity + "</p>" +
                          "<p>Location : " + lat + "," + lng + "</p>" +
                          "<p>Address : " + formatted_address + "</p>" +
                          "<p>Phone : " + formatted_phone + "</p>" +
                          "<p>Website : " + website + "</p>" +
                          "<p>Rating : " + rating + "</p>" +
                          "<p>International Phone : " + international_phone_number + "</p>" +
                          "<p>URL : <a href='" + url + "'>" + url + "</p>" +
                          "</body></html>";
            }
            // Setting the data in WebView
            webview.loadData(imgSrcHtml, "text/html", "utf-8");
        }
    }
}
