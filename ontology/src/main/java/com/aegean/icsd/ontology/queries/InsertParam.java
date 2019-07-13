package com.aegean.icsd.ontology.queries;

public class InsertParam {
  private String name;
  private String value;
  private boolean iriParam;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isIriParam() {
    return iriParam;
  }

  public void setIriParam(boolean iriParam) {
    this.iriParam = iriParam;
  }
}
