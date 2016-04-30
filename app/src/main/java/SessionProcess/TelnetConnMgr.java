package SessionProcess;

import java.io.*;
import java.net.Socket;

import android.os.Handler;
import android.os.StrictMode;

import 	java.net.InetSocketAddress;
import java.lang.Thread;

import Terminals.TerminalBase;

/**
 * Created by Franco.Liu on 2014/2/26.
 */


public class TelnetConnMgr  implements Runnable {
    protected String mStrHost = "192.168.1.100";
    protected int mPort = 23;
    protected boolean mIsConnected = false;
    protected TerminalBase mTerminal = null;
    protected Handler mUIHandler = null;
    private Socket mSocket = null;
    private InputStream mInStream = null;
    private OutputStream mOutStream = null;

    public TelnetConnMgr(String address, int port, TerminalBase terminal) {
        mStrHost = address;
        mPort = port;
        mTerminal = terminal;
        mSocket = new Socket();
        mUIHandler = new Handler();
    }

    public boolean isConnected()
    {
    	return mIsConnected;
    }

    public boolean TelnetsStart() {
        new Thread(this).start();
        return true;
    }
    
    public void TelnetDisconnect() {
        if (mSocket != null)
            try {
                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }
    
    public void run() {
    	try {
            if(mSocket == null)
                throw new Exception();
            mSocket.connect(new InetSocketAddress(mStrHost, mPort), 5000);
            mInStream = mSocket.getInputStream();
            mOutStream = mSocket.getOutputStream();
            mIsConnected = true;
            mUIHandler.post(new Runnable() {
                @Override
                public void run() {
                    mTerminal.OnConnected();
                }
            });

			Thread.sleep(100);
	    	while (true) {
                final int readBufferSize = 1024;
                final byte[] bytesData = new byte[readBufferSize];
	        	final int nRead = mInStream.read(bytesData, 0, bytesData.length);
	            if (nRead == -1)
	            	throw new Exception("Error while reading socket.");
                mUIHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTerminal.handleBufferReceived(bytesData, 0, nRead);
                    }
                });
	        }
    	} catch (InterruptedException e) {
            mUIHandler.post(new Runnable() {
                public void run()
                {
                    mTerminal.OnDisconnected();
                }
            });
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            mUIHandler.post(new Runnable() {
                public void run()
                {
                    mTerminal.OnDisconnected();
                }
            });
        } catch (Exception e) {
            mUIHandler.post(new Runnable() {
                public void run()
                {
                    mTerminal.OnDisconnected();
                }
            });
            e.printStackTrace();
        } finally {
            mIsConnected = false;
            mSocket = null;
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
