package com.darshan.bttest;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MessagesActivity extends Activity implements OnClickListener{

	ListView list;
	ArrayList<String> itemsList;
	ArrayAdapter<String> adapter;
	int mes_no=0;
	
	Button send;
	EditText mes;
	
	ServerThread server;
	// Define the Handler that receives messages from the thread and update the progress
		private Handler handler = new Handler() {
				
			public void handleMessage(Message msg) {
	        	Log.d("MESSAGEACTIVITY", "SUCCESS!!!INSIDE MessageActivity");
		        
	        	//only if the message is from ConnectedThread
	        	if(msg.what == ConnectedThread.MESSAGE)
	        	{
	        		//user-defined method
	        		addToListView(msg);
	        	}
	        	else if(msg.what == ServerThread.CLIENT_NOT_READY)
	        	{
	        		displayToast();
	        	}
	        	//instead of updating the list here,call addToListView(msg)
	        	/*
	        	//only if the message is from ConnectedThread
	        	if(msg.what == ConnectedThread.MESSAGE)
	        	{
	        		Log.d("MESSAGEACTIVITY", "DATA: read " + msg.obj.toString());
		        	
	        		itemsList.add(mes_no, msg.obj.toString());
	        		adapter.notifyDataSetChanged();
	        		mes_no++;
	        	}
	        	*/
	        	
	        }
	    };

	//one version called by above handler
	public void  addToListView(Message msg)
	{
		
    		Log.d("MESSAGEACTIVITY", "DATA: read " + msg.obj.toString());
        	
    		itemsList.add(mes_no, msg.obj.toString());
    		adapter.notifyDataSetChanged();
    		mes_no++;
    		
	}
	//another version called by onClick of send button
	public void  addToListView(String mes1)
	{
		//only if the message is from ConnectedThread
    		Log.d("MESSAGEACTIVITY", "DATA: read " + mes1);
        	
    		itemsList.add(mes_no, mes1);
    		adapter.notifyDataSetChanged();
    		mes_no++;
	}
	
	public void displayToast()
	{
		Toast.makeText(this, "Device is not ready,message will be sent later", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messages);
		
		//set the handler of ConnectedThread object
		ConnectedThread.mHandler = handler;
		
		//set the handler of ServerThread object
		ServerThread.handler = this.handler;
		
		list = (ListView)findViewById(R.id.message_list);
		itemsList = new ArrayList<String>();
		//use the deafult array adapter
        adapter = new ArrayAdapter<String>( this, android.R.layout.simple_list_item_1, itemsList );
        list.setAdapter(adapter);
		//list.setOnItemClickListener( this );
		
        send = (Button)findViewById(R.id.send_msg);
        send.setOnClickListener(this);
        //send button is enabled ONLY AFTER connection is SUCCESSFULLY established.(inside the handler)
        //send.setEnabled(false);
        
        mes = (EditText)findViewById(R.id.message);
        
		//used by message activity to send the message to ServerThread
		server = ServerThread.me;
		
		
	}
	
	@Override
	public void onClick(View v) {

		if(v.getId() == send.getId())
		{
			if(mes.getText().toString().equals("".toString()) != true )		//only if mes contains some text
			{
				server.sendDataToClient( mes.getText().toString() );
			
				//update the listview
				addToListView("Me: " + mes.getText().toString());
			
				mes.setText("".toString());		//clear the text message
			
				Log.d("MessageActivity:DATA SENT", "!!!:"+mes.getText().toString());
			}
		}
	}
		

}
