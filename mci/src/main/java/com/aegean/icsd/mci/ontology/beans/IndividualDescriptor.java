package com.aegean.icsd.mci.ontology.beans;


import java.util.List;

public class IndividualDescriptor {

  private String id;
  private String individualName;
  private String individualClass;
  private List<IndividualDescriptor> parents;
  private List<PropertyDescriptor> restrictions;
  private List<PropertyDescriptor> properties;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getIndividualName() {
    return individualName;
  }

  public void setIndividualName(String individualName) {
    this.individualName = individualName;
  }

  public List<PropertyDescriptor> getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(List<PropertyDescriptor> restrictions) {
    this.restrictions = restrictions;
  }

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

  public List<IndividualDescriptor> getParents() {
    return parents;
  }

  public void setParents(List<IndividualDescriptor> parents) {
    this.parents = parents;
  }
}
