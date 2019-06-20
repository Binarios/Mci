package com.aegean.icsd.mci.generator.beans;


import java.util.List;

public class IndividualDescriptor {

  private String individualName;
  private String individualClass;
  private List<PropertyDescriptor> properties;


  public List<PropertyDescriptor> getProperties() {
    return properties;
  }

  public void setProperties(List<PropertyDescriptor> properties) {
    this.properties = properties;
  }

  public String getIndividualClass() {
    return individualClass;
  }

  public void setIndividualClass(String individualClass) {
    this.individualClass = individualClass;
  }

  public String getIndividualName() {
    return individualName;
  }

  public void setIndividualName(String individualName) {
    this.individualName = individualName;
  }
}
