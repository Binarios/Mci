package com.aegean.icsd.ontology.beans;

import java.util.List;

public class PropertySchema {
  /**
   * The name of the property
   */
  private String name;

  /**
   * The name of the parent property
   */
  private String parent;

  /**
   * The inverse property
   */
  private String inverse;
  /**
   * The type of the property. Either ObjectProperty or DataTypeProperty
   */
  private boolean objectProperty;

  /**
   * The class name that is the range of the property values
   */
  private String range;

  /**
   * The enumerated values, if the range is an enumeration,. This can be applied to DataProperties only.
   */
  private List<String> enumerations;

  /**
   * If this property marked as functional
   */
  private boolean mandatory;

  /**
   * If this property marked as symmetric
   */
  private boolean symmetric;

  /**
   * If this property marked as reflexive
   */
  private boolean reflexive;

  /**
   * If this property marked as irreflexive
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
