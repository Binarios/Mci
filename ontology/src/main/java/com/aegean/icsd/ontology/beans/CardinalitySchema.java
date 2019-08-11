package com.aegean.icsd.ontology.beans;

import java.util.List;

public class CardinalitySchema {

  private String occurrence;
  private List<DataRangeRestrinctionSchema> dataRangeRestrictions;

  public String getOccurrence() {
    return occurrence;
  }

  public void setOccurrence(String occurrence) {
    this.occurrence = occurrence;
  }

  public List<DataRangeRestrinctionSchema> getDataRangeRestrictions() {
    return dataRangeRestrictions;
  }

  public void setDataRangeRestrictions(List<DataRangeRestrinctionSchema> dataRangeRestrictions) {
    this.dataRangeRestrictions = dataRangeRestrictions;
  }
}
