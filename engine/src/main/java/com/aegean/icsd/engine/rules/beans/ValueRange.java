package com.aegean.icsd.engine.rules.beans;

public class ValueRange {
  private ValueRangeType predicate;
  private String value;

  public ValueRangeType getPredicate() {
    return predicate;
  }

  public void setPredicate(ValueRangeType predicate) {
    this.predicate = predicate;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
