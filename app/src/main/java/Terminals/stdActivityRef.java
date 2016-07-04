package Terminals;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;

import com.te.UI.MainActivity;

public class stdActivityRef {

    public static MainActivity activity = null;

    public static void setCurrActivity(MainActivity act) {
        activity = act;
        // Now here you can get getApplication()
    }

    public static MainActivity getCurrActivity() {
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

        Vibrator myVibrator = (Vibrator) getCurrActivity().getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(3000);
    }

}
