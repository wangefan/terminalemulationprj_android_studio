/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkUnixCompress {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected CkUnixCompress(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CkUnixCompress obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        chilkatJNI.delete_CkUnixCompress(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CkUnixCompress() {
    this(chilkatJNI.new_CkUnixCompress(), true);
  }

  public void LastErrorXml(CkString str) {
    chilkatJNI.CkUnixCompress_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorHtml(CkString str) {
    chilkatJNI.CkUnixCompress_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorText(CkString str) {
    chilkatJNI.CkUnixCompress_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void get_LastErrorHtml(CkString str) {
    chilkatJNI.CkUnixCompress_get_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorHtml() {
    return chilkatJNI.CkUnixCompress_lastErrorHtml(swigCPtr, this);
  }

  public void get_LastErrorText(CkString str) {
    chilkatJNI.CkUnixCompress_get_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorText() {
    return chilkatJNI.CkUnixCompress_lastErrorText(swigCPtr, this);
  }

  public void get_LastErrorXml(CkString str) {
    chilkatJNI.CkUnixCompress_get_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorXml() {
    return chilkatJNI.CkUnixCompress_lastErrorXml(swigCPtr, this);
  }

  public void get_Version(CkString str) {
    chilkatJNI.CkUnixCompress_get_Version(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String version() {
    return chilkatJNI.CkUnixCompress_version(swigCPtr, this);
  }

  public void get_DebugLogFilePath(CkString str) {
    chilkatJNI.CkUnixCompress_get_DebugLogFilePath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String debugLogFilePath() {
    return chilkatJNI.CkUnixCompress_debugLogFilePath(swigCPtr, this);
  }

  public void put_DebugLogFilePath(String newVal) {
    chilkatJNI.CkUnixCompress_put_DebugLogFilePath(swigCPtr, this, newVal);
  }

  public boolean get_VerboseLogging() {
    return chilkatJNI.CkUnixCompress_get_VerboseLogging(swigCPtr, this);
  }

  public void put_VerboseLogging(boolean newVal) {
    chilkatJNI.CkUnixCompress_put_VerboseLogging(swigCPtr, this, newVal);
  }

  public boolean SaveLastError(String path) {
    return chilkatJNI.CkUnixCompress_SaveLastError(swigCPtr, this, path);
  }

  public int get_HeartbeatMs() {
    return chilkatJNI.CkUnixCompress_get_HeartbeatMs(swigCPtr, this);
  }

  public void put_HeartbeatMs(int newVal) {
    chilkatJNI.CkUnixCompress_put_HeartbeatMs(swigCPtr, this, newVal);
  }

  public boolean CompressFile(String inFilename, String destPath) {
    return chilkatJNI.CkUnixCompress_CompressFile(swigCPtr, this, inFilename, destPath);
  }

  public boolean CompressFileToMem(String inFilename, CkByteData outData) {
    return chilkatJNI.CkUnixCompress_CompressFileToMem(swigCPtr, this, inFilename, CkByteData.getCPtr(outData), outData);
  }

  public boolean CompressMemToFile(CkByteData inData, String destPath) {
    return chilkatJNI.CkUnixCompress_CompressMemToFile(swigCPtr, this, CkByteData.getCPtr(inData), inData, destPath);
  }

  public boolean CompressMemory(CkByteData inData, CkByteData outData) {
    return chilkatJNI.CkUnixCompress_CompressMemory(swigCPtr, this, CkByteData.getCPtr(inData), inData, CkByteData.getCPtr(outData), outData);
  }

  public boolean CompressString(String inStr, String charset, CkByteData outBytes) {
    return chilkatJNI.CkUnixCompress_CompressString(swigCPtr, this, inStr, charset, CkByteData.getCPtr(outBytes), outBytes);
  }

  public boolean CompressStringToFile(String inStr, String charset, String destPath) {
    return chilkatJNI.CkUnixCompress_CompressStringToFile(swigCPtr, this, inStr, charset, destPath);
  }

  public boolean IsUnlocked() {
    return chilkatJNI.CkUnixCompress_IsUnlocked(swigCPtr, this);
  }

  public boolean UnTarZ(String zFilename, String destDir, boolean bNoAbsolute) {
    return chilkatJNI.CkUnixCompress_UnTarZ(swigCPtr, this, zFilename, destDir, bNoAbsolute);
  }

  public boolean UncompressFile(String inFilename, String destPath) {
    return chilkatJNI.CkUnixCompress_UncompressFile(swigCPtr, this, inFilename, destPath);
  }

  public boolean UncompressFileToMem(String inFilename, CkByteData outData) {
    return chilkatJNI.CkUnixCompress_UncompressFileToMem(swigCPtr, this, inFilename, CkByteData.getCPtr(outData), outData);
  }

  public boolean UncompressFileToString(String inFilename, String inCharset, CkString outStr) {
    return chilkatJNI.CkUnixCompress_UncompressFileToString(swigCPtr, this, inFilename, inCharset, CkString.getCPtr(outStr), outStr);
  }

  public String uncompressFileToString(String inFilename, String inCharset) {
    return chilkatJNI.CkUnixCompress_uncompressFileToString(swigCPtr, this, inFilename, inCharset);
  }

  public boolean UncompressMemToFile(CkByteData inData, String destPath) {
    return chilkatJNI.CkUnixCompress_UncompressMemToFile(swigCPtr, this, CkByteData.getCPtr(inData), inData, destPath);
  }

  public boolean UncompressMemory(CkByteData inData, CkByteData outData) {
    return chilkatJNI.CkUnixCompress_UncompressMemory(swigCPtr, this, CkByteData.getCPtr(inData), inData, CkByteData.getCPtr(outData), outData);
  }

  public boolean UncompressString(CkByteData inData, String inCharset, CkString outStr) {
    return chilkatJNI.CkUnixCompress_UncompressString(swigCPtr, this, CkByteData.getCPtr(inData), inData, inCharset, CkString.getCPtr(outStr), outStr);
  }

  public String uncompressString(CkByteData inData, String inCharset) {
    return chilkatJNI.CkUnixCompress_uncompressString(swigCPtr, this, CkByteData.getCPtr(inData), inData, inCharset);
  }

  public boolean UnlockComponent(String unlockCode) {
    return chilkatJNI.CkUnixCompress_UnlockComponent(swigCPtr, this, unlockCode);
  }

}
