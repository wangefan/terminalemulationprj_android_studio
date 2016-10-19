package Terminals;

import android.os.SystemClock;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.inputmethod.BaseInputConnection;

public class CViweInputConnection extends BaseInputConnection {
    private ContentView mContentView;
    static private KeyCharacterMap mKeyCharacterMap = null;

    public CViweInputConnection(ContentView targetView, boolean fullEditor) {
        super(targetView, fullEditor);
        mContentView = targetView;
    }

    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        return mContentView.dispatchKeyEvent(event);
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
        char [] chars = text.toString().toCharArray();
        if (chars != null) {
            if (chars.length == 0) {
                return false;
            }
            if (chars.length == 1) {
                // If it's 1 character, we have a chance of being
                // able to generate normal key events...
                if (mKeyCharacterMap == null) {
                    mKeyCharacterMap = KeyCharacterMap.load(KeyCharacterMap.VIRTUAL_KEYBOARD);
                }
                KeyEvent[] events = mKeyCharacterMap.getEvents(chars);
                if (events != null) {
                    for (int i=0; i<events.length; i++) {
                        sendKeyEvent(events[i]);
                    }
                    return true;
                }
            }

            // Otherwise, revert to the special key event containing
            // the actual characters.
            KeyEvent event = new KeyEvent(SystemClock.uptimeMillis(),
                    chars.toString(), KeyCharacterMap.VIRTUAL_KEYBOARD, 0);
            sendKeyEvent(event);
        }
        return true;
    }
}
