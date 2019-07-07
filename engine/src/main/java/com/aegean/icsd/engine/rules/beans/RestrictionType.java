package com.aegean.icsd.engine.rules.beans;

import org.apache.commons.lang3.StringUtils;

public enum RestrictionType {

  ONLY("only", 1),
  VALUE("value", 2),
  EXACTLY("exactly", 3),
  MIN("min",4),
  MAX("max",5),
  SOME("some", 6);

  private String name;
  private int order;

  RestrictionType(String name, int order) {
    this.name = name;
    this.order = order;
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

  public int getOrder() {
    return this.order;
  }
}
