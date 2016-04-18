package Terminals;

import com.cipherlab.barcode.ReaderManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public class CipherReaderControl {
	
	private static com.cipherlab.barcode.ReaderManager mReaderManager;
	private static IntentFilter filter;
	
	public static void InitReader(Context context,BroadcastReceiver Receiver)
	{
		mReaderManager = ReaderManager.InitInstance(context);
		
		filter = new IntentFilter(); 
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_SOFTTRIGGER_DATA);
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_PASS_TO_APP);
		filter.addAction(com.cipherlab.barcode.GeneralString.Intent_READERSERVICE_CONNECTED);
		 
		//mReaderManager.SetActive(false);
		context.registerReceiver(Receiver, filter);
	}
	 
	public static void ReleaseReader()
	{
		if (mReaderManager != null)
		{
			mReaderManager.Release();
		}
	}
	public static void SetActived(Boolean Enable)
	{
		if (mReaderManager != null)
		{
			mReaderManager.SetActive(Enable);
		}
	}
	
	
}
