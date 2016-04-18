package SessionProcess;

import Terminals.CipherConnectSettingInfo;
import Terminals.Sshconnectionpack;
import com.chilkatsoft.CkByteData;

public class TelnetSshConnMgr extends TelnetConnMgr {
       
	 Sshconnectionpack SshConn=new Sshconnectionpack();
	 
	 
	 public TelnetSshConnMgr(String address, int port)
	 {
		    super(address,port);
	    
	 }
	public boolean TelnetsStart()
	{
		SshConn.SshSetConnectTimeoutMs(5000);
		SshConn.SshSetIdleTimeoutMs(15000);
		SshConn.SshSetReadTimeoutMs(5000);
	 
		
		boolean IsConn=SshConn.SshConnectToHost(mStrHost, nPort);
		
		if (!IsConn)
			return false;
		
		String User=CipherConnectSettingInfo.getHostSshUserByIndex(CipherConnectSettingInfo.GetSessionIndex());
		String Password=CipherConnectSettingInfo.getHostSshPasswordByIndex(CipherConnectSettingInfo.GetSessionIndex());
		IsConn=SshConn.SshAuthenticatePw(User,Password);
		
		
		SshConn.SshOpenChanel();
		SshConn.SshSendReqPty();
		SshConn.SshSendReqShell();
		
        if(mOnConnListener != null)
        	mOnConnListener.OnConnected();
	     mIsConnected = true;
	    return true;
	}

	public void TelnetDisconnect()
	{
		SshConn.SshDisConnect();
		 
		mIsConnected = false;
       
        if(mOnConnListener != null)
        	mOnConnListener.OnDisconnected();
	}
    
	public void TelnetTerminated()
	{
         Runnable rExceptionThread = new Runnable()
         {
             public void run()
             {
            	SshConn.SshDisConnect();
        		 
         		mIsConnected = false;
                if(mOnConnListener != null)
                	mOnConnListener.OnDisconnected();
             }
         };
         
         mHandlerException.post(rExceptionThread);
	}
	 

	public void run()
	{
		
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
        while (mIsConnected && (!Thread.currentThread().isInterrupted()))
        {
            CkByteData Recv=new CkByteData();
            
            if (!SshConn.SshIsConnected())
            {
            	TelnetTerminated();
               	break;
            }
            
            try
            {
                int i = mBytesData.length;
                
                
        		SshConn.SshSetIdleTimeoutMs(10);
        	 
                //j =SshConn.SshChannelReadAndPoll();
                
                if (SshConn.SshChannelReceivedExitStatus())
        		{
                	TelnetTerminated();
                	break;
                    
        		}
              
                SshConn.SshGetReceivedData(Recv);
                
                mBytesData=Recv.toByteArray();
                if(mOnConnListener != null)
                	mOnConnListener.OnBufferReceived(mBytesData,mBytesData.length);
            }
            catch (Exception e0)
            {
               break;
            }
        }
	}

	public void Send(byte[] message)
	{
	 
		 CkByteData SendData=new CkByteData();
		 SendData.clear();
		 SendData.appendByteArray(message);
		 
		 if (!mIsConnected)
	            return;
	        try
	        {
	            SshConn.SshSendData(SendData);
	        }
	        catch (Exception e0)
	        {
	            //Handler handlerException = this.mHandler;
	            String strException = e0.getMessage();
	            final String strMessage = "Error while Writing to server:\r\n" + strException;

	            //handlerException.post(rExceptionThread);

	         
	        }

	}
	 
	
}
