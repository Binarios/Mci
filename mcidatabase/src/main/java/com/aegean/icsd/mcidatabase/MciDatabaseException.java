package com.aegean.icsd.mcidatabase;

public class MciDatabaseException extends Throwable {
  private String code;
  private String msg;

  public MciDatabaseException(String code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public MciDatabaseException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
    this.msg = msg;
  }
}
