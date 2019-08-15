package com.aegean.icsd.mciwebapp.object.configurations;

public class WordConfiguration {
  private String location;
  private String filename;
  private String delimiter;
  private String antonymDelimiter;
  private String synonymDelimiter;
  private int valueIndex;
  private int synonymIndex;
  private int antonymIndex;


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

  public int getSynonymIndex() {
    return synonymIndex;
  }

  public void setSynonymIndex(int synonymIndex) {
    this.synonymIndex = synonymIndex;
  }

  public int getAntonymIndex() {
    return antonymIndex;
  }

  public void setAntonymIndex(int antonymIndex) {
    this.antonymIndex = antonymIndex;
  }

  public String getAntonymDelimiter() {
    return antonymDelimiter;
  }

  public void setAntonymDelimiter(String antonymDelimiter) {
    this.antonymDelimiter = antonymDelimiter;
  }

  public String getSynonymDelimiter() {
    return synonymDelimiter;
  }

  public void setSynonymDelimiter(String synonymDelimiter) {
    this.synonymDelimiter = synonymDelimiter;
  }
}
