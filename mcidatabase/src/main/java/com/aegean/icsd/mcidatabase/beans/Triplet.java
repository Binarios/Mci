package com.aegean.icsd.mcidatabase.beans;

public class Triplet<T,U,K> {

  private T object;
  private U property;
  private K subject;

  public T getObject() {
    return object;
  }

  public void setObject(T object) {
    this.object = object;
  }

  public U getProperty() {
    return property;
  }

  public void setProperty(U property) {
    this.property = property;
  }

  public K getSubject() {
    return subject;
  }

  public void setSubject(K subject) {
    this.subject = subject;
  }
}
