package com.darshan.bttest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ServerActivity extends Activity implements OnClickListener{
	
	ServerThread server;
	
	EditText text;
	Button btn;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		//listen for requests from client
		setContentView(R.layout.activity_server);
		text = (EditText)findViewById(R.id.server_text);
		text.setText("Write Here");

		//to send the data
		btn = (Button)findViewById(R.id.server_button);
		btn.setOnClickListener(this);
		
		//initially disabled
		btn.setVisibility(View.INVISIBLE);
/*  testing
		Log.d("ServerActivity", "creating a server thread to listen for requests");
		//text.setText("creating a server thread");
		server = new ServerThread(handler);
		
		Log.d("ServerActivity", "calling the run method of ServerThread to start listening");
		//text.setText("calling the run method");
		server.start();
*/		
	}
	
	// Define the Handler that receives messages from the thread and update the progress
		private Handler handler = new Handler() {
				
			public void handleMessage(Message msg) {

				if(msg.what == 2)
				//enable the send button 
				btn.setVisibility(View.VISIBLE);
			}
	    };
	    
	@Override
	public void onClick(View v) {

		if(v.getId() == btn.getId())
		{
			server.sendDataToClient( text.getText().toString() );
		}
	}
		
}
