package Terminals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class LanguageTable {
    private java.util.Map<String, String> mBig5Map = new java.util.HashMap<>();
    private java.util.Map<String, String> mGBMap = new java.util.HashMap<>();
    private java.util.Map<String, String> mKorMap = new java.util.HashMap<>();
    private java.util.Map<String, String> mJapMap = new java.util.HashMap<>();
    static private LanguageTable mMe = null;
    static public LanguageTable instance() {
        if(mMe == null) {
            mMe = new LanguageTable();
        }
        return mMe;
    }
    private LanguageTable() {
        mBig5Map.clear();
        mGBMap.clear();
        mKorMap.clear();
        mJapMap.clear();
    }

    private void createTableFromFile(final String fileName, java.util.Map<String, String> map, final String encode) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(stdActivityRef.getCurrActivity().getAssets().open(fileName)));
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
                map.put(new String(new char [] {ch1, ch2}), new String(new byte [] {by1, by2}, encode));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String findBig5(final String key) {
        if(mBig5Map.size() <= 0) { //create table
            createTableFromFile("Big5Table.txt", mBig5Map, "Big5");
        }
        return mBig5Map.get(key);
    }

    public String findGB(String key) {
        if(mGBMap.size() <= 0) { //create table
            createTableFromFile("GBTable.txt", mGBMap, "GBK");
        }
        return mGBMap.get(key);
    }

    public String findKor(String key) {
        if(mKorMap.size() <= 0) { //create table
            createTableFromFile("KorTable.txt", mKorMap, "MS949");
        }
        return mKorMap.get(key);
    }

    public String findJap(String key) {
        if(mJapMap.size() <= 0) { //create table
            createTableFromFile("JapTable.txt", mJapMap, "MS932");
        }
        return mJapMap.get(key);
    }
}
