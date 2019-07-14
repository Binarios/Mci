package com.aegean.icsd.engine.generator.beans;

public class GameEntity {
  private String className;
  private int cardinality;

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public int getCardinality() {
    return cardinality;
  }

  public void setCardinality(int cardinality) {
    this.cardinality = cardinality;
  }
}
