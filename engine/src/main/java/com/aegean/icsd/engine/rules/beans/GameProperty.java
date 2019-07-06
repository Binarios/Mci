package com.aegean.icsd.engine.rules.beans;

public class GameProperty {
  /**
   * The name of the property
   */
  private String name;

  /**
   * The type of the values this property is associated with
   */
  private String range;

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
}
