package com.aegean.icsd.engine.generator.beans;


public class GeneratorException extends Throwable {

  private String code;

  public GeneratorException(String code, String msg) {
    super(msg);
    this.code = code;
  }

  public GeneratorException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
  }

  public String getCodeMessage() {
    return this.code + ": " + this.getMessage();
  }

}
