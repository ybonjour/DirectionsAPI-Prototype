package ch.yvu.prototypedirectionapi;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.Handler;
import android.util.Log;

public class DirectionsRequest extends Thread {

	private static final String TAG = "DirectionsRequest";
	
	private String mOrigin;
	private String mDestination;
	
	private Handler mHandler;
	private Runnable mUpdateRunnable;
	
	private Response mResponse;
	
	public DirectionsRequest(String strOrigin, String strDestination, Handler handler, Runnable updateRunnable, Response response) {
		if(handler == null) throw new IllegalArgumentException();
		if(updateRunnable == null) throw new IllegalArgumentException();
		
		mOrigin = strOrigin;
		mDestination = strDestination;
		mHandler = handler;
		mUpdateRunnable = updateRunnable;
		mResponse = response;
	}
	
	@Override
	public void run() {
    	String strURL = parseURL(mOrigin, mDestination);

    	Log.i(TAG, String.format("Request URL: %s", strURL));
    	
    	HttpURLConnection conn = null;
   		try
   		{
   			URL connectURL = new URL(strURL);
   			conn = (HttpURLConnection)connectURL.openConnection(); 

   			// do some setup
	   		conn.setDoInput(true); 
	   		conn.setDoOutput(true); 
	   		conn.setUseCaches(false); 
	   		conn.setRequestMethod("GET"); 
	
	   		// connect and flush the request out
	   		conn.connect();
	   		conn.getOutputStream().flush();
	   		
	   		mResponse.setResponse(getResponse(conn));

   		}
   		catch(Exception e)
   		{
   			Log.e(TAG, "Error while requesting directions", e);
   		}
   		finally
   		{
   			if(conn != null)
   			{
   				conn.disconnect();
   			}
   		}
   		
   		mHandler.post(mUpdateRunnable);
	}
	
	private String parseURL(String strOrigin, String strDestination)
    {
		//URL-Encoding for Arguments
		strOrigin = URLEncoder.encode(strOrigin);
		strDestination = URLEncoder.encode(strDestination);
		
    	return "http://maps.google.com/maps/api/directions/json?origin="
    		+ strOrigin + "&destination=" + strDestination+"&sensor=false";
    }
	
   private String getResponse(HttpURLConnection conn) {
    	if(conn == null) throw new IllegalArgumentException();
    	
    	InputStream is = null;
		try 
		{
			is = conn.getInputStream(); 
			// scoop up the reply from the server
			int ch; 
			StringBuffer sb = new StringBuffer(); 
			while( ( ch = is.read() ) != -1 ) { 
				sb.append( (char)ch ); 
			} 
			return sb.toString(); 
		}
		catch(Exception e)
		{
			Log.e(TAG, "biffed it getting HTTPResponse", e);
		}
		finally 
		{
			try {
			if (is != null)
				is.close();
			} catch (Exception e) {}
		}
		
		return "";
	}

	
}
