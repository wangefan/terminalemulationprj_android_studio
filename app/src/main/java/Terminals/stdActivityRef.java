package Terminals;

import android.app.Service;
import android.os.Vibrator;

import com.te.UI.MainActivity;
import com.te.UI.SystemProperties;

public class stdActivityRef {
    final static int KEYPAD_TYPE_TN_3270_53KEY = 7;
    final static int KEYPAD_TYPE_TN_5250_53KEY = 6;
    final static int KEYPAD_TYPE_VT_53KEY = 5;
    final static int KEYPAD_TYPE_38KEY = 4;
    final static int KEYPAD_TYPE_30KEY = 3;
    final static int KEYPAD_TYPE_NOKEY = -1;
    public static MainActivity activity = null;
    public static boolean gIsActivate = false;
    public static int gCurrentEditSessionIndex = 0; // -1: means Add setting, or possible index from 0 ~ 4
    public static int gKeypadType = KEYPAD_TYPE_NOKEY;

    public static void setCurrActivity(MainActivity act) {
        activity = act;
        //To determine keypad type
        String deviceID = SystemProperties.get("sys.device.id", null);
        if (deviceID != null && deviceID.length() > 0) {
            char keypad = deviceID.charAt(5);  // the sixth digit of device ID indicates device keypad type
            if (keypad == '3') {
                // 30-key device
                gKeypadType = KEYPAD_TYPE_30KEY;
            } else if (keypad == '4') {
                // 38-key device
                gKeypadType = KEYPAD_TYPE_38KEY;
            } else if(keypad == '5') {
                // VT 53-key device
                gKeypadType = KEYPAD_TYPE_VT_53KEY;
            } else if(keypad == '6') {
                // TN 5250 53-key device
                gKeypadType = KEYPAD_TYPE_TN_5250_53KEY;
            } else if(keypad == '7') {
                // TN 3570 53-key device
                gKeypadType = KEYPAD_TYPE_TN_3270_53KEY;
            }
        } else {
            // device ID not found
            gKeypadType = KEYPAD_TYPE_NOKEY;
        }

        gCurrentEditSessionIndex = 0;
    }

    public static MainActivity getCurrActivity() {
        return activity;
        // Now here you can get getApplication()
    }

    public static void ApplicationVibration(long milliseconds) {
        Vibrator myVibrator = (Vibrator) getCurrActivity().getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        myVibrator.vibrate(milliseconds);
    }

    public static boolean is53Key() {
        if(gKeypadType == KEYPAD_TYPE_TN_5250_53KEY ||
                gKeypadType == KEYPAD_TYPE_TN_3270_53KEY ||
                gKeypadType == KEYPAD_TYPE_VT_53KEY)
            return true;
        return false;
    }

    public static boolean hasKey() {
        return  gKeypadType != KEYPAD_TYPE_NOKEY;
    }
}
