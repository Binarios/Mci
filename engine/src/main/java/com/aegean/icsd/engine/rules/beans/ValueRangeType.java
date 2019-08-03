package com.aegean.icsd.engine.rules.beans;

import org.apache.commons.lang3.StringUtils;

public enum ValueRangeType {
  EQUALS("equals"),
  MIN("min"),
  MIN_IN("minInclusive"),
  MAX("max"),
  MAX_IN("maxInclusive");


  private String name;

  ValueRangeType(String name) {
    this.name = name;
  }

  public static ValueRangeType fromString(String name) {
    if (StringUtils.equalsIgnoreCase(MIN.getName(), name)) {
      return MIN;
    } else if (StringUtils.equalsIgnoreCase(MAX.getName(), name)) {
      return MAX;
    } else if (StringUtils.equalsIgnoreCase(MIN_IN.getName(), name)) {
      return MIN_IN;
    } else if (StringUtils.equalsIgnoreCase(MAX_IN.getName(), name)) {
      return MAX_IN;
    } else {
      return EQUALS;
    }
  }

  public String getName() {
    return this.name;
  }
}
