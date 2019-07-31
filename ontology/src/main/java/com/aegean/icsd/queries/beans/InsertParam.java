package com.aegean.icsd.queries.beans;

import java.util.UUID;

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

  public static InsertParam createObj(String value) {
    return InsertParam.create(UUID.randomUUID().toString(), value, true);
  }

  public static InsertParam createValue(String value) {
    return InsertParam.create(UUID.randomUUID().toString(), value, false);
  }

  public static InsertParam create(String name, String value, boolean isIri) {
    InsertParam param = new InsertParam();
    param.setName(name);
    param.setValue(value);
    param.setIriParam(isIri);
    return param;
  }
}
