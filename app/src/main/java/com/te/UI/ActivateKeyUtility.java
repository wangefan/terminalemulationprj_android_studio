package com.te.UI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import Terminals.stdActivityRef;

public class ActivateKeyUtility {
    private static final String DEFAULT_KEY_NAME = "A905RSEKEY";
    private static final String PAT1 = "J2ZGYK7WNPS3HXA5VMB4RLC6DQ8ETF9U";
    private static final String PAT2 = "SMBUVE6DQ5J7W4RKLNPA9TF83CHX2ZGY";
    private static final String PDF = "27V3MSJQ5238F6C";

    private static ActivateKeyUtility ourInstance = new ActivateKeyUtility();

    private static String mSerialNo = "9711460PP0067";
    private static String mSoftwareNo = "4001081";

    public static ActivateKeyUtility getInstance() {
        return ourInstance;
    }

    private ActivateKeyUtility() {
        mSerialNo = "";/*todo:get from Agent*/
        mSoftwareNo = "";/*todo:replace to valid one*/
    }

    private static String getValidKey() {
        if (mSerialNo.isEmpty() || mSoftwareNo.isEmpty())
            return "";
        String sn = mSerialNo + mSoftwareNo;

        int k = 0;
        int strLen = sn.length();
        String c = null;

        byte[] ActKey = PDF.getBytes();
        c = new String(ActKey);
        for (int i = 0; i < strLen; i++) {
            char ch = sn.charAt(strLen - 1 - i);
            k = (int) ch + i * 3 + k;
            for (int j = 0; j < c.length(); j++) {
                char cMid = PAT1.charAt(((k + j) % 32));
                int iAsc = (int) cMid + j;
                int C = ((int)(ActKey[j]) + iAsc) % 256;
                ActKey[j] = (byte)(C);
            }
        }

        for (int i = 0; i < c.length(); i++) {
            int startIndex = ((ActKey[i] & 0xFF) % 32);
            char cMid = PAT2.charAt(startIndex);
            int iAsc = (int) cMid;
            ActKey[i] = (byte)(iAsc);
        }

        return new String(ActKey);
    }

    private static String genKeyFileName(int nIndex) {
        return String.format("%s%3d.txt", DEFAULT_KEY_NAME, nIndex);
    }

    private static File getKeyFile() {
        File keyFile = new File(stdActivityRef.getCurrActivity().getFilesDir(), genKeyFileName(0));
        if(keyFile.exists() == false) {//means can`t find A905RSEKEY000.txt
            for(int idx = 1; idx < 1000; ++idx) {
                //Todo:should find directory from external storage.
                keyFile = new File(stdActivityRef.getCurrActivity().getFilesDir(), genKeyFileName(idx));
                if(keyFile.exists()) {
                    break;
                }
            }
        }
        return keyFile;
    }

    private static String getKeyFromFile(File keyFile) {
        if (keyFile == null || keyFile.isDirectory() || keyFile.exists() == false)
            return "";
        String key = "";
        String serialNoAndSoftNo = mSerialNo + mSoftwareNo;
        try {
            FileReader fileReader = new FileReader(keyFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                int index = line.indexOf('=');
                if (index <= 0)
                    continue;
                String fileSerialNoAndSoftNo = line.substring(0, index);
                if (serialNoAndSoftNo.compareTo(fileSerialNoAndSoftNo) == 0) {
                    key = line.substring(index + 1);
                    break;
                }
            }
            fileReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return key;
    }

    public static boolean verifyKeyFromDefaultFile() {
        if (mSerialNo.isEmpty() || mSoftwareNo.isEmpty())
            return false;
        String strKey = getKeyFromFile(getKeyFile());
        String strValidKey = getValidKey();
        return (strKey.compareTo(strValidKey) == 0);
    }

    public static boolean verifyKey(String key) {
        if (mSerialNo.isEmpty() || mSoftwareNo.isEmpty())
            return false;
        String strValidKey = getValidKey();
        return (key.compareTo(strValidKey) == 0);
    }

    public static void genKeyFile() {
        File keyFile = new File(stdActivityRef.getCurrActivity().getFilesDir(), genKeyFileName(0));
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(keyFile, "UTF-8");
            String value = mSerialNo + mSoftwareNo + "=" + getValidKey() + "\r\n";
            writer.println(value);
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
