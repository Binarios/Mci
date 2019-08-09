package com.aegean.icsd.mciwebapp.object.configurations;

import java.util.List;

public class ImageConfiguration {
  private String location;
  private String delimiter;
  private int urlIndex;
  private int titleIndex;
  private int subjectIndex;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String descriptionDelimiter) {
    this.delimiter = descriptionDelimiter;
  }

  public int getUrlIndex() {
    return urlIndex;
  }

  public void setUrlIndex(int urlIndex) {
    this.urlIndex = urlIndex;
  }

  public int getTitleIndex() {
    return titleIndex;
  }

  public void setTitleIndex(int titleIndex) {
    this.titleIndex = titleIndex;
  }

  public int getSubjectIndex() {
    return subjectIndex;
  }

  public void setSubjectIndex(int subjectIndex) {
    this.subjectIndex = subjectIndex;
  }
}
