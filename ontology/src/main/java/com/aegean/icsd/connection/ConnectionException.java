package com.aegean.icsd.connection;

public class ConnectionException extends Throwable {
  private String code;
  private String msg;

  public ConnectionException(String code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public ConnectionException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
    this.msg = msg;
  }
}
