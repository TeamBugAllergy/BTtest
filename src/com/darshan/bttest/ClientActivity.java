package com.darshan.bttest;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ClientActivity extends Activity implements OnClickListener, OnItemClickListener{
	
	final int MAX_NUM_DEVICES = 10;//maximum number of devices
	
	public static ClientActivity me;
	
	Button scan;
	BluetoothDevice[] dev;//array of devices
	int dev_no = 0; //index for the device
	BluetoothAdapter ba;
	
	ListView list;
	ArrayList<String> itemsList;
	ArrayAdapter<String> adapter;
	
	MyDialog d;
	
	// Define the Handler that receives messages from the thread and update the progress
	public Handler handler = new Handler() {
			
		public void handleMessage(Message msg) {
        	
			if(msg.what == ClientThread.CONNECTION_FAILED)
			{
				d.setTitle("ERROR");
				d.text.setText("Please make sure the device "+d.cur_device+" supports BTtest.");
				d.pro.setVisibility(View.GONE);
				d.ok.setVisibility(View.VISIBLE);
			}
			else if(msg.what == ClientThread.CONNECTION_SUCCESS)
			{
				gotoMessagesActivity();
			}
        }
    };
	    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.activity_client);
	
		me = this;
		
		scan = (Button) findViewById(R.id.scan);
		scan.setOnClickListener(this);
		
		//BluetoothAdapter
		ba = BluetoothAdapter.getDefaultAdapter();
		
		//allocate the memory
		dev = new BluetoothDevice[MAX_NUM_DEVICES];
		
		list = (ListView)findViewById(R.id.list_device);
		itemsList = new ArrayList<String>();
		//use the deafult array adapter
        adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, itemsList );
        list.setAdapter(adapter);
		list.setOnItemClickListener( this );
		
		super.onCreate(savedInstanceState);
	}
	
	
	//broadcast receiver
	private BroadcastReceiver br = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Log.d("ClientActivity", "In the Broadcast Receiver");
			
			String action = intent.getAction();
			
			//when a device is found
			if(action.equals(BluetoothDevice.ACTION_FOUND))
			{
				//get the device name
				dev[dev_no] = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				
				Log.d("ClientActivity","device "+ dev[dev_no].getName()+ "detected.");
				
				//add the name to the list
				itemsList.add(dev_no, dev[dev_no].getName());
				adapter.notifyDataSetChanged();
				
				dev_no++; //to store next device
				
				//Unregister the BroadcastReceiver br, ONCE YOU FIND A DEVICE
				//unregisterReceiver(br);
				//Log.d("ClientActivity","Unregistered the Receiver");
				
			}
			
		}
	};

	@Override
	public void onClick(View v) {
		
		if(v.getId() == scan.getId())				//Scanning
		{
			//Scan for devices and display its name in the text box
			
			//register the broadcast receiver for ACTION_FOUND
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			registerReceiver(br,filter);
			
			Log.d("ClientActivity", "registered the Receiver");
						
			//start the discovery
			ba.startDiscovery();
			Log.d("ClientActivity", "Started the discovery");
			
			itemsList.clear();//remove any previously added device names
			adapter.notifyDataSetChanged();
			dev_no = 0; //index for the devices
			
			Toast.makeText(this, "Serching for devices", Toast.LENGTH_LONG ).show();
			
		}
		
		
	}
	
	@Override
	protected void onDestroy() {

		//Stop the discovery
		ba.cancelDiscovery();
		Log.d("ClientActivity", "Stopped the discovery");
		
		//Unregister the BroadcastReceiver br, ONCE YOU FIND A DEVICE
		unregisterReceiver(br);
		Log.d("ClientActivity","Unregistered the Receiver");

		super.onDestroy();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		//connect to the device (item) clicked
		ClientThread client = new ClientThread(dev[arg2]);	//create a thread
		client.start();		//connect to the server
		
		d = new MyDialog(this, dev[arg2]);
		d.show();
		
		//wait for successful connection....
		//wait for Message from ClientThread 

		//Log.d("ClientActivity", "##Unable to connect to " + dev[arg2].getName());
		
		/*
		if(ClientThread.ERROR_CODE == -1)//if there is an error in connecting
		{
			Log.d("ClientActivity", "##Unable to connect to " + dev[arg2].getName());
			
			Toast.makeText(this, "Device not supported", Toast.LENGTH_SHORT).show();
			
			client.cancel();
		
		}
		else
		{
			Log.d("ClientActivity", "connecting to " + dev[arg2].getName());
		
			Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();
		
			Intent i = new Intent(this, MessagesActivity.class);
			startActivity(i);
		}
*/
	}
	
	public void gotoMessagesActivity()
	{
		Log.d("ClientActivity", "connecting to the device");
		
		Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();
		
		d.dismiss();//stop showing the dialog
		
		Intent i = new Intent(this, MessagesActivity.class);
		startActivity(i);
	}

	private class MyDialog extends Dialog implements OnClickListener{
		
		public MyDialog(Context context, BluetoothDevice dev) {
			super(context);
			
			//to disply the name of the device
			cur_device = dev;
			
		}

		BluetoothDevice cur_device;
		TextView text;
		ProgressBar pro;
		private Button ok;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setTitle("Please wait...");
			
			setContentView(R.layout.dialog_waiting);
			
			text = (TextView)findViewById(R.id.waiting);
			text.setText("Waiting for the device "+ cur_device);
			
			pro = (ProgressBar)findViewById(R.id.progress);
			
			ok = (Button)findViewById(R.id.ok);
			ok.setOnClickListener(this);
				
		}
		
		@Override
		public void onClick(View v) {
			
			if(v.getId() == ok.getId())
				dismiss();
				
		}
		
	}
}
