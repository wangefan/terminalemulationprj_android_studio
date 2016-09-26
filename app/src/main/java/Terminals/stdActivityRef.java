package Terminals;

import android.app.Service;
import android.os.Build;
import android.os.Vibrator;

import com.te.UI.MainActivity;

public class stdActivityRef {

    public static MainActivity activity = null;
    public static boolean gIsActivate = false;//Todo: handle saveInstace and restore
    public static boolean gIs53Keys = false;//Todo: set the flag by current device

    public static void setCurrActivity(MainActivity act) {
        activity = act;
        //Todo:get device key type
        String buildNumber = Build.FINGERPRINT;
        if(buildNumber.compareTo("alps/full_magc6755_66t_m/magc6755_66t_m:6.0/MRA58K/1472198318:user/test-keys") == 0) {
            gIs53Keys = true;
        } else {
            gIs53Keys = false;
        }
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
