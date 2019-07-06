package com.aegean.icsd.engine.rules.beans;

public class RestrictionCardinality {
  /**
   * The type of cardinality
   */
  private CardinalityType type;

  /**
   * The cardinality value
   */
  private int value;

  public CardinalityType getType() {
    return type;
  }

  public void setType(CardinalityType type) {
    this.type = type;
  }

  public int getValue() {
    return value;
  }

  public void setValue(int value) {
    this.value = value;
  }
}
