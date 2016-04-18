package Terminals;

import android.view.KeyEvent;

public class Macroitem {
	 
	String BarcodeData="";
	KeyEvent event;
	int KeyCode;
	int GetInputType;// 0:key 1:bar
	public Macroitem()
	{
	           
	          
	}
	
	public int GetInputType()
	{
		return GetInputType;
	}
	public void SetInputType(int type)
	{
		GetInputType=type;
	}
	
	public String GetBarcodeData()
	{
		return BarcodeData;
	}
	public void SetBarcodeData(String Data)
	{
		BarcodeData=Data;
	}
	
	
	public KeyEvent GetEvent()
	{
		return event;
	}
	public void SetEvent(KeyEvent evt)
	{
		event=evt;
	}
	
	
	public int GetKeyCode()
	{
		return KeyCode;
	}
	public void SetKeyCode(int key)
	{
		KeyCode=key;
	}
	
}
