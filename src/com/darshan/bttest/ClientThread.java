package com.darshan.bttest;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

public class ClientThread extends Thread{
	
	private static UUID MY_UUID;
	
	//used to check for error conditions
	public static int CONNECTION_FAILED = 1;
	public static int CONNECTION_SUCCESS = 2;
	
	private final BluetoothSocket socket;
	private final BluetoothDevice device;
	
	BluetoothAdapter ba;

	ConnectedThread receiver;
	
	//Constructor to obtain a socket to server device
	public ClientThread(BluetoothDevice dev)
	{
        //generate an uuid in MY_UUID
        try
        {
        	MY_UUID = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
        }
        catch(IllegalArgumentException e)
        {
        	Log.d("ClientThread", "UUID is not properly formatted!!!");
        }
        
        //get a BluetoothAdapter
        ba = BluetoothAdapter.getDefaultAdapter();

		// Use a temporary object that is later assigned to socket,
        // because socket is final
		BluetoothSocket tmp = null;
        device = dev;
        
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try
        {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        }
        catch (IOException e)
        {
        	Log.d("ClientThread", "Exc::Exception caught in obtaining a server socekt");
        }
        if(tmp == null)
        {
        	Log.d("ClientThread", "@@@if (tmp == null) ERROR");
        	
        	ClientActivity.me.handler.sendEmptyMessage(CONNECTION_FAILED);// report the ERROR to ClientActivity
        	socket = null;	 //no socket :(
        	return;
        }
        
        Log.d("ClientThread", "Obtained a server socekt");
        socket = tmp;
		
	}
	
	public void run()
	{
		// Cancel discovery because it will slow down the connection
	    ba.cancelDiscovery();
	
	    try
	    {
	        // Connect the device through the socket. This will block
	        // until it succeeds or throws an exception
	    	Log.d("ClientThread","Connecting to server socket...");
	        socket.connect();
	        
	    }
	    catch (IOException connectException)
	    {
	    	Log.d("ClientThread", "@2 ERROR");
	    	ClientActivity.me.handler.sendEmptyMessage(CONNECTION_FAILED);// report the ERROR to ClientActivity
		        
	        // Unable to connect; close the socket and get out
	    	Log.d("ClientThread", "Exc::Unable to Connect to the server");
	        try
	        {
	            socket.close();
	            Log.d("ClientThread","Closing the socket now");
	        }
	        catch (IOException closeException)
	        {
	        	Log.d("ClientThread", "@1 ERROR");
	        	ClientActivity.me.handler.sendEmptyMessage(CONNECTION_FAILED);// report the ERROR to ClientActivity
	        	ClientActivity.me.handler.sendEmptyMessage(CONNECTION_FAILED);// report the ERROR to ClientActivity
	        	return;
	        }
	       
	        return;
	    }
	    Log.d("ClientThread","SUCCESSFULLY connected to the server!!!");
	    
	    ClientActivity.me.handler.sendEmptyMessage(CONNECTION_SUCCESS);// report the ERROR to ClientActivity
	    //create a ConnectedThread for transfer
	    receiver = new ConnectedThread(socket);
	    
	    // Do work to manage the connection (in a separate thread)
	    receiveFromServer(socket);
	}
	
	private void receiveFromServer(BluetoothSocket socket2) {
		
		//receive the data from server
		

		//create a reader thread
		//ConnectedThread receiver = new ConnectedThread(socket, handler);
		receiver.start();
		
		Log.d("ClientThread","Receiving the data from server!!!");
		
		//receiver.cancel();				//donot close the socket only for the first time
		//cancel();  		
		
	}

	/** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try
        {
        	socket.close();
            
        }
        catch (IOException e) { }
        
    }
	
	
}
