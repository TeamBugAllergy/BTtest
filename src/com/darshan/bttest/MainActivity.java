package com.darshan.bttest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	Button on;
	Button off;
	Button chat;

	
	
	int BT_ENABLE = 1;
	BluetoothAdapter ba;

	public static int START_SERVER_THREAD = 1;
	public static Handler MainHandler = new Handler(){
		
		public void handleMessage(Message msg) {
        
			if(msg.what == START_SERVER_THREAD)
			{
				
				createServer();
				Log.d("Inside MainActivity Handler",":)");
			}
			
		}
	};
	
	public static void createServer()
	{
		//TODO 
		//creat and start a server thread
		
		ServerThread server_thread;
		
		//*****AGAIN start****** the server to listen for connections 
		Log.d("ServerActivity", "creating a server thread to listen for requests");
		//text.setText("creating a server thread");
		server_thread = new ServerThread();
		
		Log.d("ServerActivity", "calling the run method of ServerThread to start listening");
		//text.setText("calling the run method");
		server_thread.start();
		
		Log.d("Inside createServer()",":)");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ba = BluetoothAdapter.getDefaultAdapter();

		if(ba != null)	//only if device supports bluetooth
		{
			on = (Button) findViewById(R.id.on);
			on.setOnClickListener(this);
			off = (Button) findViewById(R.id.off);
			off.setOnClickListener(this);

			chat = (Button) findViewById(R.id.chat);
			chat.setOnClickListener(this);
		}
		
	}
		
	@Override
	public void onClick(View v) {

		if(v.getId() == on.getId())//switch on the bluetooth
		{
			if(ba.isEnabled() == false)		//if bluetooth is currently disabled
			{
				Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(i, BT_ENABLE);		//start the bluetooth
			}
						
		}
		else if(v.getId() == off.getId())//switch off the bluetooth
		{
			if(ba.isEnabled() == true)
			{
				ba.disable();
				Log.d("MainActivity", "BT disabled");
			}
			
		}
		else if(v.getId() == chat.getId())//ClientActivity and client thread
		{
			
			if(ba.isEnabled() == true)
			{
				Intent i = new Intent(this, ClientActivity.class);
			
				startActivity(i);

				/* inside a function createServer()
				//Also start the server to listen for connections
				Log.d("ServerActivity", "creating a server thread to listen for requests");
				//text.setText("creating a server thread");
				server_thread = new ServerThread();
			
				Log.d("ServerActivity", "calling the run method of ServerThread to start listening");
				//text.setText("calling the run method");
				server_thread.start();
			
				 */
				//Instead of above code,call the public method
				createServer();
			}
			else
				Toast.makeText(this, "Please switch on the bluetooth", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	
		if(requestCode == BT_ENABLE)
		{
			if(resultCode == RESULT_OK)
			{
				Log.d("MainActivity", "BT enabled");
			}
			else
			{
				Log.d("MainActivity", "BT still disabled");
			}
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
