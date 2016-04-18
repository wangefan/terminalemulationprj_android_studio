/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.10
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkPfx {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected CkPfx(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CkPfx obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        chilkatJNI.delete_CkPfx(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public CkPfx() {
    this(chilkatJNI.new_CkPfx(), true);
  }

  public void LastErrorXml(CkString str) {
    chilkatJNI.CkPfx_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorHtml(CkString str) {
    chilkatJNI.CkPfx_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void LastErrorText(CkString str) {
    chilkatJNI.CkPfx_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public void get_LastErrorHtml(CkString str) {
    chilkatJNI.CkPfx_get_LastErrorHtml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorHtml() {
    return chilkatJNI.CkPfx_lastErrorHtml(swigCPtr, this);
  }

  public void get_LastErrorText(CkString str) {
    chilkatJNI.CkPfx_get_LastErrorText(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorText() {
    return chilkatJNI.CkPfx_lastErrorText(swigCPtr, this);
  }

  public void get_LastErrorXml(CkString str) {
    chilkatJNI.CkPfx_get_LastErrorXml(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String lastErrorXml() {
    return chilkatJNI.CkPfx_lastErrorXml(swigCPtr, this);
  }

  public void get_Version(CkString str) {
    chilkatJNI.CkPfx_get_Version(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String version() {
    return chilkatJNI.CkPfx_version(swigCPtr, this);
  }

  public void get_DebugLogFilePath(CkString str) {
    chilkatJNI.CkPfx_get_DebugLogFilePath(swigCPtr, this, CkString.getCPtr(str), str);
  }

  public String debugLogFilePath() {
    return chilkatJNI.CkPfx_debugLogFilePath(swigCPtr, this);
  }

  public void put_DebugLogFilePath(String newVal) {
    chilkatJNI.CkPfx_put_DebugLogFilePath(swigCPtr, this, newVal);
  }

  public boolean get_VerboseLogging() {
    return chilkatJNI.CkPfx_get_VerboseLogging(swigCPtr, this);
  }

  public void put_VerboseLogging(boolean newVal) {
    chilkatJNI.CkPfx_put_VerboseLogging(swigCPtr, this, newVal);
  }

  public boolean SaveLastError(String path) {
    return chilkatJNI.CkPfx_SaveLastError(swigCPtr, this, path);
  }

  public int get_NumCerts() {
    return chilkatJNI.CkPfx_get_NumCerts(swigCPtr, this);
  }

  public int get_NumPrivateKeys() {
    return chilkatJNI.CkPfx_get_NumPrivateKeys(swigCPtr, this);
  }

  public boolean AddCert(CkCert cert, boolean includeChain) {
    return chilkatJNI.CkPfx_AddCert(swigCPtr, this, CkCert.getCPtr(cert), cert, includeChain);
  }

  public boolean AddPrivateKey(CkPrivateKey privKey, CkCertChain certChain) {
    return chilkatJNI.CkPfx_AddPrivateKey(swigCPtr, this, CkPrivateKey.getCPtr(privKey), privKey, CkCertChain.getCPtr(certChain), certChain);
  }

  public CkCert GetCert(int index) {
    long cPtr = chilkatJNI.CkPfx_GetCert(swigCPtr, this, index);
    return (cPtr == 0) ? null : new CkCert(cPtr, true);
  }

  public CkPrivateKey GetPrivateKey(int index) {
    long cPtr = chilkatJNI.CkPfx_GetPrivateKey(swigCPtr, this, index);
    return (cPtr == 0) ? null : new CkPrivateKey(cPtr, true);
  }

  public boolean LoadPfxBytes(CkByteData pfxData, String password) {
    return chilkatJNI.CkPfx_LoadPfxBytes(swigCPtr, this, CkByteData.getCPtr(pfxData), pfxData, password);
  }

  public boolean LoadPfxEncoded(String encodedData, String encoding, String password) {
    return chilkatJNI.CkPfx_LoadPfxEncoded(swigCPtr, this, encodedData, encoding, password);
  }

  public boolean LoadPfxFile(String path, String password) {
    return chilkatJNI.CkPfx_LoadPfxFile(swigCPtr, this, path, password);
  }

  public boolean ToBinary(String password, CkByteData outBytes) {
    return chilkatJNI.CkPfx_ToBinary(swigCPtr, this, password, CkByteData.getCPtr(outBytes), outBytes);
  }

  public boolean ToEncodedString(String password, String encoding, CkString outStr) {
    return chilkatJNI.CkPfx_ToEncodedString(swigCPtr, this, password, encoding, CkString.getCPtr(outStr), outStr);
  }

  public String toEncodedString(String password, String encoding) {
    return chilkatJNI.CkPfx_toEncodedString(swigCPtr, this, password, encoding);
  }

  public boolean ToFile(String password, String path) {
    return chilkatJNI.CkPfx_ToFile(swigCPtr, this, password, path);
  }

  public CkJavaKeyStore ToJavaKeyStore(String alias, String password) {
    long cPtr = chilkatJNI.CkPfx_ToJavaKeyStore(swigCPtr, this, alias, password);
    return (cPtr == 0) ? null : new CkJavaKeyStore(cPtr, true);
  }

  public boolean UseCertVault(CkXmlCertVault vault) {
    return chilkatJNI.CkPfx_UseCertVault(swigCPtr, this, CkXmlCertVault.getCPtr(vault), vault);
  }

  public boolean ToPem(CkString outStr) {
    return chilkatJNI.CkPfx_ToPem(swigCPtr, this, CkString.getCPtr(outStr), outStr);
  }

  public String toPem() {
    return chilkatJNI.CkPfx_toPem(swigCPtr, this);
  }

  public boolean LoadPem(String pemStr, String password) {
    return chilkatJNI.CkPfx_LoadPem(swigCPtr, this, pemStr, password);
  }

}
