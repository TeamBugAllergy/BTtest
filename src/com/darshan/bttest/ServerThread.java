package com.darshan.bttest;

import java.io.IOException;
import java.util.UUID;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ServerThread extends Thread{

	//used when client is not yet ready
	String g_data;
	boolean first_time = true;
	boolean f = true;
	
	Thread t;
	
	//used by message activity to send the message
	public static ServerThread me;
	
	private static UUID MY_UUID;
	
	//used by MessageActivity to enable the send button,after a successful connection
	public static final int CLIENT_NOT_READY = 2;
	
	private final BluetoothServerSocket server_socket;
	
	BluetoothAdapter ba;
	
	//socket used for the connection
	BluetoothSocket socket; 
	
	ConnectedThread sender;
	public boolean killThread = false;		//will be made true inside the if(first_time == true) block, to stop the thread
	
	//Handler from MessageActivity
	public static Handler handler;
	
	
	public Handler my_hand = new Handler() {
		
		public void handleMessage(Message msg) {

			Log.d("handler of ST", "msg");
			
			//if(msg.what == 3)
				sendDataToClient("");
		}
    };
    
	public ServerThread()
	{
		//handler from UIthread
		//handler = h;
		
		g_data = "";
		
		//used by MessageActivity to send the message	
		me = this;

        //generate an uuid in MY_UUID
        try
        {
        	MY_UUID = UUID.fromString("a60f35f0-b93a-11de-8a39-08002009c666");
        }
        catch(IllegalArgumentException e)
        {
        	Log.d("ClientThread", "UUID is not properly formatted!!!");
        }
        
        //get a bluetooth adapter
        ba = BluetoothAdapter.getDefaultAdapter();
     

		// Use a temporary object that is later assigned to socket,
        // because socket is final
        BluetoothServerSocket tmp = null;
        
        //try to obtain a socket
        try													//obtain a server socket to listen
        {						
        	// MY_UUID is the app's UUID string, also used by the client code
            tmp = ba.listenUsingRfcommWithServiceRecord("BTtest", MY_UUID);

        }
        catch (IOException e)
        {
            Log.d("ServerThread", "Exc::Unable to obtain a socket");        	
        }
        
        Log.d("ServerThread", "Obtained a socket");        
        server_socket = tmp;
	}
	
	public void run()
	{
        socket = null;
        // Keep listening until exception occurs or a socket is returned
        while (true)
        {
            try															//obtain a connected socket from listing socket
            {					//get a connected socket from the server socket
                socket = server_socket.accept();								
            }		
            catch (IOException e)
            {
            	Log.d("ServerThread", "Exc::Unable to obtain socket from server_socket");
                break;
            }
            // If a connection was accepted
            if (socket != null)
            {
            	Log.d("ServerThread","SUCCESSFULLY accepted the connection from client!!!");
                // Do work to manage the connection (in a separate thread)
            	
            	//Notify the MessageActivity that connection has been created
            	//handler.sendEmptyMessage(READY);
            	
            	//create a ConnectedThread for transfer
            	 sender = new ConnectedThread(socket);
            	 
            	 //tonotify the MainActivity to create another server thread for further(multiple) requests
            	 //MainActivity.MainHandler.sendEmptyMessage(MainActivity.START_SERVER_THREAD);
            	 Log.d("ServerThread","Starting the SERVER AGAIN!!!");
            	 
                try
                {
					server_socket.close();
					Log.d("ServerThread", "server_socket has been closed");
					
					break;
				}
                catch (IOException e)
                {
                	
				}
                
            }
        }
        
    }

	public void sendDataToClient(String d) {

		//create a writer thread
		//ConnectedThread sender = new ConnectedThread(socket, null);

		//byte[] data = d.getBytes();
		byte[] data;
		
		if(sender == null)
		{
			//store the messages in g_data untill client is ready
			g_data = g_data +"\n"+ d;
			
			if(f == true)
			{
			Log.d("New Thread of ST", "msg");	
			t = new Thread(new Runnable() {
				
				@Override					//to stop the thread
				public void run() {

					while (sender == null && killThread == false)
						{
							Log.d("Inside the Thread","in while loop");

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {

								e.printStackTrace();
							}
						}
					
					my_hand.sendEmptyMessage(3);
				}
			
			});
			t.start();
			
			f = false;

			}
			handler.sendEmptyMessage(CLIENT_NOT_READY);
			
		}
		else
		{
			if(first_time == true)
			{
				
				d = g_data + "\n" + d;
				first_time = false;
				//t.stop();	//deprecated
				stopBackgroundThread();		//make killThread = true to stop it
				
			}
			
			data = d.getBytes();
			sender.write(data);
			
		}
				
		Log.d("ServerThread","Sending the data to Client!!!");
			
		//sender.cancel();		//donot finish the current thread						
		//cancel();     		
				
	}

	public void stopBackgroundThread()
	{
		//stop the thread waiting to deliver the pending messages
		//t.stop();		//deprecated
		killThread = true;
		
	}
	
	/** Will cancel the listening socket, and cause the thread to finish */
    public void cancel()
    {
        try
        {
            server_socket.close();
            Log.d("ServerThread", "server_socket has been closed");
            
            //below statement should be called by ClientThread
            //t.stop();
        }
        catch (IOException e)
        {
        	
        }
    }
}
