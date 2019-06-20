package com.aegean.icsd.mci.generator.beans;

public class PropertyDescriptor {
  private String name;
  private PropertyType type;
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
