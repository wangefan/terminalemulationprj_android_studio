package Terminals;
import com.chilkatsoft.*;
public class Sshconnectionpack {

	 int SshChanel;
	 CkSsh SshConn = null;
	 CkSshKey key;
	 
	 public Sshconnectionpack(){
		 //System.loadLibrary("chilkat"); 
		 System.loadLibrary("chilkat");
		 SshConn = new CkSsh();
		 SshConn.UnlockComponent("tnwverSSH_bJkOyWEOMDnU");
         		 
	 }
	 
	 public boolean SshConnectToHost(String Host,int port){
		  return SshConn.Connect(Host, port);
	 }
	 public void SshDisConnect(){
		    SshConn.Disconnect();
	 }
	 public boolean SshAuthenticatePw(String User,String password){
		  return SshConn.AuthenticatePw(User, password);
	 }
	 
	 public int SshOpenChanel(){
		 
		 SshChanel=SshConn.OpenSessionChannel();
		  return SshChanel;
		  
	 }
	  public boolean SshSendReqPty(){
		 
		 String xTermEnvVar="vt00";
	 		  return SshConn.SendReqPty(SshChanel, xTermEnvVar, 80, 24, 0, 0);
		  
	 }
	  
	 public boolean SshSendReqShell(){
	     return SshConn.SendReqShell(SshChanel);
			  
     }
	  
	 public boolean SshIsConnected(){
	     return SshConn.get_IsConnected();
			  
     } 
	 public boolean SshChannelReceivedExitStatus(){
	     return SshConn.ChannelReceivedExitStatus(SshChanel);
			  
     }
	 public int SshChannelReadAndPoll(){
	     return SshConn.ChannelReadAndPoll(SshChanel, 150);
			  
     }
	 public boolean SshGetReceivedData(CkByteData Data){
		 return SshConn.GetReceivedData(SshChanel, Data);
     }
	 public boolean SshSendData(CkByteData Data){
		 return SshConn.ChannelSendData(SshChanel, Data);
     }
	 
	 public void SshSetConnectTimeoutMs(int Ms){
		 SshConn.put_ConnectTimeoutMs(Ms);
		 
     }
	 public void SshSetIdleTimeoutMs(int Ms){
		 SshConn.put_IdleTimeoutMs(Ms);
		 
     }
	 public void SshSetReadTimeoutMs(int Ms){
		 SshConn.put_ReadTimeoutMs(Ms);
		 
     }
}
