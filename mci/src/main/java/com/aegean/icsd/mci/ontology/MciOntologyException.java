package com.aegean.icsd.mci.ontology;

public class MciOntologyException extends Throwable {
  private String code;
  private String msg;

  public MciOntologyException(String code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public MciOntologyException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
    this.msg = msg;
  }
}
