package Terminals;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import 	android.content.Context;
import android.os.Environment;

public class TerminalLogWriter {
      
	FileOutputStream outputStream;
	Context MainContext;
	public TerminalLogWriter(String Filename)
	{
		try {
			  
			  File file;
			  MainContext=stdActivityRef.GetCurrActivity().getApplicationContext();
			  
			  File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"TE");
			  directory.mkdirs();
			  
			  //File path = Environment.getExternalStoragePublicDirectory(
			            //Environment.DIRECTORY_PICTURES);
			  //file = new File(path, "DemoPicture.jpg");

			   file = new File(directory,Filename);
			  
			 
                  if (!file.exists()) {
                      file.createNewFile();
                  }
                  outputStream = new FileOutputStream(file,true);
               
			  
			  //outputStream = new FileOutputStream(Filename, Context.MODE_PRIVATE);
		     
			 // outputStream.w
			 
			  
			} catch (Exception e) {
			  e.printStackTrace();
			}
	}
	public void Write(String Title,byte[] data,int len,boolean isRecv)
	{
		String LogTitle;
		String Content;
		if (isRecv)
		   LogTitle= Title+" Recv:";
		else
		   LogTitle= Title+" Send:";
		
		String hexStr="";
		for (int i=0;i<len;i++)
		{
			String hex = "["+Integer.toHexString(data[i]& 0xFF )+"]";
			hexStr+=hex;
			
		}
		
		Content=LogTitle+hexStr;
		//content.getBytes()
		try {
			outputStream.write(Content.getBytes());
			outputStream.write(0x0d);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }

	 
	
	
	
}
