package com.aegean.icsd.mci.generator.beans;

public class DatasetProperties {
  private String ontologyLocation;
  private String ontologyName;
  private String namespace;
  private String prefix;
  private String datasetLocation;
  private String ontologyType;

  public String getOntologyLocation() {
    return ontologyLocation;
  }

  public void setOntologyLocation(String location) {
    this.ontologyLocation = location;
  }

  public String getNamespace() {
    return namespace;
  }

  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  public String getPrefix() {
    return prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getDatasetLocation() {
    return datasetLocation;
  }

  public void setDatasetLocation(String datasetLocation) {
    this.datasetLocation = datasetLocation;
  }

  public String getOntologyName() {
    return ontologyName;
  }

  public void setOntologyName(String ontologyName) {
    this.ontologyName = ontologyName;
  }

  public String getOntologyType() {
    return ontologyType;
  }

  public void setOntologyType(String ontologyType) {
    this.ontologyType = ontologyType;
  }
}
