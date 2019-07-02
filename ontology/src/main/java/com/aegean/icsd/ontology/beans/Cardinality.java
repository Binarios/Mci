package com.aegean.icsd.ontology.beans;

import java.util.List;

public class Cardinality {
  private String type;
  private String occurrence;
  private List<DataRangeRestrinction> dataRangeRestrictions;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getOccurrence() {
    return occurrence;
  }

  public void setOccurrence(String occurrence) {
    this.occurrence = occurrence;
  }

  public List<DataRangeRestrinction> getDataRangeRestrictions() {
    return dataRangeRestrictions;
  }

  public void setDataRangeRestrictions(List<DataRangeRestrinction> dataRangeRestrictions) {
    this.dataRangeRestrictions = dataRangeRestrictions;
  }
}
