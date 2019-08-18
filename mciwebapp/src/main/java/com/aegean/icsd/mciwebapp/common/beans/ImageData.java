package com.aegean.icsd.mciwebapp.common.beans;

public class ImageData {
  private String id;
  private String path;
  private Integer order;
  private Boolean start;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public Boolean isStart() {
    return start;
  }

  public void setStart(Boolean start) {
    this.start = start;
  }
}
