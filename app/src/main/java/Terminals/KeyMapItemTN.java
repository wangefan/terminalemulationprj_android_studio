package Terminals;

import com.google.gson.annotations.SerializedName;

public class KeyMapItemTN extends KeyMapItem {
    @SerializedName(value="TNFunKeyVal")
    protected int mServerKeycode = 0;

    @SerializedName(value="TNPhyKeyCode")
    protected int mPhysicalKeycode = 0;

    public KeyMapItemTN (int serverKeycode, int physicalKeycode) {
        super();
        mServerKeycode = serverKeycode;
        mPhysicalKeycode = physicalKeycode;
    }

    @Override
    public int getServerKeycode() {
        return mServerKeycode;
    }

    @Override
    public int getPhysicalKeycode() {
        return mPhysicalKeycode;
    }

    @Override
    public void setServerKeycode(int nServerKeycode) {
        mServerKeycode = nServerKeycode;
    }

    @Override
    public void setPhysicalKeycode(int nPhysicalKeycode) {
        mPhysicalKeycode = nPhysicalKeycode;
    }
}
