package Terminals;


import com.google.gson.annotations.SerializedName;

public class KeyMapItem {
    @SerializedName(value="VTFunKeyVal", alternate={"TNFunKeyVal"})
    public int mServerKeycode = 0;

    @SerializedName(value="VTPhyKeyCode", alternate={"TNPhyKeyCode"})
    public int mPhysicalKeycode = 0;

    public KeyMapItem() {
    }
}
