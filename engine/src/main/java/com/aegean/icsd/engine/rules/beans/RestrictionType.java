package com.aegean.icsd.engine.rules.beans;

import org.apache.commons.lang3.StringUtils;

public enum RestrictionType {

  ONLY("only"),
  VALUE("value"),
  EXACTLY("exactly"),
  MIN("min"),
  MAX("max"),
  SOME("some");

  private String name;

  RestrictionType(String name) {
    this.name = name;
  }

  public static RestrictionType fromString(String name) {
    if (StringUtils.equalsIgnoreCase(MIN.getName(), name)) {
      return MIN;
    } else if (StringUtils.equalsIgnoreCase(MAX.getName(), name)) {
      return MAX;
    } else if (StringUtils.equalsIgnoreCase(EXACTLY.getName(), name)) {
      return EXACTLY;
    } else if (StringUtils.equalsIgnoreCase(ONLY.getName(), name)) {
      return ONLY;
    } else if (StringUtils.equalsIgnoreCase(SOME.getName(), name)) {
      return SOME;
    } else {
      return VALUE;
    }
  }

  public String getName() {
    return this.name;
  }
}
