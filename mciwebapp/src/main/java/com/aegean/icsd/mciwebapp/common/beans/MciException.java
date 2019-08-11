package com.aegean.icsd.mciwebapp.common.beans;

public class MciException extends Exception {

  private String code;

  public MciException(String code, String msg) {
    super(msg);
    this.code = code;
  }

  public MciException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
  }

  public String getCode() {
    return this.code;
  }

  public String getCodeMessage() {
    return this.code + ": " + this.getMessage();
  }
}
