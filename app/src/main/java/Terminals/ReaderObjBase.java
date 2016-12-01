package Terminals;

import android.content.Context;

public abstract class ReaderObjBase {
    abstract public void releaseReader(Context context);
    abstract public void setActived(Boolean Enable);
}
