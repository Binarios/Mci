package com.aegean.icsd.ontology.beans;

public class IndividualRestriction {
  public static String EXACTLY_TYPE = "exactly";
  public static String MIN_TYPE = "min";
  public static String MAX_TYPE = "max";
  public static String ONLY_TYPE = "only";
  public static String SOME_TYPE = "some";
  public static String VALUE_TYPE = "value";

  /**
   * The property that this restriction is associated with
   */
  private IndividualProperty onIndividualProperty;

  /**
   * The type of the association. For example is it a given value, is it min/max
   */
  private String type;

  /**
   * if the type is exact, then this is the value
   */
  private String exactValue;

  /**
   * if the type has a cardinality (min, max, exact) then it is described here
   */
  private Cardinality cardinality;

  public IndividualProperty getOnIndividualProperty() {
    return onIndividualProperty;
  }

  public void setOnIndividualProperty(IndividualProperty onIndividualProperty) {
    this.onIndividualProperty = onIndividualProperty;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Cardinality getCardinality() {
    return cardinality;
  }

  public void setCardinality(Cardinality cardinality) {
    this.cardinality = cardinality;
  }

  public String getExactValue() {
    return exactValue;
  }

  public void setExactValue(String exactValue) {
    this.exactValue = exactValue;
  }
}
