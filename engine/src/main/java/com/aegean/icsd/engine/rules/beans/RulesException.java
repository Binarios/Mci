package com.aegean.icsd.engine.rules.beans;

public class RulesException extends Throwable{
  private String code;
  private String msg;

  public RulesException(String code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public RulesException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
    this.msg = msg;
  }
}
