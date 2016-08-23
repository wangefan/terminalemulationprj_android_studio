package Terminals;

import android.app.Service;
import android.os.Vibrator;

import com.te.UI.MainActivity;

public class stdActivityRef {

    public static MainActivity activity = null;
    public static boolean gIsActivate = false;

    public static void setCurrActivity(MainActivity act) {
        activity = act;
        // Now here you can get getApplication()
    }

    public static MainActivity getCurrActivity() {
        return activity;
        // Now here you can get getApplication()
    }

    public static void ApplicationVibration(long milliseconds) {
        Vibrator myVibrator = (Vibrator) getCurrActivity().getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(milliseconds);
    }

}
