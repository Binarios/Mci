package com.aegean.icsd.ontology;

public class OntologyException extends Throwable {
  private String code;
  private String msg;

  public OntologyException(String code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public OntologyException(String code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
    this.msg = msg;
  }
}
