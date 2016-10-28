package Terminals;

import com.google.gson.annotations.SerializedName;

public class KeyMapItemVT extends KeyMapItem {
    @SerializedName(value="VTFunKeyVal")
    protected int mServerKeycode = 0;

    @SerializedName(value="VTPhyKeyCode")
    protected int mPhysicalKeycode = 0;

    public KeyMapItemVT (int serverKeycode, int physicalKeycode) {
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
