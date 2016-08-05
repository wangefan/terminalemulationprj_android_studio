package Terminals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Big5 {
    private java.util.Map<String, String> mBig5Map = new java.util.HashMap<>();
    static private Big5 mMe = null;
    static public Big5 instance() {
        if(mMe == null) {
            mMe = new Big5();
        }
        return mMe;
    }
    public String find(final String key) {
        return mBig5Map.get(key);
    }

    private Big5() {
        mBig5Map.clear();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stdActivityRef.getCurrActivity().getAssets().open("Big5Table.txt")));
            String line = "";
            while ((line = reader.readLine()) != null) {
                if(line.length() <= 0)
                    continue;
                String [] chs = line.split(",");
                if(chs.length <= 0)
                    continue;
                char ch1 = (char) (Integer.parseInt(chs[0], 16) & 0xff);
                char ch2 = (char) (Integer.parseInt(chs[1], 16) & 0xff);
                byte by1 = (byte) (Integer.parseInt(chs[2], 16) & 0xff);
                byte by2 = (byte) (Integer.parseInt(chs[3], 16) & 0xff);
                mBig5Map.put(new String(new char [] {ch1, ch2}), new String(new byte [] {by1, by2}, "Big5"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
