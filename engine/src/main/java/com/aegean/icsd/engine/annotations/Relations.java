package com.aegean.icsd.engine.annotations;

public enum Relations {

  HAS_ID("hasId"),
  HAS_STRING_VALUE("hasStringValue"),
  HAS_SYNONYM("hasSynonym"),
  HAS_ANTONYM("hasAntonym");


  private String name;

  Relations(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
