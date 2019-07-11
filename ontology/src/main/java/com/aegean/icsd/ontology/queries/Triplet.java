package com.aegean.icsd.ontology.queries;

public class Triplet {
  private String subject;
  private String predicate;
  private String object;
  private boolean literalObject;

  public Triplet () {

  }

  public Triplet (String subject, String predicate, String object) {
    this(subject, predicate, object, false);
  }

  public Triplet (String subject, String predicate, String object, boolean literalObject) {
    this.subject = subject;
    this.predicate = predicate;
    this.object = object;
    this.literalObject = literalObject;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getPredicate() {
    return predicate;
  }

  public void setPredicate(String predicate) {
    this.predicate = predicate;
  }

  public String getObject() {
    return object;
  }

  public void setObject(String object) {
    this.object = object;
  }

  public boolean isLiteralObject() {
    return literalObject;
  }

  public void setLiteralObject(boolean literalObject) {
    this.literalObject = literalObject;
  }
}
