package com.aegean.icsd.mciwebapp.object.beans;

public class WordCriteria {
  private String forEntity;
  private String value;
  private Integer length;

  public String getForEntity() {
    return forEntity;
  }

  public void setForEntity(String forEntity) {
    this.forEntity = forEntity;
  }

  public Integer getLength() {
    return length;
  }

  public void setLength(Integer length) {
    this.length = length;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
