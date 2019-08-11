package com.aegean.icsd.engine.common.beans;

public class EngineException extends Throwable {

  private String code;

  public EngineException(String code, String msg) {
    super(msg);
    this.code = code;
  }

  public EngineException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
  }

  public String getCodeMessage() {
    return this.code + ": " + this.getMessage();
  }

}