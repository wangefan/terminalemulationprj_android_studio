package Terminals;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Vibrator;

public class stdActivityRef {
	    
	   public static Activity activity=null;
	   public static void SetCurrActivity (Activity act)
	    {
	         activity = act;
	         // Now here you can get getApplication()
	    }
	   public static Activity GetCurrActivity ()
	    {
	         return activity;
	         // Now here you can get getApplication()
	    }
	   
	    public static boolean openApp(Context context, String packageName) {
	         PackageManager manager = context.getPackageManager();
	         Intent i = manager.getLaunchIntentForPackage(packageName);
	         if (i == null) {
	             return false;      
	         }
	         i.addCategory(Intent.CATEGORY_LAUNCHER);
	         context.startActivity(i);
	         return true;
	     }
	    public static void ApplicationVibration() {
	        
	    	Vibrator myVibrator = (Vibrator) GetCurrActivity ().getApplication().getSystemService(Service.VIBRATOR_SERVICE);
	    	myVibrator.vibrate(3000);
	     }
	    
}
