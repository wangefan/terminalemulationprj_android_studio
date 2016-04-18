/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkDirTree {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected CkDirTree(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CkDirTree obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        chilkatJNI.delete_CkDirTree(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CkDirTree() {
    this(chilkatJNI.new_CkDirTree(), true);
  }

  public void LastErrorXml(CkString str) {
    chilkatJNI.CkDirTree_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorHtml(CkString str) {
    chilkatJNI.CkDirTree_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorText(CkString str) {
    chilkatJNI.CkDirTree_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void get_LastErrorHtml(CkString str) {
    chilkatJNI.CkDirTree_get_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorHtml() {
    return chilkatJNI.CkDirTree_lastErrorHtml(swigCPtr, this);
  }

  public void get_LastErrorText(CkString str) {
    chilkatJNI.CkDirTree_get_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorText() {
    return chilkatJNI.CkDirTree_lastErrorText(swigCPtr, this);
  }

  public void get_LastErrorXml(CkString str) {
    chilkatJNI.CkDirTree_get_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorXml() {
    return chilkatJNI.CkDirTree_lastErrorXml(swigCPtr, this);
  }

  public void get_Version(CkString str) {
    chilkatJNI.CkDirTree_get_Version(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String version() {
    return chilkatJNI.CkDirTree_version(swigCPtr, this);
  }

  public void get_DebugLogFilePath(CkString str) {
    chilkatJNI.CkDirTree_get_DebugLogFilePath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String debugLogFilePath() {
    return chilkatJNI.CkDirTree_debugLogFilePath(swigCPtr, this);
  }

  public void put_DebugLogFilePath(String newVal) {
    chilkatJNI.CkDirTree_put_DebugLogFilePath(swigCPtr, this, newVal);
  }

  public boolean get_VerboseLogging() {
    return chilkatJNI.CkDirTree_get_VerboseLogging(swigCPtr, this);
  }

  public void put_VerboseLogging(boolean newVal) {
    chilkatJNI.CkDirTree_put_VerboseLogging(swigCPtr, this, newVal);
  }

  public boolean SaveLastError(String path) {
    return chilkatJNI.CkDirTree_SaveLastError(swigCPtr, this, path);
  }

  public void get_BaseDir(CkString str) {
    chilkatJNI.CkDirTree_get_BaseDir(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String baseDir() {
    return chilkatJNI.CkDirTree_baseDir(swigCPtr, this);
  }

  public void put_BaseDir(String newVal) {
    chilkatJNI.CkDirTree_put_BaseDir(swigCPtr, this, newVal);
  }

  public boolean get_DoneIterating() {
    return chilkatJNI.CkDirTree_get_DoneIterating(swigCPtr, this);
  }

  public int get_FileSize32() {
    return chilkatJNI.CkDirTree_get_FileSize32(swigCPtr, this);
  }

  public void get_FullPath(CkString str) {
    chilkatJNI.CkDirTree_get_FullPath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String fullPath() {
    return chilkatJNI.CkDirTree_fullPath(swigCPtr, this);
  }

  public void get_FullUncPath(CkString str) {
    chilkatJNI.CkDirTree_get_FullUncPath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String fullUncPath() {
    return chilkatJNI.CkDirTree_fullUncPath(swigCPtr, this);
  }

  public boolean get_IsDirectory() {
    return chilkatJNI.CkDirTree_get_IsDirectory(swigCPtr, this);
  }

  public boolean get_Recurse() {
    return chilkatJNI.CkDirTree_get_Recurse(swigCPtr, this);
  }

  public void put_Recurse(boolean newVal) {
    chilkatJNI.CkDirTree_put_Recurse(swigCPtr, this, newVal);
  }

  public void get_RelativePath(CkString str) {
    chilkatJNI.CkDirTree_get_RelativePath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String relativePath() {
    return chilkatJNI.CkDirTree_relativePath(swigCPtr, this);
  }

  public boolean AdvancePosition() {
    return chilkatJNI.CkDirTree_AdvancePosition(swigCPtr, this);
  }

  public boolean BeginIterate() {
    return chilkatJNI.CkDirTree_BeginIterate(swigCPtr, this);
  }

}
