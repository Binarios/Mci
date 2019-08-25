package com.aegean.icsd.engine.rules.beans;

import java.util.List;

public class EntityProperty {
  /**
   * The name of the property
   */
  private String name;

  /**
   * The name of the parent property
   */
  private String parent;

  /**
   * The name of the inverse property
   */
  private String inverse;

  /**
   * The type of the values this property is associated with
   */
  private String range;

  /**
   * The possible values this property might have (applicable on DataTypeProperty)
   */
  private List<String> enumerations;

  /**
   * The type of the property. Either ObjectProperty or DataTypeProperty
   */
  private boolean objectProperty;

  /**
   * Is this property mandatory
   */
  private boolean mandatory;

  /**
   * Is it symmetric
   */
  private boolean symmetric;

  /**
   * Is it reflexive
   */
  private boolean reflexive;

  /**
   * Is it irreflexive
   */
  private boolean irreflexive;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRange() {
    return range;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public boolean isMandatory() {
    return mandatory;
  }

  public void setMandatory(boolean mandatory) {
    this.mandatory = mandatory;
  }

  public boolean isSymmetric() {
    return symmetric;
  }

  public void setSymmetric(boolean symmetric) {
    this.symmetric = symmetric;
  }

  public boolean isReflexive() {
    return reflexive;
  }

  public void setReflexive(boolean reflexive) {
    this.reflexive = reflexive;
  }

  public boolean isIrreflexive() {
    return irreflexive;
  }

  public void setIrreflexive(boolean irreflexive) {
    this.irreflexive = irreflexive;
  }

  public boolean isObjectProperty() {
    return objectProperty;
  }

  public void setObjectProperty(boolean objectProperty) {
    this.objectProperty = objectProperty;
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  public String getInverse() {
    return inverse;
  }

  public void setInverse(String inverse) {
    this.inverse = inverse;
  }

  public List<String> getEnumerations() {
    return enumerations;
  }

  public void setEnumerations(List<String> enumerations) {
    this.enumerations = enumerations;
  }
}
