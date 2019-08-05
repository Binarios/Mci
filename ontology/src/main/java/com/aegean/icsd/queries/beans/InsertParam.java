package com.aegean.icsd.queries.beans;

import java.util.UUID;

public class InsertParam {
  private String name;
  private Object value;
  private Class<?> valueClass;
  private boolean iriParam;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public boolean isIriParam() {
    return iriParam;
  }

  public void setIriParam(boolean iriParam) {
    this.iriParam = iriParam;
  }

  public Class<?> getValueClass() {
    return valueClass;
  }

  public void setValueClass(Class<?> valueClass) {
    this.valueClass = valueClass;
  }
  public static InsertParam createObj(String value) {
    return InsertParam.create(UUID.randomUUID().toString(), value, true, value.getClass());
  }

  public static <T> InsertParam createValue(Object value, Class<T> rangeClass) {
    return InsertParam.create(UUID.randomUUID().toString(), value, false, rangeClass);
  }

  public static <T> InsertParam create(String name, Object value, boolean isIri, Class<T> rangeClass) {
    InsertParam param = new InsertParam();
    param.setName(name);
    param.setValue(value);
    param.setIriParam(isIri);
    param.setValueClass(rangeClass);
    return param;
  }
}
