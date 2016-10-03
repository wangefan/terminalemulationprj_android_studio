/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkStringBuilder {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected CkStringBuilder(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CkStringBuilder obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        chilkatJNI.delete_CkStringBuilder(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CkStringBuilder() {
    this(chilkatJNI.new_CkStringBuilder(), true);
  }

  public int get_Length() {
    return chilkatJNI.CkStringBuilder_get_Length(swigCPtr, this);
  }

  public boolean Append(String value) {
    return chilkatJNI.CkStringBuilder_Append(swigCPtr, this, value);
  }

  public boolean AppendEncoded(CkByteData binaryData, String encoding) {
    return chilkatJNI.CkStringBuilder_AppendEncoded(swigCPtr, this, CkByteData.getCPtr(binaryData), binaryData, encoding);
  }

  public boolean AppendInt(int value) {
    return chilkatJNI.CkStringBuilder_AppendInt(swigCPtr, this, value);
  }

  public boolean AppendInt64(long value) {
    return chilkatJNI.CkStringBuilder_AppendInt64(swigCPtr, this, value);
  }

  public void Clear() {
    chilkatJNI.CkStringBuilder_Clear(swigCPtr, this);
  }

  public boolean Contains(String str, boolean caseSensitive) {
    return chilkatJNI.CkStringBuilder_Contains(swigCPtr, this, str, caseSensitive);
  }

  public boolean Equals(String str, boolean caseSensitive) {
    return chilkatJNI.CkStringBuilder_Equals(swigCPtr, this, str, caseSensitive);
  }

  public boolean GetAsString(CkString outStr) {
    return chilkatJNI.CkStringBuilder_GetAsString(swigCPtr, this, CkString.getCPtr(outStr), outStr);
  }

  public String getAsString() {
    return chilkatJNI.CkStringBuilder_getAsString(swigCPtr, this);
  }

  public String asString() {
    return chilkatJNI.CkStringBuilder_asString(swigCPtr, this);
  }

  public void Replace(String value, String replacement) {
    chilkatJNI.CkStringBuilder_Replace(swigCPtr, this, value, replacement);
  }

}
