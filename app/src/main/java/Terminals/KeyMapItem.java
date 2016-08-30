package Terminals;


import com.google.gson.annotations.SerializedName;

public class KeyMapItem {
    public static final int UNDEFINE_PHY = -1;

    @SerializedName(value="VTFunKeyVal", alternate={"TNFunKeyVal"})
    public int mServerKeycode = 0;

    @SerializedName(value="VTPhyKeyCode", alternate={"TNPhyKeyCode"})
    public int mPhysicalKeycode = 0;

    public KeyMapItem() {
    }

    public KeyMapItem (int serverKeycode, int physicalKeycode) {
        mServerKeycode = serverKeycode;
        mPhysicalKeycode = physicalKeycode;
    }
}
