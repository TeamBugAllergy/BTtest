package com.darshan.bttest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

public class ConnectedThread extends Thread{
	private final BluetoothSocket socket;
    private final InputStream in_stream;
    private final OutputStream out_stream;
    
    public static int MESSAGE = 1;
 
    public static Handler mHandler;
    
    public ConnectedThread(BluetoothSocket soc) {
    	
    	//Get the handler of UI thread...
    	
    	socket = soc;	//get the socket to be communicated with
        InputStream tmpIn = null;		//temporary IO streams
        OutputStream tmpOut = null;
 
        // Get the input and output streams, using temp objects because
        // member streams are final
        try
        {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        }
        catch (IOException e)
        {
        	
        }
 
        in_stream = tmpIn;			//streams for IO
        out_stream = tmpOut;
        
    }
 
    public void run()
    {
        byte[] buffer = new byte[1024];  // buffer store for the stream
        int bytes; // bytes returned from read()
 
        String data="";
        
        // Keep listening to the input_stream until an exception occurs
        while (true)
        {
            try
            {
                // Read from the InputStream
                bytes = in_stream.read(buffer);
                
                //data = new String(buffer);
                data = new String(buffer,0,bytes);
                
                //append the name
                data = socket.getRemoteDevice().getName() +" says: "+ data;
                
                // Send the obtained bytes to the UI activity
                mHandler.obtainMessage(MESSAGE, bytes, -1, data).sendToTarget();

                Log.d("ConnectedThread:Data READ", ""+data);
                
                data = "";
            }
            catch (IOException e)
            {
                break;
            }
        }
    }
 
    /* Call this from the main activity to send data to the remote device */
    public void write(byte[] bytes)
    {
    	String data = null;
        try
        {
            out_stream.write(bytes);

            data = new String(bytes);
            Log.d("ConnectedThread:Data WRITTEN", ""+data);
            
            data = "";
            
        }
        catch (IOException e)
        {
        	
        }
    }
 
    /* Call this from the main activity to shutdown the connection */
    public void cancel()
    {
        try
        {
            socket.close();
            
        }
        catch (IOException e)
        {
        	
        }
    }
}
