package SessionProcess;

import java.io.*;
import java.net.Socket;

import android.os.Handler;
import android.os.StrictMode;

import 	java.net.InetSocketAddress;
import java.lang.Thread;

/**
 * Created by Franco.Liu on 2014/2/26.
 */


public class TelnetConnMgr  implements Runnable {
    protected final int readBufferSize = 32*1024;
    protected byte[] mBytesData = new byte[readBufferSize];
    protected String mStrHost = "192.168.1.100";
    protected int nPort = 23;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;
    protected boolean mIsConnected = false;
    protected OnConnListener mOnConnListener = null;
    Handler mHandlerException = new Handler();
    Socket mSocket = null;
    
    /* connection Listener
     * 
     */
    public interface OnConnListener {
    	void OnConnected();
        void OnDisconnected();
        void OnBufferReceived(byte[] bytesData, int len);
    }

    public TelnetConnMgr(String address, int port)
    {
        mStrHost=address;
        nPort=port;
        mOnConnListener = null;
        mSocket = null;
    }
    
    public boolean TelnetsStart()
    {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try
        {
        	mSocket = new Socket();
        	if(mSocket == null)
        		throw new Exception();
        	mSocket.connect(new InetSocketAddress(mStrHost, nPort), 5000);
            mInStream = mSocket.getInputStream();
            mOutStream= mSocket.getOutputStream();
            new Thread(this).start();
            mIsConnected = true;
            if(mOnConnListener != null)
            	mOnConnListener.OnConnected();
            return true;

        }
        catch (Exception e0)
        {
        	return false;
        }
    }
    
    public boolean IsConnect()
    {
    	return mIsConnected;
    }
    
    public void TelnetDisconnect()
    {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try
        {
        	if (mSocket == null)
        		throw new Exception();
        	mSocket.close();
        }
        catch (Exception e0)
        {
        }
    }
    
    public void setOnConnListener(OnConnListener connListener) {
    	mOnConnListener = connListener;
    }
    
    public void run()
    {
    	try {
			Thread.sleep(100);
	    	while (true)
	        {
	        	int nRead = mInStream.read(mBytesData, 0, mBytesData.length);
	            if (nRead == -1)
	            	throw new Exception("Error while reading socket.");
	          
	            if(mOnConnListener != null)
	            	mOnConnListener.OnBufferReceived(mBytesData, nRead);
	        }
    	}
    	catch (Exception e)
        {
    		e.printStackTrace();
        }
        finally {
        	mIsConnected = false;
        	mSocket = null;
        	mHandlerException.post(new Runnable() {
                public void run()
                {
                    if(mOnConnListener != null)
                    	mOnConnListener.OnDisconnected();
                }
            });
        }
    }

    public void Send(byte[] message)
    {
        try
        {
        	if (!mIsConnected || mOutStream == null)
                throw new Exception();
            mOutStream.write(message);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
