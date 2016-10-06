package Terminals;

import android.media.MediaScannerConnection;

import com.te.UI.CipherUtility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TerminalLogWriter {
    private FileOutputStream mOutputStream;
    private String mFilePath = "";
    public TerminalLogWriter() {
    }

    private void openFile() {
        try {
            int nCurSession = TESettingsInfo.getSessionIndex();
            String fileName = String.format("TE.S%d.", nCurSession + 1);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 'at' HH-mm-ss");
            fileName += df.format(Calendar.getInstance().getTime());
            fileName += ".log";
            File file = new File(CipherUtility.getTESettingsPath(stdActivityRef.getCurrActivity()) + fileName);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                mFilePath = file.toString();
                MediaScannerConnection.scanFile(stdActivityRef.getCurrActivity(), new String[] {}, null, null);
            }
            mOutputStream = new FileOutputStream(file, true);
        } catch (Exception e) {
            e.printStackTrace();
            mOutputStream = null;
        }
    }

    public void endLog() {
        if(mOutputStream != null) {
            try {
                mOutputStream.close();
                MediaScannerConnection.scanFile(stdActivityRef.getCurrActivity(), new String[] {mFilePath}, null, null);
                mFilePath = "";
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOutputStream = null;
        }
    }

    public void write(String Title, byte[] data, int len, boolean isRecv) {
        if(mOutputStream == null) {
            openFile();
        }

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        String time  = df.format(Calendar.getInstance().getTime());
        String logTitle;
        if (isRecv)
            logTitle = Title + ":recv:" + time;
        else
            logTitle = Title + ":send:" + time;

        String hexStr = "";
        for (int i = 0; i < len; i++) {
            String hex = String.format("[%02x]", (data[i] & 0xFF));
            hexStr += hex;
        }
        String content = logTitle + hexStr + "\r\n";
        try {
            mOutputStream.write(content.getBytes());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
