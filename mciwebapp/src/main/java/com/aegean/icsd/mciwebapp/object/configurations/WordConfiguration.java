package com.aegean.icsd.mciwebapp.object.configurations;

public class WordConfiguration {
  private String location;
  private String filename;
  private String delimiter;
  private int valueIndex;


  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter(String delimiter) {
    this.delimiter = delimiter;
  }

  public int getValueIndex() {
    return valueIndex;
  }

  public void setValueIndex(int valueIndex) {
    this.valueIndex = valueIndex;
  }

  public String getFilename() {
    return filename;
  }

  public void setFilename(String filename) {
    this.filename = filename;
  }
}
