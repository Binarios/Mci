package com.aegean.icsd.engine.rules.beans;

import java.util.List;

public class EntityRules {
  /**
   * The entity name
   */
  private String name;

  /**
   * List with all the restriction for the game
   */
  private List<EntityRestriction> restrictions;

  /**
   * List of all properties associated with the game
   */
  private List<EntityProperty> properties;

  public List<EntityRestriction> getRestrictions() {
    return restrictions;
  }

  public void setRestrictions(List<EntityRestriction> restrictions) {
    this.restrictions = restrictions;
  }

  public List<EntityProperty> getProperties() {
    return properties;
  }

  public void setProperties(List<EntityProperty> properties) {
    this.properties = properties;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
