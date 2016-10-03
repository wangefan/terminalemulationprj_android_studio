/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.5
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkGlobal {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected CkGlobal(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CkGlobal obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        chilkatJNI.delete_CkGlobal(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CkGlobal() {
    this(chilkatJNI.new_CkGlobal(), true);
  }

  public void LastErrorXml(CkString str) {
    chilkatJNI.CkGlobal_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorHtml(CkString str) {
    chilkatJNI.CkGlobal_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorText(CkString str) {
    chilkatJNI.CkGlobal_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public int get_AnsiCodePage() {
    return chilkatJNI.CkGlobal_get_AnsiCodePage(swigCPtr, this);
  }

  public void put_AnsiCodePage(int newVal) {
    chilkatJNI.CkGlobal_put_AnsiCodePage(swigCPtr, this, newVal);
  }

  public void get_DebugLogFilePath(CkString str) {
    chilkatJNI.CkGlobal_get_DebugLogFilePath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String debugLogFilePath() {
    return chilkatJNI.CkGlobal_debugLogFilePath(swigCPtr, this);
  }

  public void put_DebugLogFilePath(String newVal) {
    chilkatJNI.CkGlobal_put_DebugLogFilePath(swigCPtr, this, newVal);
  }

  public int get_DefaultNtlmVersion() {
    return chilkatJNI.CkGlobal_get_DefaultNtlmVersion(swigCPtr, this);
  }

  public void put_DefaultNtlmVersion(int newVal) {
    chilkatJNI.CkGlobal_put_DefaultNtlmVersion(swigCPtr, this, newVal);
  }

  public boolean get_DefaultUtf8() {
    return chilkatJNI.CkGlobal_get_DefaultUtf8(swigCPtr, this);
  }

  public void put_DefaultUtf8(boolean newVal) {
    chilkatJNI.CkGlobal_put_DefaultUtf8(swigCPtr, this, newVal);
  }

  public int get_DnsTimeToLive() {
    return chilkatJNI.CkGlobal_get_DnsTimeToLive(swigCPtr, this);
  }

  public void put_DnsTimeToLive(int newVal) {
    chilkatJNI.CkGlobal_put_DnsTimeToLive(swigCPtr, this, newVal);
  }

  public boolean get_EnableDnsCaching() {
    return chilkatJNI.CkGlobal_get_EnableDnsCaching(swigCPtr, this);
  }

  public void put_EnableDnsCaching(boolean newVal) {
    chilkatJNI.CkGlobal_put_EnableDnsCaching(swigCPtr, this, newVal);
  }

  public void get_LastErrorHtml(CkString str) {
    chilkatJNI.CkGlobal_get_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorHtml() {
    return chilkatJNI.CkGlobal_lastErrorHtml(swigCPtr, this);
  }

  public void get_LastErrorText(CkString str) {
    chilkatJNI.CkGlobal_get_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorText() {
    return chilkatJNI.CkGlobal_lastErrorText(swigCPtr, this);
  }

  public void get_LastErrorXml(CkString str) {
    chilkatJNI.CkGlobal_get_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorXml() {
    return chilkatJNI.CkGlobal_lastErrorXml(swigCPtr, this);
  }

  public boolean get_LastMethodSuccess() {
    return chilkatJNI.CkGlobal_get_LastMethodSuccess(swigCPtr, this);
  }

  public void put_LastMethodSuccess(boolean newVal) {
    chilkatJNI.CkGlobal_put_LastMethodSuccess(swigCPtr, this, newVal);
  }

  public int get_MaxThreads() {
    return chilkatJNI.CkGlobal_get_MaxThreads(swigCPtr, this);
  }

  public void put_MaxThreads(int newVal) {
    chilkatJNI.CkGlobal_put_MaxThreads(swigCPtr, this, newVal);
  }

  public void get_ThreadPoolLogPath(CkString str) {
    chilkatJNI.CkGlobal_get_ThreadPoolLogPath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String threadPoolLogPath() {
    return chilkatJNI.CkGlobal_threadPoolLogPath(swigCPtr, this);
  }

  public void put_ThreadPoolLogPath(String newVal) {
    chilkatJNI.CkGlobal_put_ThreadPoolLogPath(swigCPtr, this, newVal);
  }

  public boolean get_VerboseLogging() {
    return chilkatJNI.CkGlobal_get_VerboseLogging(swigCPtr, this);
  }

  public void put_VerboseLogging(boolean newVal) {
    chilkatJNI.CkGlobal_put_VerboseLogging(swigCPtr, this, newVal);
  }

  public void get_Version(CkString str) {
    chilkatJNI.CkGlobal_get_Version(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String version() {
    return chilkatJNI.CkGlobal_version(swigCPtr, this);
  }

  public boolean DnsClearCache() {
    return chilkatJNI.CkGlobal_DnsClearCache(swigCPtr, this);
  }

  public boolean FinalizeThreadPool() {
    return chilkatJNI.CkGlobal_FinalizeThreadPool(swigCPtr, this);
  }

  public boolean SaveLastError(String path) {
    return chilkatJNI.CkGlobal_SaveLastError(swigCPtr, this, path);
  }

  public boolean UnlockBundle(String bundleUnlockCode) {
    return chilkatJNI.CkGlobal_UnlockBundle(swigCPtr, this, bundleUnlockCode);
  }

}
