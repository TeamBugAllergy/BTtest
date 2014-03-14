package com.darshan.bttest;

import java.util.ArrayList;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DevicesActivity extends Activity implements OnItemClickListener{
	
	ListView list;
	ArrayList<String> itemsList;
	ArrayAdapter<String> adapter;

	MyDialog d;
	View v;
	
	int i=1;
	
	Handler hand = new Handler(){
		public void handleMessage(Message msg) {
			if(msg.what == 1)
			{
				itemsList.add(i, "Item "+ i++);
				adapter.notifyDataSetChanged();
				
				d.dismiss();
			}
			else if(msg.what == 2)
			{
				d.setTitle("ERROR");
				d.text.setText("Please make sure the device 'x' supports");
				d.pro.setVisibility(View.GONE);
				d.ok.setVisibility(View.VISIBLE);
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_devices);
		
		list = (ListView)findViewById(R.id.my_devices);
		
		itemsList = new ArrayList<String> (  )  ;
		
        adapter = new ArrayAdapter<String> ( this, android . R . layout . simple_list_item_1, itemsList )  ;
        list.setAdapter(adapter);
        
        itemsList . add ("Item 0" )  ;

        adapter.notifyDataSetChanged();
        
		list.setOnItemClickListener( this );
		 
		list.setVisibility(View.VISIBLE);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		d = new MyDialog(this);
		d.show();
		
		Thread t = new Thread(){
			public void run() {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(i%2 != 0)
					hand.sendEmptyMessage(1);
				else
					hand.sendEmptyMessage(2);
			}
		};
		t.start();
		
		//itemsList.add(i, "Item "+ i++);
		//adapter.notifyDataSetChanged();
		
	}	

	class MyDialog extends Dialog implements OnClickListener{
		
		public MyDialog(Context context) {
			super(context);
			
		}

		TextView text;
		ProgressBar pro;
		private Button ok;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			setTitle("Please wait...");
			setContentView(R.layout.dialog_waiting);
			
			text = (TextView)findViewById(R.id.waiting);
			text.setText("Waiting for the device "+"adiga");
			
			pro = (ProgressBar)findViewById(R.id.progress);
			
			ok = (Button)findViewById(R.id.ok);
			ok.setOnClickListener(this);
		
			
		}
		
		public void setText(String t)
		{
			text.setText(t);
		}

		@Override
		public void onClick(View v) {
			
			if(v.getId() == ok.getId())
				dismiss();
		}
		
	}

}
