package com.aegean.icsd.ontology.beans;

import java.util.List;
import java.util.UUID;

public class ClassSchema {
  private UUID id;
  private String className;
  private List<PropertySchema> properties;
  private List<RestrictionSchema> restrictions;
  private List<RestrictionSchema> equalityRestrictions;

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

  public List<PropertySchema> getProperties() {
    return this.properties;
  }

  public void setProperties(List<PropertySchema> properties) {
    this.properties =  properties;
  }

  public List<RestrictionSchema> getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(List<RestrictionSchema> restrictions) {
    this.restrictions = restrictions;
  }

  public List<RestrictionSchema> getEqualityRestrictions() {
    return this.equalityRestrictions;
  }

  public void setEqualityRestrictions(List<RestrictionSchema> equalityRestrictions) {
    this.equalityRestrictions = equalityRestrictions;
  }
}
