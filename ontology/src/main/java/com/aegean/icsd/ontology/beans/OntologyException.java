package com.aegean.icsd.ontology.beans;

public class OntologyException extends Throwable {
  private String code;
  private String exceptionMessage;

  public OntologyException(String code, String exceptionMessage) {
    super(exceptionMessage);
    this.code = code;
    this.exceptionMessage = exceptionMessage;
  }

  public OntologyException(String code, String exceptionMessage, Throwable cause) {
    super(exceptionMessage, cause);
    this.code = code;
    this.exceptionMessage = exceptionMessage;
  }

  public String getCodeMsg() {
    return code + ": " + exceptionMessage;
  }
}
