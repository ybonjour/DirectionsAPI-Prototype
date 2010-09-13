package ch.yvu.prototypedirectionapi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PrototypeDirectionAPI extends Activity implements Response {
    
	private EditText mStart;
	private EditText mDestination;
	private Button mRequest;
	private TextView mResult;
	
	final Handler mHandler = new Handler();
	
	//String to save Response from server
	private String mResponse;
	
	final Runnable mUpdateResult = new Runnable(){
		public void run()
		{
			mResult.setText(getResponse());
		}
	};
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Get Views
        mStart = (EditText) findViewById(R.id.Start);
        mDestination = (EditText) findViewById(R.id.Destination);
        mRequest = (Button) findViewById(R.id.Request);
        mResult = (TextView) findViewById(R.id.Result);
        
        //Add onClick Listener to Request Buton
        mRequest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startRequest();
			}
		});
    }
    
    private void startRequest()
    {
    	String strOrigin = mStart.getText().toString();
    	String strDestination = mDestination.getText().toString();
    	DirectionsRequest requestThread = new DirectionsRequest(strOrigin, strDestination, mHandler, mUpdateResult, this);
    	requestThread.start();
    }
    
	//Implement Response (thread safe)
	public synchronized String getResponse()
	{
		return mResponse;
	}
	
	public synchronized void setResponse(String strValue)
	{
		mResponse = strValue;
	}
}