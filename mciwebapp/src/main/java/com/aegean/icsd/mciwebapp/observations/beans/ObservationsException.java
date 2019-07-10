package com.aegean.icsd.mciwebapp.observations.beans;

public class ObservationsException extends Throwable {

  private String code;

  public ObservationsException(String code, String msg) {
    super(msg);
    this.code = code;
  }

  public ObservationsException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
  }

  public String getCodeMessage() {
    return this.code + ": " + this.getMessage();
  }
}
