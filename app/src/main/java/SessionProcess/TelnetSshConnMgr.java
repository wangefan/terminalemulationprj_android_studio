package SessionProcess;

import com.chilkatsoft.CkByteData;
import com.chilkatsoft.CkSsh;
import com.cipherlab.terminalemulation.R;
import com.te.UI.CipherUtility;

import Terminals.TESettingsInfo;
import Terminals.TerminalBase;
import Terminals.stdActivityRef;

public class TelnetSshConnMgr extends TelnetConnMgr {
	private final int CONN_TIME_OUT = 8000;
	private final String SSH_PROXY_TYPE_HTTP = "HTTP";
	private final String SSH_PROXY_TYPE_SOCKET4 = "SOCKS4";
	private final String SSH_PROXY_TYPE_SOCKET5 = "SOCKS5";
	private int mSshChanel;
	private CkSsh mSshConn = null;

	public TelnetSshConnMgr(String address, int port, TerminalBase terminal) {
		super(address,port, terminal);
		mSshConn = new CkSsh();
	}

	private boolean setupHostProxySock() {
		if(mSshConn == null)
			return false;
		mSshConn.put_SocksHostname(TESettingsInfo.getSshProxyHostByIndex(TESettingsInfo.getSessionIndex()));
		mSshConn.put_SocksPassword(TESettingsInfo.getSshProxyPasswordByIndex(TESettingsInfo.getSessionIndex()));
		mSshConn.put_SocksPort(Integer.valueOf(TESettingsInfo.getSshProxyPortByIndex(TESettingsInfo.getSessionIndex())));
		mSshConn.put_SocksUsername(TESettingsInfo.getSshProxyUserByIndex(TESettingsInfo.getSessionIndex()));
		return true;
	}

	private boolean setupHostProxyHttp() {
		if(mSshConn == null)
			return false;
		mSshConn.put_HttpProxyHostname(TESettingsInfo.getSshProxyHostByIndex(TESettingsInfo.getSessionIndex()));
		mSshConn.put_HttpProxyPassword(TESettingsInfo.getSshProxyPasswordByIndex(TESettingsInfo.getSessionIndex()));
		mSshConn.put_HttpProxyPort(Integer.valueOf(TESettingsInfo.getSshProxyPortByIndex(TESettingsInfo.getSessionIndex())));
		mSshConn.put_HttpProxyUsername(TESettingsInfo.getSshProxyUserByIndex(TESettingsInfo.getSessionIndex()));
		return true;
	}

	private void doDisconnect() {
		mIsConnected = false;
		mSshConn = null;
		mUIHandler.post(new Runnable() {
			public void run() {
				mTerminal.OnDisconnected();
			}
		});
	}

	private void doConnectError(final String message) {
		mIsConnected = false;
		mSshConn = null;
		mUIHandler.post(new Runnable() {
			public void run() {
				mTerminal.OnConnectError(message);
			}
		});
	}

	@Override
	public void TelnetDisconnect() {
		mSshConn.Disconnect();
	}

