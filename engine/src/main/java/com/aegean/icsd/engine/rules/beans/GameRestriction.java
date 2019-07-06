package com.aegean.icsd.engine.rules.beans;

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
   * The priority order of the restriction
   */
  private int order;

  /**
   * The cardinality of the restriction
   */
  private RestrictionCardinality restrictionCardinality;

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

  public int getOrder() {
    return order;
  }

  public void setOrder(int order) {
    this.order = order;
  }

  public RestrictionCardinality getRestrictionCardinality() {
    return restrictionCardinality;
  }

  public void setRestrictionCardinality(RestrictionCardinality restrictionCardinality) {
    this.restrictionCardinality = restrictionCardinality;
  }
}
