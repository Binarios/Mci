package com.aegean.icsd.mci.ontology.beans;

public class PropertyDescriptor {
  private String name;
  private PropertyType type;
  private int cardinalityMin;
  private int cardinalityMax;
  private IndividualDescriptor rangeIndividual;
  private String rangeValue;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PropertyType getType() {
    return type;
  }

  public void setType(PropertyType type) {
    this.type = type;
  }

  public int getCardinalityMin() {
    return cardinalityMin;
  }

  public void setCardinalityMin(int cardinalityMin) {
    this.cardinalityMin = cardinalityMin;
  }

  public int getCardinalityMax() {
    return cardinalityMax;
  }

  public void setCardinalityMax(int cardinalityMax) {
    this.cardinalityMax = cardinalityMax;
  }

  public IndividualDescriptor getRangeIndividual() {
    return rangeIndividual;
  }

  public void setRangeIndividual(IndividualDescriptor rangeIndividual) {
    this.rangeIndividual = rangeIndividual;
  }

  public String getRangeValue() {
    return rangeValue;
  }

  public void setRangeValue(String rangeValue) {
    this.rangeValue = rangeValue;
  }
}
