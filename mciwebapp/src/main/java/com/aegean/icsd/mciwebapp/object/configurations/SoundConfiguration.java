package com.aegean.icsd.mciwebapp.object.configurations;

public class SoundConfiguration {
  private String location;
  private String filename;
  private String delimiter;
  private int urlIndex;
  private int subjectIndex;

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public int getUrlIndex() {
    return urlIndex;
  }

  public void setUrlIndex(int urlIndex) {
    this.urlIndex = urlIndex;
  }

  public int getSubjectIndex() {
    return subjectIndex;
  }

  public void setSubjectIndex(int subjectIndex) {
    this.subjectIndex = subjectIndex;
  }
}
