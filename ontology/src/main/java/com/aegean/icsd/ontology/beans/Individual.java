package com.aegean.icsd.ontology.beans;

import java.util.List;
import java.util.UUID;

public class Individual {
  private UUID id;
  private String className;
  private List<IndividualProperty> properties;
  private List<IndividualRestriction> restrictions;
  private List<IndividualRestriction> equalityRestrictions;

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public List<IndividualProperty> getProperties() {
    return this.properties;
  }

  public void setProperties(List<IndividualProperty> properties) {
    this.properties =  properties;
  }

  public List<IndividualRestriction> getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(List<IndividualRestriction> restrictions) {
    this.restrictions = restrictions;
  }

  public List<IndividualRestriction> getEqualityRestrictions() {
    return this.equalityRestrictions;
  }

  public void setEqualityRestrictions(List<IndividualRestriction> equalityRestrictions) {
    this.equalityRestrictions = equalityRestrictions;
  }
}
