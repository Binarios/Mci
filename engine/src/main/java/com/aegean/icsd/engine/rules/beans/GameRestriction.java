package com.aegean.icsd.engine.rules.beans;

import java.util.List;

public class GameRestriction {
  /**
   * The name of the property the restriction is on
   */
  private String onProperty;

  /**
   * The type of the values this property is associated with
   */
  private String range;

  /**
   * The type of restriction
   */
  private RestrictionType type;

  /**
   * The cardinality value
   */
  private int cardinality;

  /**
   * The acceptable range of values that must be given, if any
   */
  private List<ValueRangeRestriction> dataRange;

  public String getOnProperty() {
    return onProperty;
  }

  public void setOnProperty(String onProperty) {
    this.onProperty = onProperty;
  }

  public String getRange() {
    return range;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public RestrictionType getType() {
    return type;
  }

  public void setType(RestrictionType type) {
    this.type = type;
  }

  public int getCardinality() {
    return cardinality;
  }

  public void setCardinality(int cardinality) {
    this.cardinality = cardinality;
  }

  public List<ValueRangeRestriction> getDataRange() {
    return dataRange;
  }

  public void setDataRange(List<ValueRangeRestriction> dataRange) {
    this.dataRange = dataRange;
  }
}
