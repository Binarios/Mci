package com.aegean.icsd.engine.rules.beans;

import java.util.List;

public class ValueRangeRestriction {
  private List<ValueRange> ranges;
  private String dataType;

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public List<ValueRange> getRanges() {
    return ranges;
  }

  public void setRanges(List<ValueRange> ranges) {
    this.ranges = ranges;
  }
}