	@Override
	public void run() {
		CipherUtility.Log_d("TelnetSshConnMgr", "prepare to connect to SSH");
		try {
			mSshConn.UnlockComponent("tnwverSSH_bJkOyWEOMDnU");
			CipherUtility.Log_d("TelnetSshConnMgr", "after mSshConn.UnlockComponent");
			mSshConn.put_TcpNoDelay(TESettingsInfo.getSshTcpNoDelayByIndex(TESettingsInfo.getSessionIndex()));

			//Set up http proxy
			String sshProxyType = TESettingsInfo.getSshProxyTypeByIndex(TESettingsInfo.getSessionIndex());
			if(sshProxyType.compareTo(SSH_PROXY_TYPE_HTTP) == 0) {
				setupHostProxyHttp();
			} else if(sshProxyType.compareTo(SSH_PROXY_TYPE_SOCKET4) == 0) {
				mSshConn.put_SocksVersion(4);
				setupHostProxySock();
			} else if(sshProxyType.compareTo(SSH_PROXY_TYPE_SOCKET5) == 0) {
				mSshConn.put_SocksVersion(5);
				setupHostProxySock();
			} else {
				mSshConn.put_HttpProxyHostname("");
				mSshConn.put_HttpProxyPassword("");
				mSshConn.put_HttpProxyPort(0);
				mSshConn.put_HttpProxyUsername("");

				mSshConn.put_SocksVersion(0);
				mSshConn.put_SocksHostname("");
				mSshConn.put_SocksPassword("");
				mSshConn.put_SocksPort(0);
				mSshConn.put_SocksUsername("");
			}
			//Set up http proxy end

			//mSshConn.put_ConnectTimeoutMs(CONN_TIME_OUT); not work
			mSshConn.put_IdleTimeoutMs(CONN_TIME_OUT);//set connect timeout,
			if (!mSshConn.Connect(mStrHost, mPort)) {
				String msg = mSshConn.lastErrorText();
				throw new SSHConnException(SSHConnException.EXCEP_CONN_FAIL, msg);
			}

			String sshLoginName = TESettingsInfo.getHostSshUserByIndex(TESettingsInfo.getSessionIndex());
			//Choose authentication type, false:File  true:Name/Pwd
			boolean authType = TESettingsInfo.getAuthTypeByIndex(TESettingsInfo.getSessionIndex());
			if(authType) {// Name/Pwd
				String sshPassword = TESettingsInfo.getHostSshPasswordByIndex(TESettingsInfo.getSessionIndex());
				if (!mSshConn.AuthenticatePw(sshLoginName, sshPassword)) {
					String msg = mSshConn.lastErrorText();
					throw new SSHConnException(SSHConnException.EXCEP_AUTH_NAME_PWD_FAIL, msg);
				}
			} else {// File
				String sshKeyPath = TESettingsInfo.getHostSshKeyPathByIndex(TESettingsInfo.getSessionIndex());
				//Todo:finish
			}

			mSshChanel = mSshConn.OpenSessionChannel();
			if (mSshChanel < 0) {
				String msg = mSshConn.lastErrorText();
				throw new SSHConnException(SSHConnException.EXCEP_OPEN_CHANN_FAIL, msg);
			}

			//Pseudo terminal type
			boolean bNoPseudoTer = TESettingsInfo.getSshNoPseudoTerByIndex(TESettingsInfo.getSessionIndex());
			if(bNoPseudoTer == false) {
				final String termType = "vt100";
				final int nWidthInChars = 80;
				final int nHheightInChars = 24;
				final int pixWidth = 0;
				final int pixHeight = 0;
				if(!mSshConn.SendReqPty(mSshChanel, termType, nWidthInChars, nHheightInChars, pixWidth, pixHeight)) {
					String msg = mSshConn.lastErrorText();
					throw new SSHConnException(SSHConnException.EXCEP_PSEDO_TER_FAIL, msg);
				}
			}

			//Host shell
			boolean bNoHostShell = TESettingsInfo.getSshNoHostShellByIndex(TESettingsInfo.getSessionIndex());
			if(bNoHostShell == false) {
				if(!mSshConn.SendReqShell(mSshChanel)) {
					String msg = mSshConn.lastErrorText();
					throw new SSHConnException(SSHConnException.EXCEP_HOST_SHELL_FAIL, msg);
				}
			}

			//Todo:SetupHostEnvVal
			//Todo:SetupHostEnvTTY
			//Todo:SendReqExec
			mIsConnected = true;
			mUIHandler.post(new Runnable() {
				@Override
				public void run() {
					mTerminal.OnConnected();
				}
			});
			Thread.sleep(100);

			while (true) {
				if (!mSshConn.get_IsConnected()) {
					throw new SSHConnException(SSHConnException.EXCEP_DISCONNECTED, "");
				}
				if (mSshConn.ChannelReceivedExitStatus(mSshChanel)) {
					throw new SSHConnException(SSHConnException.EXCEP_DISCONNECTED, "");
				}
				mSshConn.put_IdleTimeoutMs(150);//set ChannelRead timeout to infinite
				int nNumDataToBeGot = mSshConn.ChannelReadAndPoll(mSshChanel, 150);
				CipherUtility.Log_d("TelnetSshConnMgr", String.format("Avable number of data = %d", nNumDataToBeGot));
				if(nNumDataToBeGot > 0) {
					CkByteData recvData = new CkByteData();
					mSshConn.GetReceivedData(mSshChanel, recvData);
					final byte[] bytesData = recvData.toByteArray();
					mTerminal.handleBufferReceived(bytesData, 0, bytesData.length);
				}
			}
		} catch (InterruptedException e) {
			CipherUtility.Log_d("TelnetSshConnMgr", String.format("InterruptedException, msg = %s", e.getMessage()));
			doDisconnect();
			e.printStackTrace();
		} catch (SSHConnException e) {
			String message = "";
			switch (e.getExcepType()) {
				case SSHConnException.EXCEP_CONN_FAIL:
				case SSHConnException.EXCEP_AUTH_NAME_PWD_FAIL:
				case SSHConnException.EXCEP_OPEN_CHANN_FAIL:
				case SSHConnException.EXCEP_PSEDO_TER_FAIL:
				case SSHConnException.EXCEP_HOST_SHELL_FAIL:
					String sshErrorFormat = stdActivityRef.getCurrActivity().getString(R.string.MSG_SSH_connect_error);
					message = String.format(sshErrorFormat, e.getErrotTextFromAPI());
					doConnectError(message);
					break;
				case SSHConnException.EXCEP_DISCONNECTED:
					doDisconnect();
					break;
				default:
					break;
			}
			e.printStackTrace();
		}
	}

	public void Send(byte[] message) {
		 CkByteData SendData=new CkByteData();
		 SendData.clear();
		 SendData.appendByteArray(message);

		 if (!mIsConnected)
	            return;
	        try {
	            mSshConn.ChannelSendData(mSshChanel, SendData);
	        }
	        catch (Exception e0) {
	            //Handler handlerException = this.mHandler;
	            String strException = e0.getMessage();
	            final String strMessage = "Error while Writing to server:\r\n" + strException;
	            //handlerException.post(rExceptionThread);
	        }
	}
	private class SSHConnException extends Exception
	{
		private static final long serialVersionUID = 5548848902595353181L;
		public static final int EXCEP_CONN_FAIL = 0;
		public static final int EXCEP_AUTH_NAME_PWD_FAIL = 1;
		public static final int EXCEP_OPEN_CHANN_FAIL = 2;
		public static final int EXCEP_PSEDO_TER_FAIL = 3;
		public static final int EXCEP_HOST_SHELL_FAIL = 4;
		public static final int EXCEP_DISCONNECTED = 5;

		private int mExcepType = -1;
		private String mErrTextFromAPI = "";

		public SSHConnException(int nExcepType, String errorTextFromAPI) {
			super();
			mExcepType = nExcepType;
			mErrTextFromAPI = errorTextFromAPI;
		}

		public int getExcepType() {
			return mExcepType;
		}

		public String getErrotTextFromAPI() {
			return mErrTextFromAPI;
		}
	}
}
