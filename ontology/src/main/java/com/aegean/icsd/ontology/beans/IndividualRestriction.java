package com.aegean.icsd.ontology.beans;

public class IndividualRestriction {
  private IndividualProperty onIndividualProperty;
  private String type;
  private String exactValue;
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
